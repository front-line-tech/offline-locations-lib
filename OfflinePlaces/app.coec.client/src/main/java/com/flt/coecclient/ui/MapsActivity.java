package com.flt.coecclient.ui;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.flt.coecclient.CoecClientApp;
import com.flt.coecclient.R;
import com.flt.coecclient.service.CoecService;
import com.flt.coecclient.service.ICoecService;
import com.flt.liblookupclient.LookupClient;
import com.flt.liblookupclient.entities.OpenNamesHelper;
import com.flt.liblookupclient.entities.OpenNamesPlace;
import com.flt.liblookupclient.geo.GeoConverter;
import com.flt.liblookupclient.geo.LatitudeLongitude;
import com.flt.liblookupclient.ui.dialogs.AbstractPlacesSearchDialog;
import com.flt.servicelib.AbstractPermissionExtensionAppCompatActivity;
import com.flt.servicelib.AbstractServiceBoundAppCompatActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MapsActivity extends AbstractServiceBoundAppCompatActivity<CoecService, ICoecService> implements OnMapReadyCallback {

  private static final String TAG = MapsActivity.class.getSimpleName();

  @BindView(R.id.speedial) SpeedDialView speeddial;
  @BindView(R.id.text_state) TextView text_state;

  private GoogleMap map;
  private FusedLocationProviderClient location_client;

  private LookupClient lookup_client;
  private AbstractPlacesSearchDialog lookup_dialog;

  private float move_zoom = 7.0f;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    ButterKnife.bind(this);
  }

  @Override
  protected void onBoundChanged(boolean isBound) {
    updateUI();
  }

  private void prepareClients() {
    location_client = createLocationClient();
    lookup_client = createLookupClient();
  }

  private LookupClient createLookupClient() {
    return new LookupClient(this);
  }

  private FusedLocationProviderClient createLocationClient() {
    return LocationServices.getFusedLocationProviderClient(this);
  }

  private void prepareSpeedDial() {
    speeddial.addActionItem(
        new SpeedDialActionItem.Builder(R.id.sd_search, R.drawable.ic_search_white_24dp)
            .setLabel(R.string.sd_label_search)
            .create());

    speeddial.addActionItem(
        new SpeedDialActionItem.Builder(R.id.sd_centre, R.drawable.ic_my_location_white_24dp)
            .setLabel(R.string.sd_label_centre)
            .create());

    speeddial.setOnActionSelectedListener(speedDialActionItem -> {
      switch (speedDialActionItem.getId()) {
        case R.id.sd_search:
          search_click();
          return false; // true to keep the Speed Dial open

        case R.id.sd_centre:
          centre_click();
          return false;

        default:
          return false;
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    prepareClients();
    prepareSpeedDial();
    updateUI();
  }

  private void updateUI() {
    boolean hasMap = map != null;
    boolean maySearch = bound && hasMap && LookupClient.providerPresentOnDevice(this);
    speeddial.setVisibility(maySearch ? View.VISIBLE : View.GONE);
    text_state.setText(maySearch ? R.string.state_ready : R.string.state_provider_unavailable);
  }

  private void centre_click() {
    if (anyOutstandingPermissions()) {
      requestAllPermissions();
      return;
    }

    try {
      location_client
          .getLastLocation()
          .addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
              if (location != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), move_zoom));
              }
            }
          });

    } catch (SecurityException e) {
      Timber.e(e, "Attempted to use LocationClient without permissions.");
    }

  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    map = googleMap;

    if (anyOutstandingPermissions()) {
      LatLng london = new LatLng(51.5074, -0.1278);
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(london, move_zoom));
    } else {
      centre_click();
    }

    updateUI();
  }

  private void search_click() {
    lookup_dialog = new AbstractPlacesSearchDialog(this, dialog_listener) {
      @Override
      protected LatitudeLongitude getOrigin() {
        CameraPosition camera = map.getCameraPosition();
        return new LatitudeLongitude(camera.target.latitude, camera.target.longitude);
      }
    };

    lookup_dialog.show();
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

  @Override protected void onGrantedOverlayPermission() { }
  @Override protected void onRefusedOverlayPermission() { }
  @Override protected String[] getRequiredPermissions() { return CoecClientApp.permissions_required; }
  @Override protected void onPermissionsGranted() { updateUI(); }
  @Override protected void onNotAllPermissionsGranted() { }
  @Override protected void onUnecessaryCallToRequestOverlayPermission() { }
}
