package com.flt.coecclient.service;

import com.flt.coecclient.db.entities.CoecMicroTasking;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface ICoecService {

  boolean acceptTasking(CoecMicroTasking tasking);
  int countAcceptedTaskings();

  void addTaskings(CoecMicroTasking... taskings);
  void addTaskings(List<CoecMicroTasking> taskings);
  LiveData<List<CoecMicroTasking>> getLiveTaskingsFor(double n, double w, double s, double e);

}
