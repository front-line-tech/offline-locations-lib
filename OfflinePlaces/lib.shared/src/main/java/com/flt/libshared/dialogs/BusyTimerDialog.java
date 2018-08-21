package com.flt.libshared.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flt.libshared.R;

import org.apache.commons.lang3.StringUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class BusyTimerDialog extends AlertDialog {

  public ProgressBar progress;
  public Chronometer chrono;
  public TextView text_title;
  public TextView text_message;
  public TextView text_submessage;
  public Button negative;
  public Button positive;

  String title;
  String task;
  String subtask;

  String positive_text;
  String negative_text;
  View.OnClickListener positive_click;
  View.OnClickListener negative_click;

  public BusyTimerDialog(Context context) {
    super(context);
    setCancelable(false);
    setCanceledOnTouchOutside(false);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_busy_with_timer);
    bind();
    update(title, task, subtask, positive_text, positive_click, negative_text, negative_click);
  }

  private void bind() {
    this.progress = findViewById(R.id.dialog_progress);
    this.chrono = findViewById(R.id.chrono_dialog_time);
    this.text_title = findViewById(R.id.dialog_title);
    this.text_message = findViewById(R.id.dialog_message);
    this.text_submessage = findViewById(R.id.dialog_submessage);
    this.negative = findViewById(R.id.btn_negative);
    this.positive = findViewById(R.id.btn_positive);
  }

  public void update(String title, String task, String subtask) {
    update(title, task, subtask, null ,null, null, null);
  }

  public void update(String title, String task, String subtask, String pos_txt, View.OnClickListener pos_click, String neg_txt, View.OnClickListener neg_click) {
    setTitle(title);
    setMessage(task);
    setSubMessage(subtask);
    setPositiveButton(null, null);
    setNegativeButton(null, null);
  }

  @Override
  public void setTitle(CharSequence title) {
    this.title = String.valueOf(title);
    text_title.setText(title);
  }

  @Override
  public void setMessage(CharSequence msg) {
    this.task = String.valueOf(msg);
    text_message.setText(msg);
    text_message.setVisibility(StringUtils.isBlank(msg) ? GONE : VISIBLE);
  }

  public void setSubMessage(String submsg) {
    this.subtask = submsg;
    text_submessage.setText(submsg);
    text_submessage.setVisibility(StringUtils.isBlank(submsg) ? GONE : VISIBLE);
  }

  public void setPositiveButton(String text, View.OnClickListener click) {
    positive_text = text;
    positive_click = click;
    if (StringUtils.isNotBlank(positive_text)) {
      positive.setText(positive_text);
      positive.setOnClickListener(positive_click);
      positive.setVisibility(VISIBLE);
    } else {
      positive.setVisibility(GONE);
    }
  }

  public void setNegativeButton(String text, View.OnClickListener click) {
    negative_text = text;
    negative_click = click;
    if (StringUtils.isNotBlank(negative_text)) {
      negative.setText(negative_text);
      negative.setOnClickListener(negative_click);
      negative.setVisibility(VISIBLE);
    } else {
      negative.setVisibility(GONE);
    }
  }

  public void show(String title, String message, String submessage) {
    show();
    update(title, message, submessage);
  }

  @Override
  public void show() {
    super.show();
    chrono.setBase(SystemClock.elapsedRealtime());
    chrono.start();
    progress.setIndeterminate(true);
    getWindow().setGravity(Gravity.BOTTOM);
  }

  @Override
  public void hide() {
    chrono.stop();
    super.hide();
  }
}
