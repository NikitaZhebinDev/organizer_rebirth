package com.kita.organizer.ui.lists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kita.organizer.R;
import com.kita.organizer.data.db.OrganizerDatabase;
import com.kita.organizer.databinding.FragmentListsBinding;

public class ListsFragment extends Fragment {

    private FragmentListsBinding binding;
    private ListsAdapter listsAdapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        /* 1.  ViewModel as before */
        ListsViewModel viewModel = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory
                        .getInstance(requireActivity().getApplication()))
                .get(ListsViewModel.class);

        /* 2.  ViewBinding */
        binding = FragmentListsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /* 3.  RecyclerView + adapter */
        RecyclerView rv = root.findViewById(R.id.recyclerViewLists);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        listsAdapter = new ListsAdapter(
                OrganizerDatabase.getInstance(requireContext()).listDao());
        rv.setAdapter(listsAdapter);

        /* 4.  Observe data and push to adapter with submitList() */
        viewModel.getAllLists()
                .observe(getViewLifecycleOwner(),
                        listsAdapter::submitList);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
