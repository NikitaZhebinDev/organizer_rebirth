package com.kita.organizer.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.kita.organizer.data.dao.TaskDao;
import com.kita.organizer.data.entity.Task;

@Database(entities = {Task.class}, version = 1)
public abstract class OrganizerDatabase extends RoomDatabase {
    private static volatile OrganizerDatabase INSTANCE;

    public abstract TaskDao taskDao();

    public static OrganizerDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (OrganizerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    OrganizerDatabase.class, "task_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
