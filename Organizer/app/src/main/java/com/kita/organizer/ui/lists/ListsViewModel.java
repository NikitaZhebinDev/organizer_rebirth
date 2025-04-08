package com.kita.organizer.ui.lists;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kita.organizer.data.db.OrganizerDatabase;
import com.kita.organizer.data.entity.ListEntity;

import java.util.List;

public class ListsViewModel extends AndroidViewModel {

    private final LiveData<List<ListEntity>> allLists;

    public ListsViewModel(@NonNull Application application) {
        super(application);
        // Use getAllLive() from ListDao to observe the list of lists
        allLists = OrganizerDatabase.getInstance(application).listDao().getAllLive();
    }

    public LiveData<List<ListEntity>> getAllLists() {
        return allLists;
    }
}
