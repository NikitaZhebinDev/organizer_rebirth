package com.kita.organizer.ui.lists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kita.organizer.R;
import com.kita.organizer.data.db.OrganizerDatabase;
import com.kita.organizer.data.entity.ListEntity;
import com.kita.organizer.databinding.FragmentListsBinding;

import java.util.List;

public class ListsFragment extends Fragment {

    private FragmentListsBinding binding;
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ListsViewModel listsViewModel =
                new ViewModelProvider(this).get(ListsViewModel.class);

        binding = FragmentListsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*final TextView textView = binding.textGallery;
        listsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/

        // Set up RecyclerView and fill it with lists
        recyclerView = root.findViewById(R.id.recyclerViewLists);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listAdapter = new ListAdapter(OrganizerDatabase.getInstance(getContext()).listDao());
        recyclerView.setAdapter(listAdapter);
        loadListsFromDatabase();

        return root;
    }

    /**
     * Load lists from the database and update the RecyclerView adapter
     */
    private void loadListsFromDatabase() {
        new Thread(() -> {
            List<ListEntity> allLists = OrganizerDatabase
                    .getInstance(getContext())
                    .listDao()
                    .getAll(); // sort/filter if needed

            requireActivity().runOnUiThread(() -> {
                listAdapter.setLists(allLists);
            });
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}