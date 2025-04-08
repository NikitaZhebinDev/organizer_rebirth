package com.kita.organizer.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.kita.organizer.data.entity.ListEntity;

import java.util.List;

@Dao
public interface ListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ListEntity listEntity); // Change IGNORE to REPLACE

    @Update
    void update(ListEntity listEntity);

    @Query("SELECT * FROM list")
    List<ListEntity> getAll();

    @Query("SELECT * FROM list")
    LiveData<List<ListEntity>> getAllLive();

    @Query("SELECT * FROM list WHERE name = :name LIMIT 1")
    ListEntity getByName(String name);

    @Query("SELECT * FROM list WHERE id = :id LIMIT 1")
    ListEntity getById(int id);

    @Delete
    void delete(ListEntity listEntity);
}

