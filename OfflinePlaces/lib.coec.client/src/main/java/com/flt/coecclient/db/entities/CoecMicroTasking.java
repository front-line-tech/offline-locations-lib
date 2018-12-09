package com.flt.coecclient.db.entities;

import com.flt.coecclient.db.pojos.CoecLocation;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "coec_microtasking",
    indices = { @Index("tasking_uuid"), @Index("marked_complete"), @Index("begins"), @Index("ends") })
public class CoecMicroTasking {

  @PrimaryKey
  @NonNull
  public UUID tasking_uuid;

  public Date begins;
  public Date ends;

  public String title;
  public String description;
  public String source;

  public int current_successes;
  public int max_successes;

  public int points;

  public boolean marked_complete;

  public double catchment_m;
  @Embedded(prefix = "at_") public CoecLocation location;

  @Override
  public int hashCode() {
    return tasking_uuid != null ? tasking_uuid.hashCode() : super.hashCode();
  }
}
