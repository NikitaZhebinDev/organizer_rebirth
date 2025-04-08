package com.kita.organizer.ui.tasks;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kita.organizer.data.db.OrganizerDatabase;
import com.kita.organizer.data.entity.TaskEntity;

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
}
