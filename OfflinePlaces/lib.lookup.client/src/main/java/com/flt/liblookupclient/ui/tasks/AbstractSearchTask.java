package com.flt.liblookupclient.ui.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.flt.libgeo.LatitudeLongitude;
import com.flt.liblookupclient.LookupClient;
import com.flt.liblookupclient.entities.OpenNamesPlace;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Collection;

public abstract class AbstractSearchTask extends AsyncTask<AbstractSearchTask.Params, AbstractSearchTask.Progress, AbstractSearchTask.Result> {
  private static final String TAG = AbstractSearchTask.class.getSimpleName();

  protected LookupClient client;

  protected AbstractSearchTask(LookupClient client) {
    this.client = client;
  }

  @Override
  protected Result doInBackground(Params... params) {
    Params param = params[0];
    try {
      Collection<OpenNamesPlace> places = client.lookupName(param.partial_search);
      return new Result(param.partial_search, param.origin, places);
    } catch (Exception e) {
      Log.e(TAG, "Exception encountered in search." , e);
      return new Result(param.partial_search, param.origin, e);
    }
  }

  public static class Params {
    public Params(String partial_search, LatitudeLongitude origin) {
      this.partial_search = partial_search;
      this.origin = origin;
    }
    public LatitudeLongitude origin;
    public String partial_search;
  }

  public static class Progress { }

  public static class Result {
    public String search;
    public LatitudeLongitude origin;
    public Collection<OpenNamesPlace> places;
    public boolean success;
    public Exception exception;

    public Result(String search, LatitudeLongitude origin, Collection<OpenNamesPlace> places) {
      this.search = search;
      this.origin = origin;
      this.places = places;
      this.success = places != null;
    }
    public Result(String search, LatitudeLongitude origin, Exception e) {
      this.search = search;
      this.origin = origin;
      this.exception = e;
      this.success = false;
    }

  }

}
