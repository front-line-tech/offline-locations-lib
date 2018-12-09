package com.flt.coecclient.sampledata;

import com.flt.coecclient.db.entities.CoecMicroTasking;
import com.flt.coecclient.db.pojos.CoecLocation;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class SampleData {

  private static CoecLocation getNearLincsHQ() {
    return new CoecLocation(53.269138, -0.498563);
  }

  private static CoecLocation getAllSaintsLane() { return new CoecLocation(53.266090, -0.491922); }

  private static CoecLocation getBlackHorse() { return new CoecLocation(53.266680, -0.489046); }

  private static CoecLocation getSchool() { return new CoecLocation(53.264600, -0.488660); }

  private static CoecLocation getPlayingField() { return new CoecLocation(53.268621, -0.486074); }

  public static List<CoecMicroTasking> createSampleTaskings() {

    long now = new Date().getTime();

    CoecMicroTasking tasking1 = new CoecMicroTasking();
    tasking1.tasking_uuid = UUID.randomUUID();
    tasking1.begins = new Date(now);
    tasking1.ends = new Date(tasking1.begins.getTime() + (1000 * 60 * 24 * 365));
    tasking1.location = getNearLincsHQ();
    tasking1.catchment_m = 1000;
    tasking1.marked_complete = false;
    tasking1.current_successes = 0;
    tasking1.max_successes = 50;
    tasking1.points = 10;
    tasking1.source = "CSCV";
    tasking1.title = "Attend Hack the Police 3";
    tasking1.description = "Walk in, disrupt some crime, walk out!";

    CoecMicroTasking tasking2 = new CoecMicroTasking();
    tasking2.tasking_uuid = UUID.randomUUID();
    tasking2.begins = new Date(now + (1000 * 15));
    tasking2.ends = new Date(tasking2.begins.getTime() + (1000 * 60 * 24 * 365));
    tasking2.location = getAllSaintsLane();
    tasking2.catchment_m = 1000;
    tasking2.marked_complete = false;
    tasking2.current_successes = 0;
    tasking2.max_successes = 3;
    tasking2.points = 25;
    tasking2.source = "Victims services";
    tasking2.title = "REPEAT VICTIM welfare check";
    tasking2.description = "Mrs Miggins is a repeat burglary victim.\nComplete basic reassurance at the door.";

    CoecMicroTasking tasking3 = new CoecMicroTasking();
    tasking3.tasking_uuid = UUID.randomUUID();
    tasking3.begins = new Date(now + (1000 * 25));
    tasking3.ends = new Date(tasking3.begins.getTime() + (1000 * 60 * 24 * 365));
    tasking3.location = getBlackHorse();
    tasking3.catchment_m = 1000;
    tasking3.marked_complete = false;
    tasking3.current_successes = 0;
    tasking3.max_successes = 3;
    tasking3.points = 25;
    tasking3.source = "Violent Crime Unit";
    tasking3.title = "Visible presence to deter disorder";
    tasking3.description = "MARKED UNITS: Drive-by the Black Horse\nto disrupt and deter violence.";

    CoecMicroTasking tasking4 = new CoecMicroTasking();
    tasking4.tasking_uuid = UUID.randomUUID();
    tasking4.begins = new Date(now + (1000 * 35));
    tasking4.ends = new Date(tasking3.begins.getTime() + (1000 * 60 * 24 * 365));
    tasking4.location = getPlayingField();
    tasking4.catchment_m = 1000;
    tasking4.marked_complete = false;
    tasking4.current_successes = 0;
    tasking4.max_successes = 3;
    tasking4.points = 25;
    tasking4.source = "YOT";
    tasking4.title = "Deter evening gang meets";
    tasking4.description = "Talk to youths regularly gathering here in the evening.";

    CoecMicroTasking tasking5 = new CoecMicroTasking();
    tasking5.tasking_uuid = UUID.randomUUID();
    tasking5.begins = new Date(now + (1000 * 45));
    tasking5.ends = new Date(tasking3.begins.getTime() + (1000 * 60 * 24 * 365));
    tasking5.location = getSchool();
    tasking5.catchment_m = 1000;
    tasking5.marked_complete = false;
    tasking5.current_successes = 0;
    tasking5.max_successes = 3;
    tasking5.points = 25;
    tasking5.source = "Traffic unit";
    tasking5.title = "Assist flow of traffic";
    tasking5.description = "Mums are getting in fights in the school run.";

    List<CoecMicroTasking> taskings = new LinkedList<>();

    taskings.add(tasking1);
    taskings.add(tasking2);
    taskings.add(tasking3);
    taskings.add(tasking4);
    taskings.add(tasking5);

    return taskings;
  }

}
