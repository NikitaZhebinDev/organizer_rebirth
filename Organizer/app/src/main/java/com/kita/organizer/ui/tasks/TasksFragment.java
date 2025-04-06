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
import com.kita.organizer.data.entity.Task;
import com.kita.organizer.databinding.FragmentTasksBinding;

import java.util.List;

public class TasksFragment extends Fragment {

    private FragmentTasksBinding binding;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TasksViewModel tasksViewModel =
                new ViewModelProvider(this).get(TasksViewModel.class);

        binding = FragmentTasksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*final TextView textView = binding.textHome;
        tasksViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/

        Animation animClickFloatBtn = AnimationUtils.loadAnimation(getContext(), R.anim.press_float_btn);
        animClickFloatBtn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Start the new task activity
                Intent intent = new Intent(getActivity(), NewTaskActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        FloatingActionButton btnNewTask = binding.btnNewTask;
        btnNewTask.setOnClickListener(v -> {
            btnNewTask.startAnimation(animClickFloatBtn);
        });

        // Set up RecyclerView and fill it with tasks
        recyclerView = root.findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter();
        recyclerView.setAdapter(taskAdapter);
        loadTasksFromDatabase();

        return root;
    }

    /**
     * Load tasks from the database and update the RecyclerView adapter
     */
    private void loadTasksFromDatabase() {
        new Thread(() -> {
            OrganizerDatabase db = OrganizerDatabase.getInstance(requireContext());
            List<Task> taskList = db.taskDao().getAllTasks();  // sort/filter if needed

            requireActivity().runOnUiThread(() -> {
                taskAdapter.setTasks(taskList);
            });
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}