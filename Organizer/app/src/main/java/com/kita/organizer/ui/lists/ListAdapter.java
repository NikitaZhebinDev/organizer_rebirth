package com.kita.organizer.ui.lists;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kita.organizer.R;
import com.kita.organizer.data.entity.ListEntity;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    private List<ListEntity> listItems = new ArrayList<>();

    public void setLists(List<ListEntity> listItems) {
        this.listItems = listItems;
        notifyDataSetChanged();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {
        // Text view for displaying the list name
        TextView listName;
        // Buttons for edit and delete actions
        ImageButton editButton;
        ImageButton deleteButton;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            listName = itemView.findViewById(R.id.list_name);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }

        public void bind(ListEntity listEntity) {
            // Set the list name text
            listName.setText(listEntity.getName());
            // TODO: Add click listeners for editButton and deleteButton if needed.
        }
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the new item_list.xml layout (ensure the file name matches)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.bind(listItems.get(position));

        // Animate item appearance
        holder.itemView.setAlpha(0f);
        holder.itemView.setTranslationY(50f);
        holder.itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Add touch listener for scale (bounce) animation
        holder.itemView.setOnTouchListener((view, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Scale down very gently
                    view.animate()
                            .scaleX(0.98f)
                            .scaleY(0.98f)
                            .setDuration(120)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                    break;
                case MotionEvent.ACTION_UP:
                    // On release, do a smooth pop (slightly overscaled) then settle back
                    view.animate()
                            .scaleX(1.02f)
                            .scaleY(1.02f)
                            .setDuration(75)
                            .withEndAction(() -> view.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(150)
                                    .setInterpolator(new OvershootInterpolator(2f))
                                    .withEndAction(() -> view.performClick()) // Accessibility: call performClick()
                                    .start())
                            .start();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    // If cancelled, return smoothly to normal scale
                    view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(150)
                            .setInterpolator(new DecelerateInterpolator())
                            .withEndAction(() -> view.performClick())
                            .start();
                    break;
            }
            return false; // Let ripple and click events proceed.
        });

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
}
