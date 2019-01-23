package com.flt.coecclient.db.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.flt.coecclient.db.pojos.CoecLocation;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
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
public class CoecMicroTasking implements Parcelable {

  public CoecMicroTasking() {}

  // Parcelable methods
  protected CoecMicroTasking(Parcel in) {
    tasking_uuid = UUID.fromString(in.readString());
    begins = new Date(in.readLong());
    ends = new Date(in.readLong());
    title = in.readString();
    description = in.readString();
    source = in.readString();
    current_successes = in.readInt();
    max_successes = in.readInt();
    points = in.readInt();
    marked_complete = (in.readInt() == 1); // actually boolean
    catchment_m = in.readDouble();
    location = new CoecLocation(in.readDouble(), in.readDouble());
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(tasking_uuid.toString());
    dest.writeLong(begins.getTime());
    dest.writeLong(ends.getTime());
    dest.writeString(title);
    dest.writeString(description);
    dest.writeString(source);
    dest.writeInt(current_successes);
    dest.writeInt(max_successes);
    dest.writeInt(points);
    dest.writeInt(marked_complete ? 1 : 0); // boolean to int
    dest.writeDouble(catchment_m);
    dest.writeDouble(location.latitude); dest.writeDouble(location.longitude);
  }

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

  public static final Creator<CoecMicroTasking> CREATOR = new Creator<CoecMicroTasking>() {
    @Override
    public CoecMicroTasking createFromParcel(Parcel in) {
      return new CoecMicroTasking(in);
    }

    @Override
    public CoecMicroTasking[] newArray(int size) {
      return new CoecMicroTasking[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  public boolean shouldShow(Date now) {
    return begins.before(now) && ends.after(now) && !marked_complete;
  }

}
