package com.flt.libgeo;

import android.util.Pair;

public class EastingNorthing extends Pair<Double,Double> {
  public EastingNorthing(double east, double north) { super(east, north); }
  public double getEasting() { return first; }
  public double getNorthing() { return second; }
}
