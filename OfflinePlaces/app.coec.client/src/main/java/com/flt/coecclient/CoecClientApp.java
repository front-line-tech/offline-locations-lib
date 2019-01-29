package com.flt.coecclient;

import android.Manifest;
import android.app.Application;
import android.content.Intent;

import com.flt.coecclient.service.CoecService;

public class CoecClientApp extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Intent i = new Intent(this, CoecService.class);
    startService(i);
  }

  public static String[] permissions_required = new String[] {
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.RECEIVE_BOOT_COMPLETED
  };

}
