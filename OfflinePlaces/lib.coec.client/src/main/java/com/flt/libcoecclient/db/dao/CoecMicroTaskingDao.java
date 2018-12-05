package com.flt.libcoecclient.db.dao;

import com.flt.libcoecclient.db.entities.CoecMicroTasking;
import com.flt.libcoecclient.db.pojos.CoecMicroTaskingHistory;

import java.util.List;
import java.util.UUID;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface CoecMicroTaskingDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert_all(List<CoecMicroTasking> taskings);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert_all(CoecMicroTasking... taskings);

  @Query("SELECT * FROM coec_microtasking ORDER BY begins")
  LiveData<List<CoecMicroTasking>> get_all();

  @Query("SELECT * FROM coec_microtasking WHERE (at_latitude BETWEEN :north_lat AND :south_lat) AND (at_longitude BETWEEN :west_lng AND :east_lng) AND NOT marked_complete ORDER BY begins")
  LiveData<List<CoecMicroTasking>> get_incomplete_for_area(double north_lat, double west_lng, double south_lat, double east_lng);

  @Query("SELECT * FROM coec_microtasking WHERE (at_latitude BETWEEN :north_lat AND :south_lat) AND (at_longitude BETWEEN :west_lng AND :east_lng) AND marked_complete ORDER BY begins")
  LiveData<List<CoecMicroTasking>> get_complete_for_area(double north_lat, double west_lng, double south_lat, double east_lng);

  @Transaction
  @Query("SELECT * FROM coec_microtasking WHERE tasking_uuid LIKE :tasking_uuid")
  LiveData<CoecMicroTaskingHistory> get_history_for_tasking(UUID tasking_uuid);

  @Query("SELECT COUNT(*) FROM coec_microtasking")
  int count();

}
