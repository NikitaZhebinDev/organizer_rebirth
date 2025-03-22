package com.kita.organizer.ui.finishedtasks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FinishedTasksViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public FinishedTasksViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is finishedtasks fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}