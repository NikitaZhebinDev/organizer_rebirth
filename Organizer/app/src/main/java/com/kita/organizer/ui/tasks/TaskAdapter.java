package com.kita.organizer.ui.tasks;

import android.app.AlertDialog;
import android.content.Context;
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
import com.kita.organizer.utils.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<TaskEntity> taskEntities = new ArrayList<>();
    private TasksViewModel tasksViewModel;

    // Optional: Interface for task click events
    public interface OnTaskClickListener {
        void onTaskClick(TaskEntity task);
    }

    private OnTaskClickListener onTaskClickListener;

    // Setter for the task click listener
    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.onTaskClickListener = listener;
    }

    // Setter for the ViewModel
    public void setTasksViewModel(TasksViewModel viewModel) {
        this.tasksViewModel = viewModel;
    }

    public void setTasks(List<TaskEntity> taskEntityList) {
        this.taskEntities = taskEntityList;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox taskCheckBox;
        TextView taskText;
        TextView taskDate;
        ImageView repeatImage;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskCheckBox = itemView.findViewById(R.id.task_checkbox);
            taskText = itemView.findViewById(R.id.task_text);
            taskDate = itemView.findViewById(R.id.task_date);
            repeatImage = itemView.findViewById(R.id.repeat_image);
        }

        public void bind(TaskEntity taskEntity, OnTaskClickListener listener, TasksViewModel viewModel) {
            taskText.setText(taskEntity.getText());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(taskEntity);
                }
            });

            taskCheckBox.setOnClickListener(v -> {
                showCompleteTaskDialog(taskEntity, viewModel);
            });
        }

        private void showCompleteTaskDialog(TaskEntity taskEntity, TasksViewModel viewModel) {
            Context context = itemView.getContext();
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Mark as Complete?")
                    .setNegativeButton("No", (d, which) -> {
                        d.dismiss();
                        taskCheckBox.setChecked(false);
                    })
                    .setPositiveButton("Yes", (d, which) -> {
                        viewModel.completeTask(context, taskEntity); // Delegate to ViewModel
                    })
                    .create();

            dialog.setOnDismissListener(d -> taskCheckBox.setChecked(false));
            dialog.show();
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
        TaskEntity taskEntity = taskEntities.get(position);
        holder.bind(taskEntity, onTaskClickListener, tasksViewModel);

        // Use the extracted animation utility methods
        AnimationUtils.animateItemAppearance(holder.itemView);
        AnimationUtils.setBounceTouchAnimation(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return taskEntities.size();
    }

}
