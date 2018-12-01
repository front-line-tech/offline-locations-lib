package com.flt.coecclient;

import android.Manifest;
import android.app.Application;

public class CoecClientApp extends Application {

  public static String[] permissions_required = new String[] {
      Manifest.permission.ACCESS_FINE_LOCATION
  };

}
