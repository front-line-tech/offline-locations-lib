package com.flt.applookupclientsample.ui;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.flt.applookupclientsample.R;
import com.flt.liblookupclient.LookupClient;
import com.flt.liblookupclient.geo.GeoConverter;
import com.flt.liblookupclient.geo.LatitudeLongitude;
import com.flt.liblookupclient.entities.OpenNamesHelper;
import com.flt.liblookupclient.entities.OpenNamesPlace;
import com.flt.liblookupclient.ui.dialogs.AbstractPlacesSearchDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

  @BindView(R.id.fab_search) FloatingActionButton fab_search;
  @BindView(R.id.text_state) TextView text_state;

  private GoogleMap map;
  private LookupClient client;

  private AbstractPlacesSearchDialog dialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    ButterKnife.bind(this);
    createClient();
  }

  private void createClient() {
    client = new LookupClient(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    updateUI();
  }

  private void updateUI() {
    boolean hasMap = map != null;
    boolean maySearch = hasMap && LookupClient.providerPresentOnDevice(this);
    fab_search.setVisibility(maySearch ? View.VISIBLE : View.GONE);
    text_state.setText(maySearch ? R.string.state_ready : R.string.state_provider_unavailable);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    map = googleMap;

    LatLng london = new LatLng(51.5074, -0.1278);
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(london, 7.0f));

    updateUI();
  }

  @OnClick(R.id.fab_search)
  public void search_click() {
    dialog = new AbstractPlacesSearchDialog(this, dialog_listener) {
      @Override
      protected LatitudeLongitude getOrigin() {
        CameraPosition camera = map.getCameraPosition();
        return new LatitudeLongitude(camera.target.latitude, camera.target.longitude);
      }
    };

    dialog.show();
  }

  private AbstractPlacesSearchDialog.Listener dialog_listener = new AbstractPlacesSearchDialog.Listener() {
    @Override
    public void on_cancel() {
      Toast.makeText(MapsActivity.this, R.string.toast_warning_search_cancelled, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void on_select(OpenNamesPlace result) {
      Double x = OpenNamesHelper.getX(result);
      Double y = OpenNamesHelper.getY(result);

      if (x == null || y == null) {
        Toast.makeText(MapsActivity.this, R.string.toast_warning_place_has_no_location, Toast.LENGTH_SHORT).show();
      } else {
        LatitudeLongitude location = GeoConverter.osEastNorth_to_latLng(x, y);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
      }
    }
  };
}
