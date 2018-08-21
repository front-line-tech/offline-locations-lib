package com.flt.liblookupclient.entities;

import android.database.Cursor;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public class OpenNamesPlace {
  private static final String TAG = OpenNamesPlace.class.getSimpleName();

  public String ID;
  public String NAMES_URI;
  public String NAME1;
  public String NAME1_LANG;
  public String NAME2;
  public String NAME2_LANG;
  public String TYPE;
  public String LOCAL_TYPE;
  public String GEOMETRY_X;
  public String GEOMETRY_Y;
  public String MOST_DETAIL_VIEW_RES;
  public String LEAST_DETAIL_VIEW_RES;
  public String MBR_XMIN;
  public String MBR_YMIN;
  public String MBR_XMAX;
  public String MBR_YMAX;
  public String POSTCODE_DISTRICT;
  public String POSTCODE_DISTRICT_URI;
  public String POPULATED_PLACE;
  public String POPULATED_PLACE_URI;
  public String POPULATED_PLACE_TYPE;
  public String DISTRICT_BOROUGH;
  public String DISTRICT_BOROUGH_URI;
  public String DISTRICT_BOROUGH_TYPE;
  public String COUNTY_UNITARY;
  public String COUNTY_UNITARY_URI;
  public String COUNTY_UNITARY_TYPE;
  public String REGION;
  public String REGION_URI;
  public String COUNTRY;
  public String COUNTRY_URI;
  public String RELATED_SPATIAL_OBJECT;
  public String SAME_AS_DBPEDIA;
  public String SAME_AS_GEONAMES;

  public boolean exists() {
    return StringUtils.isNotBlank(ID);
  }

  public static OpenNamesPlace fromCursorRow(Cursor cursor) {
    OpenNamesPlace place = new OpenNamesPlace();
    Field[] fields = OpenNamesPlace.class.getDeclaredFields();
    for (Field field : fields) {
      field.setAccessible(true);
      String fieldName = field.getName();
      int col = cursor.getColumnIndex(fieldName);
      if (col != -1) {
        String value = cursor.getString(col);
        try {
          field.set(place, value);
        } catch (IllegalAccessException e) {
          Log.e(TAG, "Unexpected IllegalAccessException for: " + field.getName(), e);
          throw new RuntimeException(e);
        }
      }
    }
    return place;
  }

}
