package com.flt.liblookupprovider.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import com.flt.liblookupprovider.db.entities.Place;

import java.util.Collection;
import java.util.List;

@Dao
public interface PlacesDao {

  @Query("SELECT * FROM place WHERE (NAME1 LIKE :search OR NAME2 LIKE :search) ORDER BY NAME1")
  Cursor get_simple_like_cursor(String search);

  @Query("SELECT * FROM place WHERE (NAME1 LIKE :search OR NAME2 LIKE :search) ORDER BY NAME1")
  List<Place> get_simple_like_list(String search);

  @Query("SELECT * FROM place WHERE row_id LIKE :row_id ORDER BY NAME1")
  Cursor get_item_cursor(long row_id);

  @Query("SELECT * FROM place WHERE ID LIKE :ID ORDER BY NAME1")
  Cursor get_item_cursor(String ID);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(Place... places);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(Collection<Place> places);

  @Query("DELETE from place")
  void delete_all();

  @Query("SELECT COUNT(*) FROM place")
  LiveData<Integer> count_live();

  @Query("SELECT COUNT(*) FROM place")
  Integer count();
}
