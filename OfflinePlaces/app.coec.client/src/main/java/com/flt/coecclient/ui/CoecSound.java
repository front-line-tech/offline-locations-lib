package com.flt.coecclient.ui;

import com.flt.coecclient.R;

/**
 * https://themushroomkingdom.net/media/smb/wav
 */
public enum CoecSound {
  Create(R.raw.new_task_1),
  Consider(R.raw.new_task_2),
  Accept(R.raw.smb_powerup),
  Fail(R.raw.smb_pipe),
  Succeed(R.raw.smb_stage_clear);

  public int audio_resource;

  CoecSound(int audio_resource) {
    this.audio_resource = audio_resource;
  }
}
