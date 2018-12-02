package com.flt.libcoecclient.db.pojos;

import com.flt.libcoecclient.db.entities.CoecMicroTasking;
import com.flt.libcoecclient.db.entities.CoecOperative;
import com.flt.libcoecclient.db.entities.CoecOutcome;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class CoecMicroTaskingHistory {

  @Embedded
  public CoecMicroTasking tasking;

  @Relation(parentColumn = "tasking_uuid", entityColumn = "tasking_uuid", entity = CoecOutcome.class)
  public List<CoecOutcome> outcomes;

}
