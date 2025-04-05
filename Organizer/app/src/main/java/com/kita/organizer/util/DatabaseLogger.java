package com.kita.organizer.util;

import android.content.Context;
import android.util.Log;

import com.kita.organizer.data.dao.ListDao;
import com.kita.organizer.data.dao.TaskDao;
import com.kita.organizer.data.db.OrganizerDatabase;
import com.kita.organizer.data.entity.ListEntity;
import com.kita.organizer.data.entity.Task;

import java.util.List;

public class DatabaseLogger {

    // Method to log all lists in the database
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

    // Method to log all tasks in the database
    public static void logTasksInDatabase(String tag, Context context) {
        new Thread(() -> {
            OrganizerDatabase db = OrganizerDatabase.getInstance(context);
            TaskDao taskDao = db.taskDao();
            List<Task> tasks = taskDao.getAllTasks();

            // Log each task found
            for (Task task : tasks) {
                Log.d(tag, "Task present: " +
                        "ID: " + task.getId() + ", " +
                        "Text: " + task.getText() + ", " +
                        "Date: " + task.getDate() + ", " +
                        "Time: " + task.getTime() + ", " +
                        "Repeat: " + task.getRepeatOption() + ", " +
                        "List ID: " + task.getListId());
            }
            if (tasks.isEmpty()) {
                Log.d(tag, "No tasks found in the database.");
            }
        }).start();
    }
}
