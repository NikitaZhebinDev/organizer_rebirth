package com.kita.organizer.ui.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kita.organizer.NewTaskActivity;
import com.kita.organizer.R;
import com.kita.organizer.data.db.OrganizerDatabase;
import com.kita.organizer.data.entity.TaskEntity;
import com.kita.organizer.databinding.FragmentTasksBinding;

import java.util.List;

public class TasksFragment extends Fragment {

    private FragmentTasksBinding binding;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private TasksViewModel tasksViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTasksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tasksViewModel = new ViewModelProvider(this).get(TasksViewModel.class);

        Animation animClickFloatBtn = AnimationUtils.loadAnimation(getContext(), R.anim.press_float_btn);
        animClickFloatBtn.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                // Start new task activity
                Intent intent = new Intent(getActivity(), NewTaskActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton btnNewTask = binding.btnNewTask;
        btnNewTask.setOnClickListener(v -> btnNewTask.startAnimation(animClickFloatBtn));

        // Set up RecyclerView and fill it with tasks
        recyclerView = root.findViewById(R.id.recycler_view_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter();
        recyclerView.setAdapter(taskAdapter);

        // Observe LiveData instead of manually loading
        tasksViewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> {
            taskAdapter.setTasks(tasks);
        });

        // Load tasks initially
        tasksViewModel.loadTasks(requireContext());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        tasksViewModel.loadTasks(requireContext()); // Refresh when coming back
    }

    /**
     * Load tasks from the database and update the RecyclerView adapter
     */
    private void loadTasksFromDatabase() {
        new Thread(() -> {
            OrganizerDatabase db = OrganizerDatabase.getInstance(requireContext());
            List<TaskEntity> taskEntityList = db.taskDao().getAllTasks();  // sort/filter if needed

            requireActivity().runOnUiThread(() -> {
                taskAdapter.setTasks(taskEntityList);
            });
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}