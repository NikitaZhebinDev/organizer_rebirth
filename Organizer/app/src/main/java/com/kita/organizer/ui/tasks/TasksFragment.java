package com.kita.organizer.ui.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kita.organizer.NewTaskActivity;
import com.kita.organizer.R;
import com.kita.organizer.databinding.FragmentTasksBinding;

public class TasksFragment extends Fragment {

    private FragmentTasksBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TasksViewModel tasksViewModel =
                new ViewModelProvider(this).get(TasksViewModel.class);

        binding = FragmentTasksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        tasksViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        Animation animClickFloatBtn = AnimationUtils.loadAnimation(getContext(), R.anim.press_float_btn);

        FloatingActionButton btnNewTask = binding.btnNewTask;
        btnNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnNewTask.startAnimation(animClickFloatBtn);
                // Start the new task activity
                Intent intent = new Intent(getActivity(), NewTaskActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}