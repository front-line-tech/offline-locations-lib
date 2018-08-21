package com.flt.liblookupclient.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flt.libgeo.LatitudeLongitude;
import com.flt.liblookupclient.LookupClient;
import com.flt.liblookupclient.R;
import com.flt.liblookupclient.entities.OpenNamesPlace;
import com.flt.liblookupclient.ui.adapters.OpenNamesPlacesResultsAdapter;
import com.flt.liblookupclient.ui.tasks.AbstractSearchTask;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Objects;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public abstract class AbstractPlacesSearchDialog extends AlertDialog {
  private static final String TAG = AbstractPlacesSearchDialog.class.getSimpleName();

  TextView text_title;
  TextView text_info;
  TextView text_instruction;
  RecyclerView recycler;
  Button close;
  ProgressBar busy;

  private Context context;

  private String title;
  private String info;
  private TextInputLayout edit_layout;
  private ImageView icon_search;

  LookupClient client;
  OpenNamesPlacesResultsAdapter adapter;

  private boolean searching;

  private Listener listener;

  public interface Listener {
    void on_cancel();
    void on_select(OpenNamesPlace result);
  }

  public AbstractPlacesSearchDialog(Context context, Listener listener) {
    super(context);
    this.context = context;
    this.client = new LookupClient(context);
    setCancelable(false);
    setCanceledOnTouchOutside(false);
    this.listener = listener;
  }

  private void bind() {
    text_title = findViewById(R.id.dialog_title);
    text_info = findViewById(R.id.text_info);
    recycler = findViewById(R.id.recycler_results);
    close = findViewById(R.id.btn_close);
    close.setOnClickListener(view -> negative_click());
    edit_layout = findViewById(R.id.input_search);
    icon_search = findViewById(R.id.btn_search);
    icon_search.setOnClickListener(view -> do_search());
    busy = findViewById(R.id.search_busy);
  }

  private void do_search() {
    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(edit_layout.getEditText().getWindowToken(), 0);

    if (searching) {
      Toast.makeText(context, R.string.toast_warning_already_searching, Toast.LENGTH_SHORT).show();
      return;
    }

    String partial = edit_layout.getEditText().getText().toString();
    if (!client.validateNameSearch(partial)) {
      Toast.makeText(context, R.string.toast_warning_search_not_valid, Toast.LENGTH_SHORT).show();
      return;
    }

    AbstractSearchTask task = new AbstractSearchTask(client) {
      @Override
      protected void onPreExecute() {
        super.onPreExecute();
        setSearching(true);
      }

      @Override
      protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        try {
          adapter.update(result.search, result.origin, result.places);
        } catch (Exception e) {
          Log.e(TAG, "Error updating results from search.", e);
        }
        try {
        setSearching(false);
        } catch (Exception e) {
          Log.e(TAG, "Error updating busy state.", e);
        }
      }
    };

    task.execute(new AbstractSearchTask.Params(partial, getOrigin()));
  }

  /**
   * Implement to retrieve the current location of the user (or the current focus of the map or search),
   * so allowing result of similar 'relevance' to be sorted by distance from the user.
   * @return a LatitudeLongitude if available, or null if the location is not available.
   */
  protected abstract LatitudeLongitude getOrigin();

  private void setSearching(boolean searching) {
    this.searching = searching;
    busy.setVisibility(searching ? VISIBLE : GONE);
    close.setEnabled(!searching);
    icon_search.setClickable(!searching);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_opennames_search);
    bind();

    adapter = new OpenNamesPlacesResultsAdapter(context, adapter_listener);
    recycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    recycler.setAdapter(adapter);

    edit_layout.getEditText().setImeOptions(EditorInfo.IME_ACTION_SEARCH);
    edit_layout.getEditText().setImeActionLabel(context.getString(R.string.btn_search), EditorInfo.IME_ACTION_SEARCH);
    edit_layout.getEditText().setOnEditorActionListener((textView, actionId, keyEvent) -> {
      if (actionId == EditorInfo.IME_ACTION_SEARCH ||
          actionId == EditorInfo.IME_ACTION_DONE ||
          actionId == EditorInfo.IME_NULL ||
          keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
        do_search();
        return true;
      }
      return false;
    });

  }

  private OpenNamesPlacesResultsAdapter.Listener adapter_listener = new OpenNamesPlacesResultsAdapter.Listener() {
    public void on_place_selected(OpenNamesPlace place) {
      hide();
      listener.on_select(place);
    }
  };

  public void negative_click() {
    hide();
    listener.on_cancel();
  }

  @Override
  public void setTitle(CharSequence title) {
    this.title = String.valueOf(title);
    text_title.setText(title);
  }

  @Override
  public void setMessage(CharSequence msg) {
    this.info = String.valueOf(msg);
    text_info.setText(info);
  }

  @Override
  public void show() {
    super.show();
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    Objects.requireNonNull(getWindow()).setGravity(Gravity.CENTER);
  }

}
