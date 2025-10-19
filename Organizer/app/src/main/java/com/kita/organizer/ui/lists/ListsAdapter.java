package com.kita.organizer.ui.lists;

import static com.kita.organizer.utils.DialogUtils.wrapInVerticalContainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DiffUtil;

import com.kita.organizer.R;
import com.kita.organizer.data.dao.ListDao;
import com.kita.organizer.data.entity.ListEntity;
import com.kita.organizer.utils.AnimationUtils;
import com.kita.organizer.utils.DialogUtils;
import com.kita.organizer.utils.KeyboardUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Adapter powered by {@link androidx.recyclerview.widget.ListAdapter}: background diff-calculation,
 * automatic granular notifyItem…() calls, and free animations.
 */
public class ListsAdapter
        extends androidx.recyclerview.widget.ListAdapter<ListEntity, ListsAdapter.ListViewHolder> {

    private final ListDao listDao;

    public ListsAdapter(ListDao listDao) {
        super(DIFF);
        this.listDao = listDao;
    }

    static class ListViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {

        TextView listName;
        ImageButton editButton;
        ImageButton deleteButton;
        private final ListsAdapter adapter;

        ListViewHolder(@NonNull View itemView, ListsAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            listName = itemView.findViewById(R.id.list_name);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }

        void bind(ListEntity entity, ListDao dao) {
            listName.setText(entity.getName());

            // Check if this list is one of the default lists.
            if ("Default".equals(entity.getName()) || "Personal".equals(entity.getName())) { // TODO inter-tion
                // Hide the edit and delete buttons for default lists.
                editButton.setVisibility(View.INVISIBLE);
                deleteButton.setVisibility(View.INVISIBLE);
            } else {
                // Ensure the buttons are visible in case this view holder was recycled.
                editButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);

                // Set up click listeners when the list is not default.
                editButton.setOnClickListener(v ->
                        adapter.showRenameDialog(v.getContext(), entity, getBindingAdapterPosition(), dao));
                deleteButton.setOnClickListener(v ->
                        adapter.showDeleteDialog(v.getContext(), entity, getBindingAdapterPosition(), dao));
            }
        }

    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int vt) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        return new ListViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder h, int pos) {
        h.bind(getItem(pos), listDao);

        /*AnimationUtils.animateItemAppearance(h.itemView);*/
        AnimationUtils.setBounceTouchAnimation(h.itemView);
    }

    private void showRenameDialog(Context ctx, ListEntity entity,
                                  int adapterPos, ListDao dao) {

        EditText input = DialogUtils.createStyledEditText(
                ctx, "Enter new name", entity.getName());

        new AlertDialog.Builder(ctx)
                .setTitle("Rename list")
                .setView(wrapInVerticalContainer(input))
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Rename", (d, w) -> {
                    String newName = input.getText().toString().trim();
                    if (newName.isEmpty()) return;

                    entity.setName(newName);

                    Executors.newSingleThreadExecutor()
                            .execute(() -> dao.update(entity));

                    notifyItemChanged(adapterPos);   // single-row refresh
                })
                .show();

        KeyboardUtils.showKeyboard(ctx, input);
    }

    private void showDeleteDialog(Context ctx, ListEntity entity,
                                  int adapterPos, ListDao dao) {

        String msg = "Are you sure you want to delete \"" + entity.getName() + "\"?\n\n"
                + "All tasks in this list will also be deleted.";

        new AlertDialog.Builder(ctx)
                .setTitle("Delete list?")
                .setMessage(msg)
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", (d, w) -> {

                    /* 1. delete from DB on background thread */
                    Executors.newSingleThreadExecutor()
                            .execute(() -> dao.delete(entity));

                    /* 2. optimistically remove it from UI list */
                    List<ListEntity> newList = new ArrayList<>(getCurrentList());
                    newList.remove(entity);
                    submitList(newList);              // ListsAdapter magic!
                })
                .show();
    }

    /* ─────────────────────────────────────────────
     * DiffUtil.ItemCallback
     * ────────────────────────────────────────────*/
    private static final DiffUtil.ItemCallback<ListEntity> DIFF =
            new DiffUtil.ItemCallback<ListEntity>() {

                /* identity comparison (usually primary key) */
                @Override
                public boolean areItemsTheSame(@NonNull ListEntity a,
                                               @NonNull ListEntity b) {
                    return a.getId() == b.getId();
                }

                /* content comparison (displayed fields) */
                @Override
                public boolean areContentsTheSame(@NonNull ListEntity a,
                                                  @NonNull ListEntity b) {
                    return a.equals(b);   // ensure equals() checks every UI field
                }
            };
}
