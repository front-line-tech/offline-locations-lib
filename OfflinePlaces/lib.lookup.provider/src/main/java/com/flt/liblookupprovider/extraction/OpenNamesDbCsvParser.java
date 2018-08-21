package com.flt.liblookupprovider.extraction;

import com.flt.liblookupprovider.db.OpenNamesDb;
import com.flt.liblookupprovider.db.entities.Place;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;

import static com.flt.liblookupprovider.db.util.EntityFactory.createPlaceFromCSV;

public class OpenNamesDbCsvParser implements OpenNamesExtractor.StreamParser {

  OpenNamesDb db;

  public OpenNamesDbCsvParser(OpenNamesDb db) {
    this.db = db;
  }

  @Override
  public boolean canParse(ZipEntry entry) {
    return
      entry.getName().toLowerCase().contains("data") &&
      entry.getName().toLowerCase().endsWith(".csv");
  }

  @Override
  public int parse(InputStream stream) throws IOException {
    Reader reader = new InputStreamReader(stream);
    CSVReader csvReader = new CSVReader(reader);

    boolean finished = false;
    List<Place> places = new LinkedList<>();

    do {
      String[] line = csvReader.readNext();
      if (line != null) {
        places.add(createPlaceFromCSV(line));
      } else {
        finished = true;
      }
    } while (!finished);

    db.getPlacesDao().insert(places);
    return places.size();
  }
}
