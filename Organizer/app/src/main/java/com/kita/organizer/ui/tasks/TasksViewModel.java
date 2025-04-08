package com.kita.organizer.ui.tasks;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kita.organizer.data.db.OrganizerDatabase;
import com.kita.organizer.data.entity.CompletedTaskEntity;
import com.kita.organizer.data.entity.ListEntity;
import com.kita.organizer.data.entity.TaskEntity;

import java.time.LocalDate;
import java.util.List;

public class TasksViewModel extends ViewModel {

    private final MutableLiveData<List<TaskEntity>> taskList = new MutableLiveData<>();

    public LiveData<List<TaskEntity>> getTasks() {
        return taskList;
    }

    public void loadTasks(Context context) {
        new Thread(() -> {
            List<TaskEntity> tasks = OrganizerDatabase
                    .getInstance(context)
                    .taskDao()
                    .getAllTasks();
            taskList.postValue(tasks);
        }).start();
    }

    // New method to complete a task
    public void completeTask(Context context, TaskEntity taskEntity) {
        new Thread(() -> {
            // Get list name for the task
            ListEntity listEntity = OrganizerDatabase.getInstance(context)
                    .listDao()
                    .getById(taskEntity.getListId());
            String listName = (listEntity != null) ? listEntity.getName() : "";

            // Create CompletedTaskEntity
            CompletedTaskEntity completedTask = new CompletedTaskEntity(
                    taskEntity.getText(),
                    taskEntity.getDate(),
                    taskEntity.getTime(),
                    taskEntity.getRepeatOption(),
                    taskEntity.getListId(),
                    listName,
                    LocalDate.now()
            );

            // Insert completed task and delete the original task
            OrganizerDatabase database = OrganizerDatabase.getInstance(context);
            database.completedTaskDao().insert(completedTask);
            database.taskDao().delete(taskEntity);

            // Reload tasks to update the UI
            loadTasks(context);
        }).start();
    }
}
