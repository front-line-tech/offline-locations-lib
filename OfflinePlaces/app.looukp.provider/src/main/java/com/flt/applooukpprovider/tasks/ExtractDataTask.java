package com.flt.applooukpprovider.tasks;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.flt.applooukpprovider.R;
import com.flt.liblookupprovider.LibLookup;
import com.flt.liblookupprovider.extraction.ExtractionState;
import com.flt.libshared.dialogs.BusyTimerDialog;
import com.flt.libshared.events.WeakEventProvider;

import org.apache.commons.lang3.StringUtils;

public class ExtractDataTask extends AsyncTask<ExtractDataTask.Params, ExtractDataTask.Progress, ExtractDataTask.Result> {
  private static final String TAG = ExtractDataTask.class.getSimpleName();

  private BusyTimerDialog dialog;
  private AppCompatActivity activity;
  private LibLookup lib;

  private String dialog_title;
  private String dialog_message;
  private String dialog_submessage;

  public ExtractDataTask(AppCompatActivity context) {
    this.activity = context;
    this.lib = LibLookup.getInstance(context);
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    dialog = new BusyTimerDialog(activity);
    dialog_title = activity.getString(R.string.dialog_title_extracting);
    dialog_message = activity.getString(R.string.dialog_message_initialising);
    dialog_submessage = activity.getString(R.string.dialog_submessage_initialising);
    dialog.show(dialog_title, dialog_message, dialog_submessage);
  }

  @Override
  protected Result doInBackground(Params... params) {
    try {
      lib.getExtractionStateEventProvider().addListener(extraction_listener);
      boolean override = params[0].override;
      boolean test_only = params[0].test_only;
      lib.doExtraction(override, test_only);

      return new Result(true);
    } catch (Exception e) {
      Log.e(TAG, "Exception encountered during extraction.", e);
      activity.runOnUiThread(() -> Toast.makeText(activity, R.string.toast_warning_extraction_error, Toast.LENGTH_LONG).show());
      return new Result(false);
    } finally {
      lib.getExtractionStateEventProvider().removeListener(extraction_listener);
    }
  }

  private WeakEventProvider.Listener<ExtractionState> extraction_listener = new WeakEventProvider.Listener<ExtractionState>() {
    @Override
    protected boolean parseEvent(ExtractionState event) {
      publishProgress(new Progress(event));
      return false; // keep listening
    }
  };

  @Override
  protected void onProgressUpdate(Progress... values) {
    super.onProgressUpdate(values);
    ExtractionState state = values[0].current_state;

    String file = state.current_file;
    if (StringUtils.isBlank(file)) {
      dialog_message = activity.getString(R.string.dialog_message_ready_to_process_file);
    } else {
      dialog_message = activity.getString(R.string.dialog_message_processing_file, file);
    }

    int processed = state.processed_files;
    int total = state.total_files;
    int places = state.processed_places;
    dialog_submessage = activity.getString(R.string.dialog_submessage_processed_X_of_Y_files_for_Z_places, processed, total, places);

    try {
      dialog.update(dialog_title, dialog_message, dialog_submessage);
    } catch (Exception e) {
      Log.w(TAG, "Could not update dialog.", e);
    }
  }

  @Override
  protected void onPostExecute(Result result) {
    super.onPostExecute(result);
    try {
      dialog.hide();
    } catch (Exception e) {
      Log.w(TAG, "Could not hide dialog.", e);
    }
    dialog = null;
    activity = null;
  }

  public static class Params {
    public Params(boolean override, boolean test_only) {
      this.override = override;
      this.test_only = test_only;
    }
    public boolean test_only;
    public boolean override;
  }

  public static class Progress {
    public Progress(ExtractionState state) {
      this.current_state = state;
    }
    public ExtractionState current_state;
  }

  public static class Result {
    public boolean succeeded;
    public Result(boolean succeeded) {
      this.succeeded = succeeded;
    }

  }
}
