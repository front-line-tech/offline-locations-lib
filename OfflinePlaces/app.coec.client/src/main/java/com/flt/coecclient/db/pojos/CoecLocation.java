package com.flt.coecclient.db.pojos;

import androidx.room.Ignore;

public class CoecLocation {

  public CoecLocation() {}

  @Ignore
  public CoecLocation(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public double latitude;
  public double longitude;

}
