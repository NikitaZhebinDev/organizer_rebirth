package com.kita.organizer.ui.tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kita.organizer.R;
import com.kita.organizer.data.entity.TaskEntity;
import com.kita.organizer.utils.AnimationUtils;

/**
 * TaskAdapter now extends ListAdapter and leverages DiffUtil for automatic minimal updates.
 *
 * Usage in your Fragment/Activity:
 *
 * taskAdapter.submitList(tasks);
 *
 * instead of calling a custom setTasks() method.
 */
public class TaskAdapter extends ListAdapter<TaskEntity, TaskAdapter.TaskViewHolder> {

    private TasksViewModel tasksViewModel;
    private OnTaskClickListener onTaskClickListener;

    /**
     * Interface for task click events.
     */
    public interface OnTaskClickListener {
        void onTaskClick(TaskEntity task);
    }

    /**
     * Constructor relying on a TasksViewModel.
     */
    public TaskAdapter(TasksViewModel tasksViewModel) {
        super(DIFF_CALLBACK);
        this.tasksViewModel = tasksViewModel;
    }

    /**
     * Setter for the task click listener.
     */
    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.onTaskClickListener = listener;
    }

    /**
     * ViewHolder for task item views.
     */
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

        /**
         * Binds the TaskEntity to the ViewHolder and sets up click listeners.
         */
        public void bind(TaskEntity taskEntity, OnTaskClickListener listener, TasksViewModel viewModel) {
            taskText.setText(taskEntity.getText());

            // Set the row click listener.
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(taskEntity);
                }
            });

            // Set click listener on the CheckBox to complete the task.
            taskCheckBox.setOnClickListener(v -> {
                showCompleteTaskDialog(taskEntity, viewModel);
            });
        }

        /**
         * Shows a confirmation dialog to mark the task complete.
         */
        private void showCompleteTaskDialog(TaskEntity taskEntity, TasksViewModel viewModel) {
            Context context = itemView.getContext();
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Mark as Complete?")
                    .setNegativeButton("No", (d, which) -> {
                        d.dismiss();
                        taskCheckBox.setChecked(false);
                    })
                    .setPositiveButton("Yes", (d, which) -> {
                        viewModel.completeTask(context, taskEntity);
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

        // Retrieve the task using getItem() from ListAdapter.
        TaskEntity taskEntity = getItem(position);
        holder.bind(taskEntity, onTaskClickListener, tasksViewModel);

        // Apply the custom animations.
        /*AnimationUtils.animateItemAppearance(holder.itemView);*/
        AnimationUtils.setBounceTouchAnimation(holder.itemView);
    }

    /**
     * DiffUtil.ItemCallback implementation for TaskEntity.
     */
    private static final DiffUtil.ItemCallback<TaskEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<TaskEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull TaskEntity oldItem, @NonNull TaskEntity newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull TaskEntity oldItem, @NonNull TaskEntity newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
