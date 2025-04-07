package com.kita.organizer.ui.completedtasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kita.organizer.R;
import com.kita.organizer.data.dao.CompletedTaskDao;
import com.kita.organizer.data.db.OrganizerDatabase;
import com.kita.organizer.data.entity.CompletedTaskEntity;

import java.util.List;

public class CompletedTasksFragment extends Fragment {

    private RecyclerView recyclerView;
    private CompletedTaskAdapter adapter;
    private CompletedTaskDao completedTaskDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout (make sure to create fragment_completed_tasks.xml)
        View view = inflater.inflate(R.layout.fragment_completed_tasks, container, false);

        // Initialize RecyclerView and set its layout manager and adapter.
        recyclerView = view.findViewById(R.id.recycler_view_completed_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CompletedTaskAdapter();
        recyclerView.setAdapter(adapter);

        // Retrieve our CompletedTaskDao from the database.
        // Adjust OrganizerDatabase.getInstance(getContext()) as needed for your app.
        OrganizerDatabase database = OrganizerDatabase.getInstance(getContext());
        completedTaskDao = database.completedTaskDao();
        loadCompletedTasksFromDatabase();

        return view;
    }

    /**
     * Loads all completed tasks from the database on a background thread
     * and updates the adapter on the UI thread.
     */
    private void loadCompletedTasksFromDatabase() {
        new Thread(() -> {
            List<CompletedTaskEntity> completedTasks = completedTaskDao.getAllCompletedTasks();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.setTasks(completedTasks));
            }
        }).start();
    }
}
