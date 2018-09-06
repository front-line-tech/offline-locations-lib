package com.flt.liblookupprovider.extraction;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class OpenNamesExtractor {
  private static final String TAG = OpenNamesExtractor.class.getSimpleName();

  private Context context;
  private Listener listener;
  private ExtractionState state;

  public interface Listener {
    void onOpenFile(String filename);
    void onCompleteFile(int places_found);
  }

  public OpenNamesExtractor(Context context, Listener listener) {
    this.context = context;
    this.listener = listener;
  }

  public int countRelevantFiles(InputStream input, StreamParser parser) throws IOException {
    int relevant_files = 0;
    boolean finished = false;
    ZipInputStream stream = new ZipInputStream(input);
    do {
      ZipEntry entry = stream.getNextEntry();
      if (entry != null) {
        Log.v(TAG, "Counting: " + entry.getName());
        if (parser.canParse(entry)) {
          relevant_files++;
        }
        stream.closeEntry();
      } else {
        finished = true;
      }
    } while (!finished);
    return relevant_files;
  }

  public void doExtraction(InputStream input, StreamParser parser, int maxFiles) throws IOException {
    ZipInputStream stream = new ZipInputStream(input);
    boolean finished = false;
    int parsed = 0;
    do {
      ZipEntry entry = stream.getNextEntry();
      if (entry != null) {
        Log.d(TAG, "Parsing: " + entry.getName());
        if (parser.canParse(entry)) {
          listener.onOpenFile(entry.getName());
          int found = parser.parse(stream);
          listener.onCompleteFile(found);
          parsed++;
        }
        stream.closeEntry();
      } else {
        finished = true;
      }
    } while (!finished && parsed < maxFiles);
  }

  public void doExtraction(InputStream input, StreamParser parser) throws IOException {
    doExtraction(input, parser, Integer.MAX_VALUE);
  }

  public interface StreamParser {
    boolean canParse(ZipEntry entry);
    int parse(InputStream stream) throws IOException;

  }

}
