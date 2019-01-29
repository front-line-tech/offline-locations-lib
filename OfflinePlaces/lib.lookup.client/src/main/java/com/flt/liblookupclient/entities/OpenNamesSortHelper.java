package com.flt.liblookupclient.entities;

import android.support.annotation.NonNull;

import com.flt.liblookupclient.geo.GeoConverter;
import com.flt.liblookupclient.geo.LatitudeLongitude;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.apache.commons.lang3.StringUtils;

import static com.flt.liblookupclient.entities.OpenNamesHelper.getPrimary;
import static com.flt.liblookupclient.entities.OpenNamesHelper.getX;
import static com.flt.liblookupclient.entities.OpenNamesHelper.getY;

public class OpenNamesSortHelper {

  public static int getCombinedOrder(String search, LatitudeLongitude origin, OpenNamesPlace p1, OpenNamesPlace p2) {
    int matchOrdering = getMatchQualityOrder(search, p1, p2);
    if (matchOrdering != 0) {
      return matchOrdering;
    } else {
      if (origin != null) {
        return getDistanceOrder(origin, p1, p2);
      } else {
        return getAlphabeticalOrder(p1, p2);
      }
    }
  }

  public static int getMatchQualityOrder(String search, OpenNamesPlace p1, OpenNamesPlace p2) {
    OpenNamesMatch match1 = determineMatch(search, p1);
    OpenNamesMatch match2 = determineMatch(search, p2);

    float ws1 = match1.getWeightedStrength();
    float ws2 = match2.getWeightedStrength();

    // poorer matches go to the BOTTOM of the list
    if (ws1 < ws2) { return 1; }
    if (ws1 > ws2) { return -1; }
    return 0; // same
  }

  public static int getAlphabeticalOrder(OpenNamesPlace p1, OpenNamesPlace p2) {
    if (StringUtils.isBlank(getPrimary(p1)) && StringUtils.isBlank(getPrimary(p2))) { return 0; }
    if (StringUtils.isBlank(getPrimary(p1))) { return 1; }
    if (StringUtils.isBlank(getPrimary(p2))) { return -1; }
    return getPrimary(p1).compareToIgnoreCase(getPrimary(p2));
  }

  public static int getDistanceOrder(LatitudeLongitude origin, OpenNamesPlace p1, OpenNamesPlace p2) {
    double d1 = getDistance(origin, p1);
    double d2 = getDistance(origin, p2);

    // farther matches go to the BOTTOM of the list
    if (d1 < d2) { return -1; }
    if (d1 > d2) { return 1; }
    return 0;
  }

  public static OpenNamesMatch determineMatch(String search, OpenNamesPlace place) {
    String searchName = search.trim();
    String placeName = getPrimary(place).trim();

    boolean exactMatch = StringUtils.equalsIgnoreCase(searchName, placeName);
    if (exactMatch) {
      return OpenNamesMatch.exact();
    }

    boolean startsWith = StringUtils.startsWithIgnoreCase(placeName, searchName);
    if (startsWith) {
      return OpenNamesMatch.startsWith();
    }

    boolean contains = StringUtils.containsIgnoreCase(placeName, searchName);
    if (contains) {
      int index = StringUtils.indexOfIgnoreCase(placeName, searchName);
      return OpenNamesMatch.contains(index+1); // never allow 0 for the index!
    }

    return OpenNamesMatch.negative();
  }

  public static double getDistance(@NonNull LatitudeLongitude origin, @NonNull OpenNamesPlace place) {
    LatLng origin_latLng = new LatLng(origin.getLatitude(), origin.getLongitude());

    Double placeX = getX(place);
    Double placeY = getY(place);

    if (placeX != null && placeY != null) {
      LatitudeLongitude place_centre = GeoConverter.osEastNorth_to_latLng(getX(place), getY(place));
      LatLng place_latLng = new LatLng(place_centre.getLatitude(), place_centre.getLongitude());
      double dist = origin_latLng.distanceTo(place_latLng);
      return dist;
    } else {
      return Double.MAX_VALUE; // if the location can't be determined, it's VERY far away
    }
  }

  public static boolean areSame(OpenNamesPlace p1, OpenNamesPlace p2) {
    return StringUtils.equals(p1.ID, p2.ID);
  }

  public static boolean areContentsSame(OpenNamesPlace p1, OpenNamesPlace p2) {
    return areSame(p1, p2); // assumption: places are immutable. tough.
  }

}
