package com.flt.applooukpprovider;

import android.Manifest;
import android.app.Application;

import com.flt.liblookupprovider.LibLookup;

public class LookupProviderApp extends Application {

  public static String[] permissions_required = new String[] {
      Manifest.permission.READ_EXTERNAL_STORAGE
  };

}
