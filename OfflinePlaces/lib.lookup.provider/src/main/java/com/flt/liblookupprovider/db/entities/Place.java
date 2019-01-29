package com.flt.liblookupprovider.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.flt.liblookupclient.entities.OpenNamesPlace;

import java.util.Date;

@Entity(tableName = "place",
    indices = { @Index("row_id"), @Index("created"), @Index("ID") })
public class Place extends OpenNamesPlace {

  @NonNull @PrimaryKey(autoGenerate = true) public long row_id;
  @NonNull public Date created;

}
