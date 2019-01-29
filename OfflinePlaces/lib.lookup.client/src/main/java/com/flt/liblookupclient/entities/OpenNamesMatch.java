package com.flt.liblookupclient.entities;

import android.util.Pair;

public class OpenNamesMatch extends Pair<OpenNamesMatch.MatchType, Float> {

  public enum MatchType {
    ExactMatch(1.0f),
    StartsWith(0.9f),
    Contains(0.5f),
    DoesNotContain(0.0f);
    MatchType(float weighting) {
      this.weighting = weighting;
    }
    public float weighting;
  }

  public OpenNamesMatch(MatchType type, float strength) {
    super (type, strength);
  }

  public float getRawStrength() { return second; }
  public float getRawWeighting() { return first.weighting; }
  public MatchType getType() { return first; }

  public float getWeightedStrength() { return second * first.weighting; }

  public static OpenNamesMatch exact() {
    return new OpenNamesMatch(OpenNamesMatch.MatchType.ExactMatch, 1.0f);
  }

  public static OpenNamesMatch startsWith() {
    return new OpenNamesMatch(MatchType.StartsWith, 1.0f);
  }

  public static OpenNamesMatch contains(int atPosition) {
    return new OpenNamesMatch(MatchType.Contains, 1.0f / atPosition);
  }

  public static OpenNamesMatch negative() {
    return new OpenNamesMatch(MatchType.DoesNotContain, 0.0f);
  }

}
