package com.flt.liblookupclient.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flt.libgeo.LatitudeLongitude;
import com.flt.liblookupclient.R;
import com.flt.liblookupclient.entities.OpenNamesHelper;
import com.flt.liblookupclient.entities.OpenNamesPlace;
import com.flt.liblookupclient.entities.OpenNamesSortHelper;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Collection;

public class OpenNamesPlacesResultsAdapter extends RecyclerView.Adapter<OpenNamesPlacesResultsAdapter.ViewHolder> {

  private Context context;
  private Listener listener;

  private String search;
  private LatitudeLongitude origin;
  private SortedList<OpenNamesPlace> places;

  public interface Listener {
    void on_place_selected(OpenNamesPlace place);
  }

  public OpenNamesPlacesResultsAdapter(Context context, Listener listener) {
    this.context = context;
    this.listener = listener;
    this.places = new SortedList<>(OpenNamesPlace.class, list_callback);
  }

  public void update(String search, LatitudeLongitude origin, Collection<OpenNamesPlace> items) {
    this.search = search;
    this.origin = origin;
    this.places.beginBatchedUpdates();
    this.places.clear();
    this.places.addAll(items);
    this.places.endBatchedUpdates();
  }

  private SortedListAdapterCallback<OpenNamesPlace> list_callback = new SortedListAdapterCallback<OpenNamesPlace>(this) {
    @Override public int compare(OpenNamesPlace o1, OpenNamesPlace o2) { return OpenNamesSortHelper.getCombinedOrder(search, origin, o1, o2); }
    @Override public boolean areContentsTheSame(OpenNamesPlace o1, OpenNamesPlace o2) { return OpenNamesSortHelper.areContentsSame(o1, o2); }
    @Override public boolean areItemsTheSame(OpenNamesPlace o1, OpenNamesPlace o2) { return OpenNamesSortHelper.areSame(o1, o2); }
  };

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.list_item_opennames_place, null);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    OpenNamesPlace place = places.get(position);
    holder.setPlace(place, listener);
  }

  @Override
  public int getItemCount() {
    return places.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    OpenNamesPlace place;

    TextView text_primary;
    TextView text_secondary;
    TextView text_type;

    ImageView icon;

    public ViewHolder(View itemView) {
      super(itemView);
      text_primary = itemView.findViewById(R.id.text_place_primary);
      text_secondary = itemView.findViewById(R.id.text_place_secondary);
      text_type = itemView.findViewById(R.id.text_place_type);
      icon = itemView.findViewById(R.id.place_icon);
      itemView.setClickable(true);
    }

    public void setPlace(OpenNamesPlace place, Listener listener) {
      this.place = place;
      text_primary.setText(OpenNamesHelper.getPrimary(place));
      text_secondary.setText(OpenNamesHelper.getSecondary(place));
      text_type.setText(OpenNamesHelper.getType(place));
      icon.setImageResource(OpenNamesHelper.getIcon(place));
      itemView.setOnClickListener(view -> listener.on_place_selected(ViewHolder.this.place));
    }
  }
}
