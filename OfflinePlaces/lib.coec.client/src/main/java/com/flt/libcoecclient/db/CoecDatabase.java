package com.flt.libcoecclient.db;

import com.flt.libcoecclient.db.dao.CoecMicroTaskingDao;
import com.flt.libcoecclient.db.dao.CoecOperativeDao;
import com.flt.libcoecclient.db.dao.CoecOutcomeDao;
import com.flt.libcoecclient.db.entities.CoecOperative;
import com.flt.libcoecclient.db.entities.CoecMicroTasking;
import com.flt.libcoecclient.db.entities.CoecOutcome;
import com.flt.libcoecclient.db.util.Converters;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {
    CoecMicroTasking.class,
    CoecOutcome.class,
    CoecOperative.class },
    version = 5,
    exportSchema = false)
@TypeConverters({Converters.class})
public abstract class CoecDatabase extends RoomDatabase {

  public abstract CoecMicroTaskingDao taskDao();

  public abstract CoecOutcomeDao outcomeDao();

  public abstract CoecOperativeDao operativeDao();
}