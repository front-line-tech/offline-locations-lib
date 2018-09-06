package com.flt.liblookupprovider.extraction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Recognises obb files according to the pattern described in Android developer documentation:
 * [main|patch].<expansion-version>.<package-name>.obb
 *
 * eg. main.314159.com.example.app.obb
 *
 * Currently only works with main obb files.
 * TODO: extend this to accept and recognise patch obb files too.
 */
public class ObbFilenameUnderstander {
  private static String obb_regex = "^main\\.(\\d+).*\\.obb";

  public static boolean isMainObb(String filename) {
    return Pattern.matches(obb_regex, filename);
  }

  public static boolean isMainObbForPackage(String filename, String packageName) {
    return Pattern.matches(obb_regex, filename) && filename.contains(packageName);
  }

  public static String generateMainObbForPackage(String packageName, int version) {
    return "main." + version + "." + packageName + ".obb";
  }

  public static int retrieveMainObbVersion(String filename) {
    Pattern p = Pattern.compile(obb_regex);
    Matcher m = p.matcher(filename);
    if (m.matches()) {
      String s1 = m.group(0); // full match
      String s2 = m.group(1); // first specified match
      return Integer.parseInt(s2);
    } else {
      return Integer.MIN_VALUE;
    }
  }

}
