package com.flt.applooukpprovider;

import android.Manifest;
import android.app.Application;

import com.flt.liblookupprovider.LibLookup;

public class LookupProviderApp extends Application {

  public static String[] permissions_required = new String[] {
      Manifest.permission.READ_EXTERNAL_STORAGE,
//      Manifest.permission.INTERNET,
//      Manifest.permission.WAKE_LOCK,
//      Manifest.permission.ACCESS_NETWORK_STATE,
//      Manifest.permission.ACCESS_WIFI_STATE,
//      Manifest.permission.WRITE_EXTERNAL_STORAGE,
//      "com.android.vending.CHECK_LICENSE",
  };

}
