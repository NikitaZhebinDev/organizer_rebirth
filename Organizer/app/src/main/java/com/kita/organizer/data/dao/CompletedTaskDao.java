package com.kita.organizer.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kita.organizer.data.entity.CompletedTaskEntity;

import java.util.List;

@Dao
public interface CompletedTaskDao {

    @Insert
    void insert(CompletedTaskEntity completedTaskEntity);

    @Query("SELECT * FROM completed_task")
    List<CompletedTaskEntity> getAllCompletedTasks();

    @Query("SELECT * FROM completed_task WHERE id = :id LIMIT 1")
    CompletedTaskEntity getCompletedTaskById(int id);

    @Update
    void update(CompletedTaskEntity completedTaskEntity);

    @Delete
    void delete(CompletedTaskEntity completedTaskEntity);

    // Query completed tasks filtered by the list name
    @Query("SELECT * FROM completed_task WHERE listName = :listName")
    List<CompletedTaskEntity> getCompletedTasksByListName(String listName);

    // For clearing all completed tasks if needed
    @Query("DELETE FROM completed_task")
    void deleteAllCompletedTasks();
}

