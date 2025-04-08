package com.kita.organizer.ui.completedtasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kita.organizer.R;
import com.kita.organizer.data.entity.CompletedTaskEntity;
import com.kita.organizer.utils.AnimationUtils;

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
        // References for UI elements. Adjust IDs to match your item_completed_task.xml.
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
            taskText.setText(taskEntity.getText());

            // Concatenate date and time
            String dateTime = (taskEntity.getDate() != null ? taskEntity.getDate().toString() : "No date") +
                    " " +
                    (taskEntity.getTime() != null ? taskEntity.getTime().toString() : "No time");
            taskDate.setText(dateTime);

            // Set list name and completion date
            listName.setText(taskEntity.getListName());
            completionDate.setText(taskEntity.getCompletionDate().toString());

            // Set item click listener
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
        // Inflate the item_completed_task.xml layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_completed_task, parent, false);
        return new CompletedTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedTaskViewHolder holder, int position) {
        CompletedTaskEntity taskEntity = completedTaskEntities.get(position);
        holder.bind(taskEntity, onTaskClickListener);

        // Use the shared animation utilities
        AnimationUtils.animateItemAppearance(holder.itemView);
        AnimationUtils.setBounceTouchAnimation(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return completedTaskEntities.size();
    }
}
