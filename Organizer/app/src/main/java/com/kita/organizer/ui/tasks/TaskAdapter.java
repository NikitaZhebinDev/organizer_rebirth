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
import com.kita.organizer.data.db.OrganizerDatabase;
import com.kita.organizer.data.entity.CompletedTaskEntity;
import com.kita.organizer.data.entity.ListEntity;
import com.kita.organizer.data.entity.TaskEntity;

import java.time.LocalDate;
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
        // Define views in the item_task.xml layout
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

            // Set a click listener on the entire itemView.
            // Note: The performClick() call in the touch listener will trigger this.
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(taskEntity);
                }
            });

            // Extracted checkbox logic is now handled in a separate method.
            taskCheckBox.setOnClickListener(v -> showCompleteTaskDialog(taskEntity));
        }

        /**
         * Handles the checkbox click -> complete task action.
         * Displays an AlertDialog asking the user to confirm task completion.
         * If confirmed, the task is moved from Tasks to CompletedTasks in the database.
         *
         * @param taskEntity the task to be completed
         */
        private void showCompleteTaskDialog(TaskEntity taskEntity) {
            Context context = itemView.getContext();
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Mark as Complete?")
                    .setNegativeButton("No", (d, which) -> {
                        d.dismiss();
                        taskCheckBox.setChecked(false);
                    })
                    .setPositiveButton("Yes", (d, which) -> {
                        new Thread(() -> {
                            OrganizerDatabase database = OrganizerDatabase.getInstance(context);
                            ListEntity listEntity = database.listDao().getById(taskEntity.getListId());
                            String listName = (listEntity != null) ? listEntity.getName() : "";

                            CompletedTaskEntity completedTask = new CompletedTaskEntity(
                                    taskEntity.getText(),
                                    taskEntity.getDate(),
                                    taskEntity.getTime(),
                                    taskEntity.getRepeatOption(),
                                    taskEntity.getListId(),
                                    listName,
                                    LocalDate.now()
                            );

                            database.completedTaskDao().insert(completedTask);
                            database.taskDao().delete(taskEntity);
                        }).start();
                    })
                    .create();

            // Set a listener to handle the case when the dialog is dismissed
            dialog.setOnDismissListener(d -> taskCheckBox.setChecked(false));

            dialog.show();
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the new item_task.xml layout.
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
        // Add touch listener for the "small-and-big" (bounce) animation.
        holder.itemView.setOnTouchListener((view, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    view.animate()
                            .scaleX(0.98f)
                            .scaleY(0.98f)
                            .setDuration(120)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                    break;
                case MotionEvent.ACTION_UP:
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
