package com.flt.liblookupprovider.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.flt.liblookupprovider.db.dao.PlacesDao;
import com.flt.liblookupprovider.db.entities.Place;
import com.flt.liblookupprovider.db.util.Converters;

@Database(
    entities = { Place.class },
    version = 2,
    exportSchema = false)
@TypeConverters({Converters.class})
public abstract class OpenNamesDb extends RoomDatabase {

  public abstract PlacesDao getPlacesDao();

}
