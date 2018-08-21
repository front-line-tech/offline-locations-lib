package com.flt.applooukpprovider.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.flt.applooukpprovider.LookupProviderApp;
import com.flt.applooukpprovider.R;
import com.flt.applooukpprovider.tasks.ExtractDataTask;
import com.flt.liblookupprovider.LibLookup;
import com.flt.libshared.events.WeakEventProvider;
import com.flt.servicelib.AbstractPermissionExtensionAppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AbstractPermissionExtensionAppCompatActivity {

  LibLookup lib;

  @BindView(R.id.text_state) TextView text_state;
  @BindView(R.id.btn_extract) Button btn_extract;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    setTitleBarToVersionWith(getString(R.string.title_activity_main));
  }

  @Override
  protected void onResume() {
    super.onResume();
    lib = LibLookup.getInstance(this);
    updateUI();
    lib.getLibStateEventProvider().addListener(lib_state_listener);
  }

  @Override
  protected void onPause() {
    super.onPause();
    lib.getLibStateEventProvider().removeListener(lib_state_listener);
    lib = null;
  }

  private void updateUI() {
    switch (lib.getState()) {
      case DataExtracting:
        text_state.setText(R.string.text_state_extracting);
        btn_extract.setVisibility(GONE);
        break;

      case DataUnavailable:
        text_state.setText(R.string.text_state_data_unavailable);
        btn_extract.setVisibility(VISIBLE);
        break;

      case DataReady:
        text_state.setText(getString(R.string.text_state_data_available, lib.getDb().getPlacesDao().count()));
        btn_extract.setVisibility(GONE);
        break;

      case Finished:
        text_state.setText(R.string.text_state_finished);
        btn_extract.setVisibility(GONE);
        break;

      case Initialising:
      default:
        text_state.setText(R.string.text_state_initialising);
        btn_extract.setVisibility(GONE);
        break;
    }
  }

  WeakEventProvider.Listener<LibLookup.StateChangeEvent> lib_state_listener = new WeakEventProvider.Listener<LibLookup.StateChangeEvent>() {
    @Override
    protected boolean parseEvent(LibLookup.StateChangeEvent event) {
      runOnUiThread(() -> updateUI());
      return false; // keep listening
    }
  };

  @OnClick(R.id.btn_extract)
  public void extract_click() {
    doExtraction(false);
  }

  private void doExtraction(boolean override) {
    new ExtractDataTask(this).execute(new ExtractDataTask.Params(override, false));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(Menu.NONE, R.string.menu_force_extract, 0, R.string.menu_force_extract);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.string.menu_force_extract:
        doExtraction(true);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override protected void onGrantedOverlayPermission() { }
  @Override protected void onRefusedOverlayPermission() { }
  @Override protected String[] getRequiredPermissions() { return LookupProviderApp.permissions_required; }
  @Override protected void onPermissionsGranted() { updateUI(); }
  @Override protected void onNotAllPermissionsGranted() { updateUI(); }
  @Override protected void onUnecessaryCallToRequestOverlayPermission() { }
}
