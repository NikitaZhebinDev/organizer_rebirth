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
import com.kita.organizer.databinding.FragmentTasksBinding;

public class TasksFragment extends Fragment {
    private FragmentTasksBinding binding;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private TasksViewModel tasksViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tasksViewModel = new ViewModelProvider(this).get(TasksViewModel.class);

        // Set up the FloatingActionButton with animation
        Animation animClickFloatBtn = AnimationUtils.loadAnimation(getContext(), R.anim.press_float_btn);
        animClickFloatBtn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // You can add any start animation logic here, if needed.
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // You can add any logic for repeat animation if necessary.
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Start NewTaskActivity after the animation ends
                Intent intent = new Intent(getActivity(), NewTaskActivity.class);
                startActivity(intent);
            }
        });

        // Set click listener on the FloatingActionButton
        FloatingActionButton btnNewTask = binding.btnNewTask;
        btnNewTask.setOnClickListener(v -> btnNewTask.startAnimation(animClickFloatBtn));

        // Set up RecyclerView with TaskAdapter
        taskAdapter = new TaskAdapter();
        taskAdapter.setTasksViewModel(tasksViewModel); // Pass ViewModel to adapter

        recyclerView = root.findViewById(R.id.recycler_view_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(taskAdapter);

        // Observe LiveData for automatic UI updates
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
        tasksViewModel.loadTasks(requireContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

