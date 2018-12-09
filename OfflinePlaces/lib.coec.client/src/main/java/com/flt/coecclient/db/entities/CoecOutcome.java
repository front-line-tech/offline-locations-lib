package com.flt.coecclient.db.entities;

import com.flt.coecclient.db.constants.OutcomeType;

import java.util.Date;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "coec_outcome",
    foreignKeys = {
        @ForeignKey(
            entity = CoecMicroTasking.class,
            parentColumns = "tasking_uuid",
            childColumns = "tasking_uuid",
            onDelete = CASCADE),
        @ForeignKey(
            entity = CoecOperative.class,
            parentColumns = "operative_uuid",
            childColumns = "operative_uuid",
            onDelete = CASCADE)
    },
    indices = { @Index("tasking_uuid"), @Index("operative_uuid"), @Index("added"), @Index("outcome") })
public class CoecOutcome {

  @PrimaryKey
  @NonNull
  public UUID outcome_uuid;

  public UUID tasking_uuid;
  public UUID operative_uuid;

  public Date added;

  public OutcomeType outcome;

  public String notes;

  @Override
  public int hashCode() {
    return outcome_uuid != null ? outcome_uuid.hashCode() : super.hashCode();
  }

}
