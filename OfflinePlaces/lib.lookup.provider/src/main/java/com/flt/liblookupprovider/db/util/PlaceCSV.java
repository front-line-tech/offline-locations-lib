package com.flt.liblookupprovider.db.util;

public enum PlaceCSV {

  ID(0),
  NAMES_URI(1),
  NAME1(2),
  NAME1_LANG(3),
  NAME2(4),
  NAME2_LANG(5),
  TYPE(6),
  LOCAL_TYPE(7),
  GEOMETRY_X(8),
  GEOMETRY_Y(9),
  MOST_DETAIL_VIEW_RES(10),
  LEAST_DETAIL_VIEW_RES(11),
  MBR_XMIN(12),
  MBR_YMIN(13),
  MBR_XMAX(14),
  MBR_YMAX(15),
  POSTCODE_DISTRICT(15),
  POSTCODE_DISTRICT_URI(16),
  POPULATED_PLACE(17),
  POPULATED_PLACE_URI(18),
  POPULATED_PLACE_TYPE(19),
  DISTRICT_BOROUGH(20),
  DISTRICT_BOROUGH_URI(21),
  DISTRICT_BOROUGH_TYPE(22),
  COUNTY_UNITARY(23),
  COUNTY_UNITARY_URI(24),
  COUNTY_UNITARY_TYPE(25),
  REGION(26),
  REGION_URI(27),
  COUNTRY(28),
  COUNTRY_URI(29),
  RELATED_SPATIAL_OBJECT(30),
  SAME_AS_DBPEDIA(31),
  SAME_AS_GEONAMES(32);

  private int col_index;

  PlaceCSV(int col_index) {
    this.col_index = col_index;
  }

  public int getCol() {
    return col_index;
  }
}
