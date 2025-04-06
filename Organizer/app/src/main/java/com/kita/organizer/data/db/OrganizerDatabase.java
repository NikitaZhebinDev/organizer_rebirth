package com.kita.organizer.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.kita.organizer.data.dao.ListDao;
import com.kita.organizer.data.dao.TaskDao;
import com.kita.organizer.data.entity.ListEntity;
import com.kita.organizer.data.entity.TaskEntity;

/**
 * The Room database for the organizer app.
 * exportSchema - will tell Room to export the schema files into the schemas directory, set in project's build.gradle (Module-level).
 */
@Database(entities = {TaskEntity.class, ListEntity.class}, version = 1, exportSchema = true)
public abstract class OrganizerDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();
    public abstract ListDao listDao();

    private static volatile OrganizerDatabase INSTANCE;

    public static OrganizerDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (OrganizerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    OrganizerDatabase.class, "organizer_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
