package com.kita.organizer.utils;

import android.content.Context;
import android.util.Log;

import com.kita.organizer.data.dao.ListDao;
import com.kita.organizer.data.dao.TaskDao;
import com.kita.organizer.data.db.OrganizerDatabase;
import com.kita.organizer.data.entity.ListEntity;
import com.kita.organizer.data.entity.TaskEntity;

import java.util.List;

public class DatabaseLogger {

    /**
     * Logs all lists in the database.
     * @param tag - tag to use for logging
     * @param context - context to use for database access
     */
    public static void logListsInDatabase(String tag, Context context) {
        new Thread(() -> {
            OrganizerDatabase db = OrganizerDatabase.getInstance(context);
            ListDao listDao = db.listDao();
            List<ListEntity> lists = listDao.getAll();

            // Log each list found
            for (ListEntity list : lists) {
                Log.d(tag, "List present: " + list.getName());
            }
            if (lists.isEmpty()) {
                Log.d(tag, "No lists found in the database.");
            }
        }).start();
    }

    /**
     * Logs all tasks in the database.
     * @param tag - tag to use for logging
     * @param context - context to use for database access
     */
    public static void logTasksInDatabase(String tag, Context context) {
        new Thread(() -> {
            OrganizerDatabase db = OrganizerDatabase.getInstance(context);
            TaskDao taskDao = db.taskDao();
            List<TaskEntity> taskEntities = taskDao.getAllTasks();

            // Log each task found
            for (TaskEntity taskEntity : taskEntities) {
                Log.d(tag, "Task present: " +
                        "ID: " + taskEntity.getId() + ", " +
                        "Text: " + taskEntity.getText() + ", " +
                        "Date: " + taskEntity.getDate() + ", " +
                        "Time: " + taskEntity.getTime() + ", " +
                        "Repeat: " + taskEntity.getRepeatOption() + ", " +
                        "List ID: " + taskEntity.getListId());
            }
            if (taskEntities.isEmpty()) {
                Log.d(tag, "No tasks found in the database.");
            }
        }).start();
    }
}
