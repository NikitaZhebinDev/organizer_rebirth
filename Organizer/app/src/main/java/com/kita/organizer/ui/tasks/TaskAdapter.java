package com.kita.organizer.ui.tasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kita.organizer.R;
import com.kita.organizer.data.entity.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks = new ArrayList<>();

    public void setTasks(List<Task> taskList) {
        this.tasks = taskList;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        Button taskButton;
        CheckBox taskCheckBox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskButton = itemView.findViewById(R.id.taskButton);
            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
        }

        public void bind(Task task) {
            taskButton.setText(task.getText());
            // TODO: handle checkbox or button click here if needed
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(tasks.get(position));

        // Animate item appearance
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
        return tasks.size();
    }
}

