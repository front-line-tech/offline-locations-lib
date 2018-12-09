package com.flt.coecclient.db.entities;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "coec_operative",
    indices = { @Index("operative_uuid"), @Index("shoulder"), @Index("warrant_or_pay") })
public class CoecOperative {

  @PrimaryKey
  @NonNull
  public UUID operative_uuid;

  public String name;
  public String shoulder;
  public String warrant_or_pay;

  @Override
  public int hashCode() {
    return operative_uuid != null ? operative_uuid.hashCode() : super.hashCode();
  }

}
