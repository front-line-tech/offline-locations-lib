package com.flt.coecclient.db;

import com.flt.coecclient.db.dao.CoecMicroTaskingDao;
import com.flt.coecclient.db.dao.CoecOperativeDao;
import com.flt.coecclient.db.dao.CoecOutcomeDao;
import com.flt.coecclient.db.entities.CoecOperative;
import com.flt.coecclient.db.entities.CoecMicroTasking;
import com.flt.coecclient.db.entities.CoecOutcome;
import com.flt.coecclient.db.util.Converters;

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