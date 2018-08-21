package com.flt.libgeo;

import android.util.Pair;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

import uk.me.jstott.jcoord.OSRef;


public class GeoConverter {

  public static String FORM_OF_WAY_one_way_road = "Single Carriageway";

  public static List<LatitudeLongitude> osMultilineString_to_LatLngs(String geometry) {
    List<LatitudeLongitude> points = new LinkedList<>();

    if (StringUtils.isEmpty(geometry)) {
      return points;
    }
    if (!StringUtils.startsWithIgnoreCase(geometry, "MULTILINESTRING")) {
      return points;
    }

    String line_trimmed = StringUtils.substring(geometry, "MULTILINESTRING".length()).trim();
    String[] parts = StringUtils.split(line_trimmed, ", ()[]{}\n");

    for (int i = 0; i < parts.length; i += 2) {
      Pair<Double,Double> pair =
          osEastNorth_to_latLng(
              Double.parseDouble(parts[i + 0]),
              Double.parseDouble(parts[i + 1]));

      points.add(new LatitudeLongitude(pair.first, pair.second));
    }

    return points;
  }

  public static LatitudeLongitude osPoint_to_LatLng(String geometry) {
    if (StringUtils.isEmpty(geometry)) {
      return null;
    }
    if (!StringUtils.startsWithIgnoreCase(geometry, "POINT")) {
      return null;
    }

    String line_trimmed = StringUtils.substring(geometry, "POINT".length()).trim();
    String[] parts = StringUtils.split(line_trimmed, " ()[]{}");

    return osEastNorth_to_latLng(
        Double.parseDouble(parts[0]),
        Double.parseDouble(parts[1]));
  }

  public static LatitudeLongitude osEastNorth_to_latLng(double east, double north) {
    OSRef ref = new OSRef(east, north);
    uk.me.jstott.jcoord.LatLng latLng = ref.toLatLng();
    latLng.toWGS84();
    return new LatitudeLongitude(latLng.getLatitude(), latLng.getLongitude());
  }

  public static EastingNorthing latLng_to_osEastNorth(LatLng latLng) {
    uk.me.jstott.jcoord.LatLng jcoord_latLng =
        new uk.me.jstott.jcoord.LatLng(
            latLng.getLatitude(),
            latLng.getLongitude(),
            latLng.getAltitude());

    // convert back from WGS84? apparently not!
    OSRef ref = jcoord_latLng.toOSRef();
    return new EastingNorthing(ref.getEasting(), ref.getNorthing());
  }

}
