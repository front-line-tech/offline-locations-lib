package com.flt.coecclient.db.pojos;

import com.flt.coecclient.db.entities.CoecMicroTasking;
import com.flt.coecclient.db.entities.CoecOperative;
import com.flt.coecclient.db.entities.CoecOutcome;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class CoecMicroTaskingHistory {

  @Embedded
  public CoecMicroTasking tasking;

  @Relation(parentColumn = "tasking_uuid", entityColumn = "tasking_uuid", entity = CoecOutcome.class)
  public List<CoecOutcome> outcomes;

}
