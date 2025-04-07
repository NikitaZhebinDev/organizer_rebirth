package com.kita.organizer.ui.tasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kita.organizer.R;
import com.kita.organizer.data.entity.TaskEntity;

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

        // Keep references for UI elements for binding
        CheckBox taskCheckBox;
        TextView taskText;
        TextView taskDate;
        ImageView repeatImage;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            // Now using itemView directly as the clickable area
            taskCheckBox = itemView.findViewById(R.id.task_checkbox);
            taskText = itemView.findViewById(R.id.task_text);
            taskDate = itemView.findViewById(R.id.task_date);
            repeatImage = itemView.findViewById(R.id.repeat_image);
        }

        public void bind(TaskEntity taskEntity, OnTaskClickListener listener) {
            // Set the task text
            taskText.setText(taskEntity.getText());

            // Optionally set the date if applicable
            // if (taskEntity.getDate() != null) {
            //     taskDate.setText(taskEntity.getDate());
            // } else {
            //     taskDate.setText("");
            // }

            // Set the checkbox state if needed
            // taskCheckBox.setChecked(taskEntity.isCompleted());
            // You can also add listeners for checkbox or repeat icon if desired

            // Make the entire itemView clickable
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

        // Animate the item's appearance
        holder.itemView.setAlpha(0f);
        holder.itemView.setTranslationY(50f);
        holder.itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    @Override
    public int getItemCount() {
        return taskEntities.size();
    }
}
