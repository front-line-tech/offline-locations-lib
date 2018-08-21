package com.flt.liblookupclient;

import android.content.Context;

public class OpenNamesContent {

  public static String authority(Context context) { return context.getString(R.string.liblookup_provider_authority); }

  public static String path_search = "places/search";
  public static final String selection_row = "row";
  public static final String selection_id = "id";
  public static final String selection_name = "name";

  public static final String mime_place = "os-data/open-name-place";

}
