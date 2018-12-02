package com.flt.coecclient.service;

import android.content.SharedPreferences;

import com.flt.coecclient.CoecClientApp;
import com.flt.coecclient.R;
import com.flt.coecclient.ui.MapsActivity;
import com.flt.servicelib.AbstractBackgroundBindingService;
import com.flt.servicelib.BackgroundServiceConfig;

public class CoecService extends AbstractBackgroundBindingService<ICoecService> {

  @Override
  public void onCreate() {
    super.onCreate();

    // TODO

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
}
