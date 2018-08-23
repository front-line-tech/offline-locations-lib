package com.flt.applooukpprovider.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flt.applooukpprovider.LookupProviderApp;
import com.flt.applooukpprovider.R;
import com.flt.applooukpprovider.tasks.ExtractDataTask;
import com.flt.liblookupprovider.LibLookup;
import com.flt.libshared.events.WeakEventProvider;
import com.flt.servicelib.AbstractPermissionExtensionAppCompatActivity;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AbstractPermissionExtensionAppCompatActivity {

  LibLookup lib;

  @BindView(R.id.icon) ImageView icon_state;
  @BindView(R.id.text_state) TextView text_state;
  @BindView(R.id.btn_extract) Button btn_extract;


  @BindView(R.id.text_technicals_01_gradle_client) TextView text_technicals_01_gradle_client;
  @BindView(R.id.text_technicals_02_content_resolver) TextView text_technicals_02_content_resolver;

  @BindView(R.id.card_technicals_01_gradle) CardView card_technicals_01_gradle;
  @BindView(R.id.card_technicals_02_content) CardView card_technicals_02_content_resolver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    setTitleBarToVersionWith(getString(R.string.title_activity_main));

    text_technicals_01_gradle_client.setText(Html.fromHtml(getString(R.string.text_technicals_01_gradle_client)));

    text_technicals_02_content_resolver.setText(Html.fromHtml(getString(R.string.text_technicals_02_content_resolver,
          getString(R.string.liblookup_provider_authority),
          getString(R.string.liblookup_provider_permission))));

    // alter default ignore-click behaviour!
    text_technicals_01_gradle_client.setMovementMethod(LinkMovementMethod.getInstance());
    text_technicals_02_content_resolver.setMovementMethod(LinkMovementMethod.getInstance());
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
        icon_state.setImageResource(R.drawable.ic_hourglass_empty_black_24dp);
        text_state.setText(R.string.text_state_extracting);
        btn_extract.setVisibility(GONE);
        break;

      case DataUnavailable:
        icon_state.setImageResource(R.drawable.ic_warning_black_24dp);
        text_state.setText(R.string.text_state_data_unavailable);
        btn_extract.setVisibility(VISIBLE);
        break;

      case DataReady:
        icon_state.setImageResource(R.drawable.ic_lightbulb_outline_black_24dp);
        text_state.setText(getString(R.string.text_state_data_available, lib.getDb().getPlacesDao().count(), lib.getCompletedExtractionFile()));
        btn_extract.setVisibility(GONE);
        break;

      case Finished:
        icon_state.setImageResource(R.drawable.ic_cancel_black_24dp);
        text_state.setText(R.string.text_state_finished);
        btn_extract.setVisibility(GONE);
        break;

      case Initialising:
      default:
        icon_state.setImageResource(R.drawable.ic_hourglass_empty_black_24dp);
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
    menu.add(Menu.NONE, R.string.menu_show_developer_notes, 0, R.string.menu_show_developer_notes);
    menu.add(Menu.NONE, R.string.menu_force_extract, 0, R.string.menu_force_extract);
    menu.add(Menu.NONE, R.string.menu_about_app, 0, R.string.menu_about_app);
    menu.add(Menu.NONE, R.string.menu_os_licenses, 0, R.string.menu_os_licenses);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.string.menu_os_licenses:
        showLicenses();
        return true;

      case R.string.menu_about_app:
        showAbout();
        return true;

      case R.string.menu_force_extract:
        doExtraction(true);
        return true;

      case R.string.menu_show_developer_notes:
        showDeveloperNotes();
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void showLicenses() {
    startActivity(new Intent(this, OssLicensesMenuActivity.class));
  }

  private void showDeveloperNotes() {
    card_technicals_01_gradle.setVisibility(VISIBLE);
    card_technicals_02_content_resolver.setVisibility(VISIBLE);
  }

  private void showAbout() {
    SpannableString message = new SpannableString(getString(R.string.dialog_message_about));
    Linkify.addLinks(message, Linkify.ALL);

    AlertDialog d = new AlertDialog.Builder(this)
        .setTitle(R.string.dialog_title_about)
        .setMessage(message)
        .setPositiveButton(R.string.btn_ok, (dialogInterface, i) -> dialogInterface.dismiss())
        .show();

    ((TextView)d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
  }

  @Override protected void onGrantedOverlayPermission() { }
  @Override protected void onRefusedOverlayPermission() { }
  @Override protected String[] getRequiredPermissions() { return LookupProviderApp.permissions_required; }
  @Override protected void onPermissionsGranted() { updateUI(); }
  @Override protected void onNotAllPermissionsGranted() { updateUI(); }
  @Override protected void onUnecessaryCallToRequestOverlayPermission() { }
}
