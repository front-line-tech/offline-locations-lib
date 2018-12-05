package com.flt.coecclient.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.flt.coecclient.CoecClientApp;
import com.flt.coecclient.R;
import com.flt.coecclient.ui.MapsActivity;
import com.flt.libcoecclient.db.CoecDatabase;
import com.flt.libcoecclient.db.entities.CoecMicroTasking;
import com.flt.libcoecclient.sampledata.SampleData;
import com.flt.servicelib.AbstractBackgroundBindingService;
import com.flt.servicelib.BackgroundServiceConfig;

import java.util.List;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import timber.log.Timber;

public class CoecService extends AbstractBackgroundBindingService<ICoecService> implements ICoecService {

  private String DB_NAME = "taskings";
  private CoecDatabase db;

  @Override
  public void onCreate() {
    super.onCreate();
    db = createDb();
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
            Executors.newSingleThreadScheduledExecutor().execute(() -> {
              List<CoecMicroTasking> sampleTasks = SampleData.createSampleTaskings();
              db.taskDao().insert_all(sampleTasks);
              Timber.i("Added " + sampleTasks.size() + " sample tasks.");
            });
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
  public LiveData<List<CoecMicroTasking>> getLiveTaskingsFor(double n, double w, double s, double e) {
    return db.taskDao().get_all(); // TODO: fix query later
    //return db.taskDao().get_incomplete_for_area(n,w,s,e);
  }
}
