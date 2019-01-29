package com.flt.liblookupprovider.extraction;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class OpenNamesFileFinder {

  private Context context;
  private String pkgName;

  public OpenNamesFileFinder(Context context) {
    this.context = context;
    pkgName = context.getApplicationInfo().packageName;
  }

  public String nameBestOpenNamesZipCandidate() throws IOException {
    List<File> expansionPaths = findOpenNamesZipCandidateExpansionFiles();
    if (expansionPaths.size() > 0) {
      File best = bestOpenNamesExpansionFile(expansionPaths);
      return best.getName();
    } else {
      List<String> assetPaths = findOpenNamesZipCandidateAssets();
      if (assetPaths.size() > 0) {
        String best = bestOpenNamesAssetFile(assetPaths);
        return best;
      } else {
        return null;
      }
    }
  }

  public InputStream getBestOpenNamesZipCandidate() throws IOException {
    List<File> expansionPaths = findOpenNamesZipCandidateExpansionFiles();
    if (expansionPaths.size() > 0) {
      File best = bestOpenNamesExpansionFile(expansionPaths);
      return new FileInputStream(best);

    } else {
      List<String> assetPaths = findOpenNamesZipCandidateAssets();
      if (assetPaths.size() > 0) {
        String best = bestOpenNamesAssetFile(assetPaths);
        AssetManager mgr = context.getAssets();
        return mgr.open(best);
      } else {
        return null;
      }
    }
  }

  private File bestOpenNamesExpansionFile(List<File> options) {
    int best_version = Integer.MIN_VALUE;
    File best_file = null;
    for (File option : options) {
      int optionVersion = ObbFilenameUnderstander.retrieveMainObbVersion(option.getName());
      if (best_file == null || optionVersion > best_version) {
        best_file = option;
        best_version = optionVersion;
      }
    }
    return best_file;
  }

  private String bestOpenNamesAssetFile(List<String> options) {
    // sort alphabetically, last item is 'latest' as they are all prefixed: YYYY_MM_
    Collections.sort(options);
    return options.get(options.size()-1);
  }

  private boolean isMyObb(String filename) {
    return ObbFilenameUnderstander.isMainObbForPackage(filename, pkgName);
  }

  private List<File> findOpenNamesZipCandidateExpansionFiles() throws IOException {
    File dir = context.getObbDir();
    List<File> candidates = new LinkedList<>();
    for (File candidate : dir.listFiles()) {
      if (isMyObb(candidate.getName())) {
        candidates.add(candidate);
      }
    }
    return candidates;
  }

  private List<String> findOpenNamesZipCandidateAssets() throws IOException {
    String[] paths = context.getAssets().list("");
    List<String> goodPaths = new LinkedList<>();
    for (String path : paths) {
      if (path.toLowerCase().endsWith(".zip") && path.toLowerCase().contains("opname_csv")) {
        goodPaths.add(path);
      }
    }
    return goodPaths;
  }



}
