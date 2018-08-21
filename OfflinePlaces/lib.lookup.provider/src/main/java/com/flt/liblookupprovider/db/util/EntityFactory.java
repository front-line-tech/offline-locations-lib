package com.flt.liblookupprovider.db.util;

import android.util.Log;

import com.flt.liblookupprovider.db.entities.Place;

import java.lang.reflect.Field;
import java.util.Date;

public class EntityFactory {
  private static final String TAG = EntityFactory.class.getSimpleName();

  public static Place createPlace() {
    Place p = new Place();
    p.created = new Date();
    return p;
  }

  public static Place createPlaceFromCSV(String[] line) {
    Place p = createPlace();

    for (PlaceCSV field : PlaceCSV.values()) {
      try {
        String value = line[field.getCol()];
        Field f = p.getClass().getField(field.name());

        f.setAccessible(true);
        f.set(p, value);

      } catch (IllegalAccessException ia) {
        Log.e(TAG, "Skipping field - illegal access " + field.name());
      } catch (NoSuchFieldException nsf) {
        Log.e(TAG, "Skipping field - not found on target: " + field.name());
      }
    }

    return p;
  }
}
