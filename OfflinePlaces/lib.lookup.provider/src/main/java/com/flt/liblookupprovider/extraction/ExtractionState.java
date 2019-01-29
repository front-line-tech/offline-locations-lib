package com.flt.liblookupprovider.extraction;

import java.util.Date;

public class ExtractionState {

  public String source_zip;
  public int total_files;
  public Date started;
  public Date complete;

  public String current_file;
  public int processed_files;
  public int processed_places;

  public ExtractionState(String source_zip, int total_files) {
    this.source_zip = source_zip;
    this.total_files = total_files;
  }

  public void setCurrentFile(String file) {
    current_file = file;
    if (started == null) { started = new Date(); }
  }

  public void completeCurrentFile(int had_places) {
    current_file = null;
    processed_files += 1;
    processed_places += had_places;
  }

  public void setComplete() {
    this.complete = new Date();
  }

  public boolean isComplete() {
    return complete != null;
  }

}
