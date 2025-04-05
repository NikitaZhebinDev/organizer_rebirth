package com.kita.organizer.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.kita.organizer.data.entity.ListEntity;

import java.util.List;

@Dao
public interface ListDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ListEntity listEntity);

    @Query("SELECT * FROM list")
    List<ListEntity> getAll();

    @Query("SELECT * FROM list WHERE name = :name LIMIT 1")
    ListEntity getByName(String name);

    @Delete
    void delete(ListEntity listEntity);
}
