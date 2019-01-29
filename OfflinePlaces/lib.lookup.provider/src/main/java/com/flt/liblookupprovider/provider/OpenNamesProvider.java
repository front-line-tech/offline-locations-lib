package com.flt.liblookupprovider.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.flt.liblookupprovider.LibLookup;
import com.flt.liblookupprovider.R;
import com.flt.liblookupclient.events.WeakEventProvider;

import org.apache.commons.lang3.StringUtils;

import static com.flt.liblookupclient.OpenNamesContent.authority;
import static com.flt.liblookupclient.OpenNamesContent.mime_place;
import static com.flt.liblookupclient.OpenNamesContent.path_search;
import static com.flt.liblookupclient.OpenNamesContent.selection_id;
import static com.flt.liblookupclient.OpenNamesContent.selection_name;
import static com.flt.liblookupclient.OpenNamesContent.selection_row;

public class OpenNamesProvider extends ContentProvider {
  private static final String TAG = OpenNamesProvider.class.getSimpleName();

  private static final int code_places_search = 1;

  // Creates a UriMatcher object.
  private UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

  private LibLookup lib;
  private LibLookup.State lib_state;
  private int min_length_partial_search;

  public OpenNamesProvider() { }

  @Override
  public boolean onCreate() {
    Context context = getContext();
    uriMatcher.addURI(authority(context), path_search, code_places_search);
    min_length_partial_search = context.getResources().getInteger(R.integer.min_length_partial_search);
    lib = LibLookup.getInstance(context);
    lib.getLibStateEventProvider().addListener(lib_listener);
    return lib.hasExtracted();
  }

  private WeakEventProvider.Listener<LibLookup.StateChangeEvent> lib_listener = new WeakEventProvider.Listener<LibLookup.StateChangeEvent>() {
    @Override
    protected boolean parseEvent(LibLookup.StateChangeEvent event) {
      lib_state = event.state;
      return false;
    }
  };

  @Override
  public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    try {
      if (lib_state.canServeData()) {
        switch (uriMatcher.match(uri)) {
          case code_places_search:
            if (StringUtils.equalsIgnoreCase(selection_row, selection)) {
              long row_id = Long.parseLong(selectionArgs[0]);
              Cursor c = lib.getDb().getPlacesDao().get_item_cursor(row_id);
              return c;
            }
            if (StringUtils.equalsIgnoreCase(selection_id, selection)) {
              String place_id = selectionArgs[0];
              return lib.getDb().getPlacesDao().get_item_cursor(place_id);
            }
            if (StringUtils.equalsIgnoreCase(selection_name, selection)) {
              String partial_name = selectionArgs[0];
              if (satisfactorySearchTermName(partial_name)) {
                String partial_name_search = partial_name;
                if (!partial_name_search.startsWith("%")) { partial_name_search = "%" + partial_name_search; }
                if (!partial_name_search.endsWith("%")) { partial_name_search = partial_name_search + "%"; }
                return lib.getDb().getPlacesDao().get_simple_like_cursor(partial_name_search);
              }
            }
            break;
        }
      }
      return null; // no cursor for you - either not a matching Uri, or fell through on selection

    } catch (Exception e) {
      Log.e(TAG, "Exception encountered during Query.", e);
      throw e;
    }
  }

  public boolean satisfactorySearchTermName(String partial_name) {
    return StringUtils.isNotBlank(partial_name) && partial_name.length() >= min_length_partial_search;
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    return 0; // don't delete for content provider
  }

  @Override
  public String getType(Uri uri) {
    switch (uriMatcher.match(uri)) {
      case code_places_search:
        return mime_place;
      default:
        return null;
    }
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    return null; // don't insert for content provider
  }

 @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    return 0; // don't update for content provider
  }
}
