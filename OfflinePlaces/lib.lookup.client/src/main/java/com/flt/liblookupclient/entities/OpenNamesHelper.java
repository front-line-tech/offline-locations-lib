package com.flt.liblookupclient.entities;

import com.flt.liblookupclient.R;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class OpenNamesHelper {

  public static String getPrimary(OpenNamesPlace place) {
    String name = place.NAME1;
    if (name != null && StringUtils.isNotBlank(place.NAME2)) {
      name += " (" + place.NAME2 + ")";
    }
    return name;
  }

  public static String getSecondary(OpenNamesPlace place) {
    List<String> items = new LinkedList<>();
    if (StringUtils.isNotBlank(place.POPULATED_PLACE_URI)) { items.add(place.POPULATED_PLACE_URI); }
    if (StringUtils.isNotBlank(place.DISTRICT_BOROUGH_URI)) { items.add(place.DISTRICT_BOROUGH_URI); }
    if (StringUtils.isNotBlank(place.COUNTY_UNITARY_URI)) { items.add(place.COUNTY_UNITARY_URI); }
    if (StringUtils.isNotBlank(place.COUNTRY_URI)) { items.add(place.COUNTRY_URI); }
    return StringUtils.join(items, ", ");
  }

  public static int getIcon(OpenNamesPlace place) {
    if (StringUtils.equalsIgnoreCase(getType(place), "city")) {
      return R.drawable.ic_location_city_black_24dp;
    }
    if (StringUtils.equalsIgnoreCase(getType(place), "postcode") ||
        StringUtils.equalsIgnoreCase(getType(place), "post code") ||
        StringUtils.equalsIgnoreCase(getType(place), "postal code")) {
      return R.drawable.ic_local_post_office_black_24dp;
    }

    return R.drawable.ic_place_black_24dp;
  }

  public static String getType(OpenNamesPlace place) {
    if (StringUtils.isNotBlank(place.LOCAL_TYPE)) {
      return place.LOCAL_TYPE;
    }
    return null;
  }

  public static Double getX(OpenNamesPlace place) {
    String x = place.GEOMETRY_X;
    if (StringUtils.isNotBlank(x)) {
      return Double.parseDouble(x);
    } else {
      String x1 = place.MBR_XMIN;
      String x2 = place.MBR_XMAX;
      if (StringUtils.isNotBlank(x1) && StringUtils.isNotBlank(x2)) {
        return (Double.parseDouble(x1) + Double.parseDouble(x2)) / 2.0;
      } else {
        return null;
      }
    }
  }


  public static Double getY(OpenNamesPlace place) {
    String y = place.GEOMETRY_Y;
    if (StringUtils.isNotBlank(y)) {
      return Double.parseDouble(y);
    } else {
      String y1 = place.MBR_YMIN;
      String y2 = place.MBR_YMAX;
      if (StringUtils.isNotBlank(y1) && StringUtils.isNotBlank(y2)) {
        return (Double.parseDouble(y1) + Double.parseDouble(y2)) / 2.0;
      } else {
        return null;
      }
    }
  }

}
