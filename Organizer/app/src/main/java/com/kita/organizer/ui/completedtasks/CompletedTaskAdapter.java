package com.kita.organizer.ui.completedtasks;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kita.organizer.R;
import com.kita.organizer.data.entity.CompletedTaskEntity;

import java.util.ArrayList;
import java.util.List;

public class CompletedTaskAdapter extends RecyclerView.Adapter<CompletedTaskAdapter.CompletedTaskViewHolder> {

    private List<CompletedTaskEntity> completedTaskEntities = new ArrayList<>();

    // Optional: Interface for completed task click events
    public interface OnCompletedTaskClickListener {
        void onCompletedTaskClick(CompletedTaskEntity task);
    }

    private OnCompletedTaskClickListener onTaskClickListener;

    // Setter for the completed task click listener
    public void setOnCompletedTaskClickListener(OnCompletedTaskClickListener listener) {
        this.onTaskClickListener = listener;
    }

    public void setTasks(List<CompletedTaskEntity> taskEntityList) {
        this.completedTaskEntities = taskEntityList;
        notifyDataSetChanged();
    }

    static class CompletedTaskViewHolder extends RecyclerView.ViewHolder {
        // References for UI elements for binding.
        // Note: Adjust these ids to match your item_completed_task.xml layout.
        TextView taskText;
        TextView taskDate;
        TextView listName;
        TextView completionDate;

        public CompletedTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskText = itemView.findViewById(R.id.task_text);
            taskDate = itemView.findViewById(R.id.task_date);
            listName = itemView.findViewById(R.id.list_name);
            completionDate = itemView.findViewById(R.id.completion_date);
        }

        public void bind(CompletedTaskEntity taskEntity, OnCompletedTaskClickListener listener) {
            // Set the task text.
            taskText.setText(taskEntity.getText());

            // Concatenate the original date and time as in TaskAdapter.
            String dateTime = (taskEntity.getDate() != null ? taskEntity.getDate().toString() : "No date") +
                    " " +
                    (taskEntity.getTime() != null ? taskEntity.getTime().toString() : "No time");

            taskDate.setText(dateTime);

            // Set the restored list name.
            listName.setText(taskEntity.getListName());

            // Set the completion date.
            completionDate.setText(taskEntity.getCompletionDate().toString());

            // Set a click listener on the entire itemView.
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCompletedTaskClick(taskEntity);
                }
            });
        }
    }

    @NonNull
    @Override
    public CompletedTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_completed_task.xml layout.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_completed_task, parent, false);
        return new CompletedTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedTaskViewHolder holder, int position) {
        CompletedTaskEntity taskEntity = completedTaskEntities.get(position);
        holder.bind(taskEntity, onTaskClickListener);
        setItemAppearanceAnimation(holder);
        setClickBounceAnimation(holder);
    }

    @Override
    public int getItemCount() {
        return completedTaskEntities.size();
    }

    private void setItemAppearanceAnimation(@NonNull CompletedTaskViewHolder holder) {
        // Animate the item's appearance (similar to TaskAdapter)
        holder.itemView.setAlpha(0f);
        holder.itemView.setTranslationY(50f);
        holder.itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void setClickBounceAnimation(@NonNull CompletedTaskViewHolder holder) {
        // Add touch listener for the "small-and-big" (bounce) animation
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
                    // On release, do a smooth pop (slightly overscaled) then settle back.
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
                    // If cancelled, return smoothly to normal scale.
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
}
