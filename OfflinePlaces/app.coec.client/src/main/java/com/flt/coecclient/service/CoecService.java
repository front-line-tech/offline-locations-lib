package com.flt.coecclient.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.flt.coecclient.CoecClientApp;
import com.flt.coecclient.R;
import com.flt.coecclient.ui.MapsActivity;
import com.flt.coecclient.db.CoecDatabase;
import com.flt.coecclient.db.entities.CoecMicroTasking;
import com.flt.coecclient.sampledata.SampleData;
import com.flt.servicelib.AbstractBackgroundBindingService;
import com.flt.servicelib.BackgroundServiceConfig;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import timber.log.Timber;

public class CoecService extends AbstractBackgroundBindingService<ICoecService> implements ICoecService {

  public static final String ACTION_RECEIVE_TASK = "coec.ACTION_RECEIVE_TASK";
  public static final String EXTRA_KEY_INTENT_MICROTASK = "coec.MicroTask";

  public static Intent createIntentWithTasking(Context context, CoecMicroTasking tasking) {
    Intent i = new Intent(context, CoecService.class);
    i.setAction(ACTION_RECEIVE_TASK);
    i.putExtra(EXTRA_KEY_INTENT_MICROTASK, tasking);
    return i;
  }

  private String DB_NAME = "taskings";
  private CoecDatabase db;

  private Map<UUID, CoecMicroTasking> accepted_taskings;

  @Override
  public void onCreate() {
    super.onCreate();
    db = createDb();
    accepted_taskings = new LinkedHashMap<>();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent == null) {
      return super.onStartCommand(intent, flags, startId);
    }
    String action = intent.getAction();
    if (!StringUtils.isBlank(action)) {
      Timber.d("Received Intent with action: %s", action);

      if (action.equals(ACTION_RECEIVE_TASK)) {
        parseReceiveTaskIntent(intent);
        return START_STICKY;
      } else {
        return super.onStartCommand(intent, flags, startId);
      }
    } else {
      return super.onStartCommand(intent, flags, startId);
    }
  }

  private void parseReceiveTaskIntent(final Intent intent) {
    CoecMicroTasking tasking = intent.getExtras().getParcelable(EXTRA_KEY_INTENT_MICROTASK);
    addTaskings(tasking);
  }

  private CoecDatabase createDb() {
    return Room
        .inMemoryDatabaseBuilder(getApplicationContext(), CoecDatabase.class)
        .fallbackToDestructiveMigration()
        .allowMainThreadQueries()
        .addCallback(new RoomDatabase.Callback() {
          @Override
          public void onCreate(@NonNull SupportSQLiteDatabase sqlite) {
            super.onCreate(sqlite);
            Timber.i("Creating db for first time.");
          }
        })
        .build();
  }

  @Override
  protected void restoreFrom(SharedPreferences prefs) { }

  @Override
  protected void storeTo(SharedPreferences.Editor editor) { }

  @Override
  protected BackgroundServiceConfig configure(BackgroundServiceConfig defaults) {
    defaults.setNotification(
        getString(R.string.notification_title_coec_service),
        getString(R.string.notification_content_coec_service),
        getString(R.string.notification_ticker_coec_service),
        R.drawable.ic_new_releases_black_24dp,
        MapsActivity.class);

    return defaults;
  }

  @Override
  protected String[] getRequiredPermissions() {
    return CoecClientApp.permissions_required;
  }

  @Override
  public boolean acceptTasking(CoecMicroTasking tasking) {
    if (!accepted_taskings.containsKey(tasking.tasking_uuid)) {
      accepted_taskings.put(tasking.tasking_uuid, tasking);
      return true;
    } else {
      return false; // already accepted!
    }
  }

  @Override
  public int countAcceptedTaskings() {
    return accepted_taskings.size();
  }

  @Override
  public void addTaskings(final CoecMicroTasking... taskings) {
    Executors.newSingleThreadScheduledExecutor().execute(() -> {
      db.taskDao().insert_all(taskings);
    });
  }

  @Override
  public void addTaskings(final List<CoecMicroTasking> taskings) {
    Executors.newSingleThreadScheduledExecutor().execute(() -> {
      db.taskDao().insert_all(taskings);
    });
  }

  @Override
  public LiveData<List<CoecMicroTasking>> getLiveTaskingsFor(double n, double w, double s, double e) {
    //return db.taskDao().get_all(); // TODO: fix query later
    return db.taskDao().get_incomplete_for_area(n,w,s,e);
  }
}
