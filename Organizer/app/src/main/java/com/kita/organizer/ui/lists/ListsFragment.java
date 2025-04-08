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
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ListsViewModel listsViewModel =
                new ViewModelProvider(
                        this,
                        ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
                ).get(ListsViewModel.class);

        binding = FragmentListsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*final TextView textView = binding.textGallery;
        listsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/

        // Set up RecyclerView and fill it with lists
        recyclerView = root.findViewById(R.id.recyclerViewLists);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listAdapter = new ListAdapter(OrganizerDatabase.getInstance(getContext()).listDao());
        recyclerView.setAdapter(listAdapter);

        listsViewModel.getAllLists().observe(getViewLifecycleOwner(), listEntities -> {
            listAdapter.setLists(listEntities);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}