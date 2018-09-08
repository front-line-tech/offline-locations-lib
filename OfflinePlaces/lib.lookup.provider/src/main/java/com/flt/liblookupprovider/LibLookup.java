package com.flt.liblookupprovider;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import com.flt.liblookupprovider.db.OpenNamesDb;
import com.flt.liblookupprovider.extraction.ExtractionState;
import com.flt.liblookupprovider.extraction.OpenNamesDbCsvParser;
import com.flt.liblookupprovider.extraction.OpenNamesExtractor;
import com.flt.liblookupprovider.extraction.OpenNamesFileFinder;
import com.flt.libshared.events.WeakEventProvider;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class LibLookup {
  private static final String TAG = LibLookup.class.getSimpleName();
  private static final String db_name = "db_liblookup";
  private static final String prefs_name = "prefs_liblookup";

  private static final String PREF_extracted = "extraction.complete";
  private static final String PREF_extraction_file = "extraction.file";
  private static final String PREF_extraction_date = "extraction.date";

  private Context appContext;
  private OpenNamesDb db;

  private State state_lib;
  private ExtractionState state_extraction;

  private OpenNamesFileFinder finder;

  private WeakEventProvider<StateChangeEvent> provider_state;
  private WeakEventProvider<ExtractionState> provider_extraction_state;

  public enum State {
    Initialising(false, false, false, false),
    SourceUnavailable(false, true, true, false),
    PendingPermission(false, false, false, true),
    ReadyToExtract(false, true, false, false),
    DataExtracting(false, false, false, false),
    ErrorInExtraction(false, true, false, false),
    DataReady(true, false, false, false),
    ShutDown(false, false, false, false);

    private boolean can_serve_data;
    private boolean may_extract;
    private boolean need_download;
    private boolean need_permissions;

    State(boolean can_serve_data, boolean may_extract, boolean need_download, boolean need_permissions) {
      this.can_serve_data = can_serve_data;
      this.may_extract = may_extract;
      this.need_download = need_download;
      this.need_permissions = need_permissions;
    }

    public boolean canServeData() { return can_serve_data; }
    public boolean mayExtractData() { return may_extract; }
    public boolean needsDownload() { return need_download; }
    public boolean needsPermissions() { return need_permissions; }
  }

  private static LibLookup instance;

  public static LibLookup getInstance(Context context) {
    if (instance == null) {
      instance = new LibLookup(context);
    }
    return instance;
  }

  private LibLookup(Context context) {
    this.appContext = context.getApplicationContext();
    this.provider_state = new WeakEventProvider<>();
    this.provider_extraction_state = new WeakEventProvider<>();
    init();
  }

  private void init() {
    setState(State.Initialising);
    this.db = initDb();
    this.finder = new OpenNamesFileFinder(appContext);

    if (hasExtracted()) {
      setState(State.DataReady);
    } else {
      setState(State.ReadyToExtract);
    }
  }

  private void setState(State next) {
    if (next != state_lib) {
      Log.d(TAG, "Switching state: " + next.name());
      state_lib = next;
      notifyListener_state();
    }
  }

  private OpenNamesDb initDb() {
    return Room.databaseBuilder(appContext, OpenNamesDb.class, db_name)
        .fallbackToDestructiveMigration() // provide real migrations live when time comes
        .allowMainThreadQueries()
        .build();
  }

  public WeakEventProvider<ExtractionState> getExtractionStateEventProvider() {
    return provider_extraction_state;
  }

  public WeakEventProvider<StateChangeEvent> getLibStateEventProvider() {
    return provider_state;
  }

  public State getState() {
    return state_lib;
  }

  public boolean hasPermissionToExtract() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return appContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    } else {
      return true;
    }
  }

  public boolean isExtractionSourceFound() {
    try {
      return StringUtils.isNotBlank(finder.nameBestOpenNamesZipCandidate());
    } catch (Exception e) {
      Log.w(TAG, "Exception caught checking for any extractable source.", e);
      return false;
    }
  }

  public void doExtraction(boolean overwrite, boolean test_only) {
    if (hasExtracted() && !overwrite) {
      Log.d(TAG, "OpenNames data already extracted, set overwrite flag to delete and replace data.");
      return;
    }

    if (hasExtracted() && overwrite) {
      Log.i(TAG, "Overwrite flag set, deleting existing OpenNames data for replacement...");
      db.getPlacesDao().delete_all();
      unsetExtracted();
    }

    if (!isExtractionSourceFound()) {
      setState(State.SourceUnavailable);
      return;
    }

    try {
      InputStream stream_count = finder.getBestOpenNamesZipCandidate();
      if (stream_count != null) {
        Log.i(TAG, "Extracting OpenNames data...");
        setState(State.DataExtracting);

        OpenNamesExtractor extractor = new OpenNamesExtractor(appContext, extraction_listener);
        OpenNamesDbCsvParser parser = new OpenNamesDbCsvParser(db);

        String selected_asset = finder.nameBestOpenNamesZipCandidate();

        state_extraction = new ExtractionState(
            selected_asset,
            extractor.countRelevantFiles(stream_count, parser));

        stream_count.close();
        notifyListener_extraction();

        InputStream stream_read = finder.getBestOpenNamesZipCandidate();
        if (test_only) {
          extractor.doExtraction(stream_read, parser, 5);
        } else {
          extractor.doExtraction(stream_read, parser);
        }
        stream_read.close();
        setExtracted(selected_asset);

        state_extraction.setComplete();
        notifyListener_extraction();
      } else {
        Log.w(TAG, "Unable to extract OpenNames data.");
        setState(State.SourceUnavailable);
      }

    } catch (SecurityException ex) {
      Log.w(TAG, "Security Exception caught attempting to extract.", ex);
      setState(State.PendingPermission);
    } catch (Exception ex) {
      Log.w(TAG, "Unexpected Exception caught attempting to extract.", ex);
      setState(State.ErrorInExtraction);
    }

  }

  public OpenNamesDb getDb() {
    return db;
  }

  private void notifyListener_extraction() {
    provider_extraction_state.notifyListeners(state_extraction);
  }

  private void notifyListener_state() {
    provider_state.notifyListenersSticky(new StateChangeEvent(state_lib));
  }

  private OpenNamesExtractor.Listener extraction_listener = new OpenNamesExtractor.Listener() {
    @Override
    public void onOpenFile(String filename) {
      state_extraction.setCurrentFile(filename);
      notifyListener_extraction();
    }

    @Override
    public void onCompleteFile(int places_found) {
      state_extraction.completeCurrentFile(places_found);
      notifyListener_extraction();
    }
  };

  private void unsetExtracted() {
    SharedPreferences prefs = appContext.getSharedPreferences(prefs_name, MODE_PRIVATE);
    SharedPreferences.Editor edit = prefs.edit();
    edit.putBoolean(PREF_extracted, false);
    edit.remove(PREF_extraction_file);
    edit.remove(PREF_extraction_date);
    edit.commit();
    setState(State.ReadyToExtract);
  }

  private void setExtracted(String file) {
    SharedPreferences prefs = appContext.getSharedPreferences(prefs_name, MODE_PRIVATE);
    SharedPreferences.Editor edit = prefs.edit();
    edit.putBoolean(PREF_extracted, true);
    edit.putString(PREF_extraction_file, file);
    edit.putLong(PREF_extraction_date, new Date().getTime());
    edit.commit();
    Log.i(TAG, "Extraction marked complete.");
    setState(State.DataReady);
  }

  public boolean hasExtracted() {
    SharedPreferences prefs = appContext.getSharedPreferences(prefs_name, MODE_PRIVATE);
    boolean marked_extracted = prefs.getBoolean(PREF_extracted, false);
    if (marked_extracted) {
      boolean and_in_db = db.getPlacesDao().count() > 0;
      return and_in_db;
    } else {
      return false;
    }
  }

  public Date getCompletedExtractionDate() {
    SharedPreferences prefs = appContext.getSharedPreferences(prefs_name, MODE_PRIVATE);
    long ticks = prefs.getLong(PREF_extraction_file, 0);
    if (ticks > 0) {
      return new Date(ticks);
    } else {
      return null;
    }
  }

  public String getCompletedExtractionFile() {
    SharedPreferences prefs = appContext.getSharedPreferences(prefs_name, MODE_PRIVATE);
    String file = prefs.getString(PREF_extraction_file, null);
    return file;
  }

  public static class StateChangeEvent {
    public State state;
    public StateChangeEvent(State state) {
      this.state = state;
    }
  }
}
