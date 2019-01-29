package com.flt.coecclient.ui;

import android.view.View;
import android.widget.TextView;

import com.flt.coecclient.R;
import com.flt.coecclient.db.entities.CoecMicroTasking;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CoecTaskingInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

  private MapsActivity context;

  public CoecTaskingInfoWindowAdapter(MapsActivity ctx){
    context = ctx;
  }

  @Override
  public View getInfoWindow(Marker marker) {
    return null;
  }

  @Override
  public View getInfoContents(Marker marker) {
    if (marker == null || marker.getTag() == null) { return null; }

    Object tag = marker.getTag();
    if (tag instanceof CoecMicroTasking) {
      CoecMicroTasking tasking = (CoecMicroTasking) tag;

      View view = context.getLayoutInflater().inflate(R.layout.infowindow_microtasking, null);

      TextView info_title = view.findViewById(R.id.info_title);
      TextView info_source = view.findViewById(R.id.info_source);
      TextView info_points = view.findViewById(R.id.text_points);
      TextView info_description = view.findViewById(R.id.info_description);

      RelativeTimeTextView info_from = view.findViewById(R.id.info_timestamp_from);
      RelativeTimeTextView info_to = view.findViewById(R.id.info_timestamp_to);

      info_title.setText(tasking.title);
      info_source.setText(tasking.source);
      info_points.setText(String.valueOf(tasking.points));
      info_description.setText(tasking.description);
      info_from.setReferenceTime(tasking.begins.getTime());
      info_to.setReferenceTime(tasking.ends.getTime());

      return view;
    } else {
      return null;
    }
  }
}