package com.flt.libgeo;

import android.util.Pair;

public class LatitudeLongitude extends Pair<Double,Double> {
  public LatitudeLongitude(double lat, double lng) { super(lat, lng); }
  public double getLatitude() { return first; }
  public double getLongitude() { return second; }
}
