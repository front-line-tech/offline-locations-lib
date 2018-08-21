package com.flt.liblookupprovider;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import com.flt.liblookupprovider.db.OpenNamesDb;
import com.flt.liblookupprovider.extraction.ExtractionState;
import com.flt.liblookupprovider.extraction.OpenNamesDbCsvParser;
import com.flt.liblookupprovider.extraction.OpenNamesExtractor;
import com.flt.libshared.events.WeakEventProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class LibLookup {
  private static final String TAG = LibLookup.class.getSimpleName();
  private static final String db_name = "db_liblookup";
  private static final String prefs_name = "prefs_liblookup";

  private static final String PREF_extracted = "extraction.complete";
  private static final String PREF_extraction_file = "extraction.file";


  private Context appContext;
  private OpenNamesDb db;

  private State state_lib;
  private ExtractionState state_extraction;

  private WeakEventProvider<StateChangeEvent> provider_state;
  private WeakEventProvider<ExtractionState> provider_extraction_state;

  public enum State {
    Initialising(false),
    DataUnavailable(false),
    DataExtracting(false),
    DataReady(true),
    Finished(false);

    private boolean can_serve_data;

    State(boolean can_serve_data) {
      this.can_serve_data = can_serve_data;
    }

    public boolean canServeData() { return can_serve_data; }
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
    if (hasExtracted()) {
      setState(State.DataReady);
    } else {
      setState(State.DataUnavailable);
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

  public void doExtraction(boolean overwrite, boolean test_only) throws IOException {
    if (hasExtracted() && !overwrite) {
      Log.d(TAG, "OpenNames data already extracted, set overwrite flag to delete and replace data.");
      return;
    }

    if (hasExtracted() && overwrite) {
      Log.i(TAG, "Overwrite flag set, deleting existing OpenNames data for replacement...");
      db.getPlacesDao().delete_all();
      unsetExtracted();
    }

    Log.i(TAG, "Extracting OpenNames data...");
    setState(State.DataExtracting);

    OpenNamesExtractor x = new OpenNamesExtractor(appContext, extraction_listener);
    OpenNamesDbCsvParser parser = new OpenNamesDbCsvParser(db);

    List<String> assets = x.findOpenNamesZipAssets();
    if (assets.size() > 0) {
      AssetManager mgr = appContext.getAssets();
      String selected_asset = assets.get(0);

      InputStream stream_count = mgr.open(selected_asset);
      state_extraction = new ExtractionState(selected_asset, x.countRelevantFiles(stream_count, parser));
      stream_count.close();
      notifyListener_extraction();

      InputStream stream_read = mgr.open(selected_asset);
      if (test_only) {
        x.doExtraction(stream_read, parser, 5);
      } else {
        x.doExtraction(stream_read, parser);
      }
      stream_read.close();
      setExtracted(selected_asset);

      state_extraction.setComplete();
      notifyListener_extraction();
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
    edit.commit();
    setState(State.DataUnavailable);
  }

  private void setExtracted(String file) {
    SharedPreferences prefs = appContext.getSharedPreferences(prefs_name, MODE_PRIVATE);
    SharedPreferences.Editor edit = prefs.edit();
    edit.putBoolean(PREF_extracted, true);
    edit.putString(PREF_extraction_file, file);
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
