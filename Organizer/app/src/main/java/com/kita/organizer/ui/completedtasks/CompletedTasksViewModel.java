package com.kita.organizer.ui.completedtasks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CompletedTasksViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CompletedTasksViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is completedtasks fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}