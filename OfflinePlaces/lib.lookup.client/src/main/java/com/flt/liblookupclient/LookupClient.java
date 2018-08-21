package com.flt.liblookupclient;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;

import com.flt.liblookupclient.entities.OpenNamesPlace;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class LookupClient {

  private Context appContext;
  private ContentResolver resolver;
  private int min_length_partial_search;

  public static Uri uri_base = Uri.parse("content://" + OpenNamesContent.authority);
  public static Uri uri_lookup = Uri.withAppendedPath(uri_base, OpenNamesContent.path_search);

  public LookupClient(Context context) {
    this.appContext = context.getApplicationContext();
    this.resolver = appContext.getContentResolver();
    this.min_length_partial_search = appContext.getResources().getInteger(R.integer.min_length_partial_search);
  }

  public static boolean providerPresentOnDevice(Context context) {
    String providerPackage = context.getString(R.string.provider_package);
    PackageManager pm = context.getPackageManager();
    List<PackageInfo> infos = pm.getInstalledPackages(0);
    for (PackageInfo info : infos) {
      if (StringUtils.equalsIgnoreCase(info.packageName, providerPackage)) {
        return true;
      }
    }
    return false;
  }

  public boolean validateNameSearch(String partial) {
    return StringUtils.isNotBlank(partial) && partial.length() >= min_length_partial_search;
  }

  public Collection<OpenNamesPlace> lookupName(String partial) {
    Cursor cursor = resolver.query(
        uri_lookup,
        null,
        OpenNamesContent.selection_name,
        new String[] { partial },
        null);

    return extractPlaces(cursor);
  }

  public OpenNamesPlace retrieve(String id) {
    Cursor cursor = resolver.query(
        uri_lookup,
        null,
        OpenNamesContent.selection_id,
        new String[] { id },
        null);

    return singleOrNull(extractPlaces(cursor));
  }

  public static List<OpenNamesPlace> extractPlaces(Cursor cursor) {
    List<OpenNamesPlace> found = new LinkedList<>();
    while (cursor.moveToNext()) {
      OpenNamesPlace place = OpenNamesPlace.fromCursorRow(cursor);
      if (place.exists()) { found.add(place); }
    }
    return found;
  }

  public static <T> T singleOrNull(List<T> list) {
    if (list.size() == 1) {
      return list.get(0);
    } else {
      return null;
    }
  }

}
