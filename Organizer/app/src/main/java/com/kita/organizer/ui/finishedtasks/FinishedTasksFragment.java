package com.kita.organizer.ui.finishedtasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kita.organizer.databinding.FragmentFinishedTasksBinding;

public class FinishedTasksFragment extends Fragment {

    private FragmentFinishedTasksBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FinishedTasksViewModel finishedTasksViewModel =
                new ViewModelProvider(this).get(FinishedTasksViewModel.class);

        binding = FragmentFinishedTasksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSlideshow;
        finishedTasksViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}