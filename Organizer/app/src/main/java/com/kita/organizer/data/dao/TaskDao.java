package com.kita.organizer.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kita.organizer.data.entity.TaskEntity;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(TaskEntity taskEntity);

    @Update
    void update(TaskEntity taskEntity);

    @Query("SELECT * FROM Task")
    List<TaskEntity> getAllTasks();

    @Delete
    void delete(TaskEntity task);
}
