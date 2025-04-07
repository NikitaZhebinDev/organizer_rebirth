package com.kita.organizer.ui.tasks;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kita.organizer.R;
import com.kita.organizer.data.entity.TaskEntity;
import com.kita.organizer.ui.lists.ListAdapter;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<TaskEntity> taskEntities = new ArrayList<>();

    // Optional: Interface for task click events
    public interface OnTaskClickListener {
        void onTaskClick(TaskEntity task);
    }

    private OnTaskClickListener onTaskClickListener;

    // Setter for the task click listener
    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.onTaskClickListener = listener;
    }

    public void setTasks(List<TaskEntity> taskEntityList) {
        this.taskEntities = taskEntityList;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        // References for UI elements for binding
        CheckBox taskCheckBox;
        TextView taskText;
        TextView taskDate;
        ImageView repeatImage;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            // Using itemView as the clickable area (i.e. the entire MaterialCardView)
            taskCheckBox = itemView.findViewById(R.id.task_checkbox);
            taskText = itemView.findViewById(R.id.task_text);
            taskDate = itemView.findViewById(R.id.task_date);
            repeatImage = itemView.findViewById(R.id.repeat_image);
        }

        public void bind(TaskEntity taskEntity, OnTaskClickListener listener) {
            // Set the task text
            taskText.setText(taskEntity.getText());

            // Optionally set the date or checkbox state if needed.
            // taskDate.setText(taskEntity.getDate());
            // taskCheckBox.setChecked(taskEntity.isCompleted());

            // Set a click listener on the entire itemView.
            // Note: The performClick() call in the touch listener will trigger this.
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(taskEntity);
                }
            });
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the new item_task.xml layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskEntity taskEntity = taskEntities.get(position);
        holder.bind(taskEntity, onTaskClickListener);

        setItemAppearanceAnimation(holder);
        setClickBounceAnimation(holder);
    }

    @Override
    public int getItemCount() {
        return taskEntities.size();
    }

    private void setItemAppearanceAnimation(@NonNull TaskViewHolder holder) {
        // Animate the item's appearance (optional)
        holder.itemView.setAlpha(0f);
        holder.itemView.setTranslationY(50f);
        holder.itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void setClickBounceAnimation(@NonNull TaskViewHolder holder) {
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

}
