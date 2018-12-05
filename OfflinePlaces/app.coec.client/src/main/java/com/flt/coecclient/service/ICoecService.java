package com.flt.coecclient.service;

import com.flt.libcoecclient.db.entities.CoecMicroTasking;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface ICoecService {

  LiveData<List<CoecMicroTasking>> getLiveTaskingsFor(double n, double w, double s, double e);

}
