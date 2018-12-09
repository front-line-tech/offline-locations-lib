package com.flt.coecclient.db.util;

import com.flt.coecclient.db.constants.OutcomeType;
import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import androidx.room.TypeConverter;

public class Converters {

  @TypeConverter
  public static String fromOutcome(OutcomeType outcome) {
    if (outcome == null) { return(null); }
    return(outcome.name());
  }

  @TypeConverter
  public static OutcomeType toOutcome(String str) {
    if (str == null) { return(null); }
    return(OutcomeType.valueOf(str));
  }

  @TypeConverter
  public static Long fromDate(Date date) {
    if (date == null) { return(null); }
    return(date.getTime());
  }

  @TypeConverter
  public static Date toDate(Long millisSinceEpoch) {
    if (millisSinceEpoch == null) { return(null); }
    return(new Date(millisSinceEpoch));
  }

  @TypeConverter
  public static String fromUuid(UUID uuid) {
    if (uuid == null) { return null; }
    return uuid.toString();
  }

  @TypeConverter
  public static UUID toUuid(String uuidStr) {
    if (StringUtils.isBlank(uuidStr)) { return null; }
    return UUID.fromString(uuidStr);
  }

  @TypeConverter
  public static String fromLatLng(LatLng point) {
    if (point == null) { return null; }
    return String.format(Locale.UK, "%f,%f",
        point.latitude,
        point.longitude);
  }

  @TypeConverter
  public static LatLng toLatLng(String latLngStr) {
    if (StringUtils.isBlank(latLngStr)) { return null; }
    String[] parts = latLngStr.split(",");
    if (parts.length == 2) {
      return new LatLng(
          Double.parseDouble(parts[0]),
          Double.parseDouble(parts[1]));
    }
    throw new ArrayIndexOutOfBoundsException("LatLng csv with " + parts.length + " parts.");
  }

}
