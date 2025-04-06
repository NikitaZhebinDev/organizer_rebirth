package com.kita.organizer.ui.tasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kita.organizer.R;
import com.kita.organizer.data.entity.TaskEntity;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<TaskEntity> taskEntities = new ArrayList<>();

    public void setTasks(List<TaskEntity> taskEntityList) {
        this.taskEntities = taskEntityList;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        // The clickable container (styled as a button)
        LinearLayout taskButton;
        // The checkbox for completing the task
        CheckBox taskCheckBox;
        // Text view for the task text
        TextView taskText;
        // Text view for the date (if applicable)
        TextView taskDate;
        // Image view for the repeat icon
        ImageView repeatImage;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskButton = itemView.findViewById(R.id.task_button);
            taskCheckBox = itemView.findViewById(R.id.task_checkbox);
            taskText = itemView.findViewById(R.id.task_text);
            taskDate = itemView.findViewById(R.id.task_date);
            repeatImage = itemView.findViewById(R.id.repeat_image);
        }

        public void bind(TaskEntity taskEntity) {
            // Set the task text
            taskText.setText(taskEntity.getText());

            // todo : Optionally, set the date if the task contains a valid date
            // (assuming task.getDate() returns a String; adjust as needed)
            /*if (task.getDate() != null) {
                taskDate.setText(task.getDate());
            } else {
                taskDate.setText("");
            }*/

            // Set the checkbox state based on task completion
            //taskCheckBox.setChecked(task.isCompleted());
            // TODO: Bind the repeat icon (if applicable) or set click listeners on taskButton/taskCheckBox
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
        holder.bind(taskEntity);

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

