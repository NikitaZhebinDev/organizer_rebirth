package com.kita.organizer.ui.lists;

import static com.kita.organizer.utils.DialogUtils.wrapInVerticalContainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.kita.organizer.R;
import com.kita.organizer.data.dao.ListDao;
import com.kita.organizer.data.entity.ListEntity;
import com.kita.organizer.utils.AnimationUtils;
import com.kita.organizer.utils.DialogUtils;
import com.kita.organizer.utils.KeyboardUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    private List<ListEntity> listItems = new ArrayList<>();
    private final ListDao listDao;

    public ListAdapter(ListDao listDao) {
        this.listDao = listDao;
    }

    public void setLists(List<ListEntity> listItems) {
        this.listItems = listItems;
        notifyDataSetChanged();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView listName;
        ImageButton editButton;
        ImageButton deleteButton;
        private final ListAdapter adapter;

        public ListViewHolder(@NonNull View itemView, ListAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            listName = itemView.findViewById(R.id.list_name);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }

        public void bind(ListEntity listEntity, ListDao listDao) {
            listName.setText(listEntity.getName());

            // set up click listeners for edit and delete buttons
            editButton.setOnClickListener(view -> {
                adapter.showListRenameDialog(view.getContext(), listEntity, getAdapterPosition(), listDao);
            });
            deleteButton.setOnClickListener(view -> {
                adapter.showListDeleteDialog(view.getContext(), listEntity, getAdapterPosition(), listDao);
            });
        }
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        return new ListViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.bind(listItems.get(position), listDao);

        // Use the extracted animation utility methods
        AnimationUtils.animateItemAppearance(holder.itemView);
        AnimationUtils.setBounceTouchAnimation(holder.itemView);
    }


    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void showListRenameDialog(Context context, ListEntity listEntity, int adapterPosition, ListDao listDao) {
        EditText listNameInput = DialogUtils.createStyledEditText(context, "Enter new name", listEntity.getName());

        new AlertDialog.Builder(context)
                .setTitle("Rename list")
                .setView(wrapInVerticalContainer(listNameInput))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Rename", (dialog, which) -> {
                    String newName = listNameInput.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        listEntity.setName(newName);

                        // Run update in a background thread
                        Executors.newSingleThreadExecutor().execute(() -> {
                            listDao.update(listEntity);
                        });

                        notifyItemChanged(adapterPosition);
                    }
                })
                .show();

        // Request focus for the input field and show the keyboard
        KeyboardUtils.showKeyboard(context, listNameInput);
    }


    public void showListDeleteDialog(Context context, ListEntity listEntity, int adapterPosition, ListDao listDao) {
        // Add a message warning about task deletion
        String message = "Are you sure you want to delete the list: " + listEntity.getName() + "?\n\n" +
                "Please note that all tasks within this list will also be deleted.";

        new AlertDialog.Builder(context)
                .setTitle("Delete list?")
                .setMessage(message) // Adding the message here
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Run deletion on a background thread
                    Executors.newSingleThreadExecutor().execute(() -> {
                        // todo Optionally, you can delete tasks here before deleting the list
                        // todo
                        //OrganizerDatabase.getInstance(context).taskDao().deleteByListId(listEntity.getId());

                        // Delete the list
                        listDao.delete(listEntity);
                    });

                    // Remove item from the list and notify the adapter
                    listItems.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);
                })
                .show();
    }

}
