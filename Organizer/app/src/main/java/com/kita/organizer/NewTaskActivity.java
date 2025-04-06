package com.kita.organizer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kita.organizer.data.dao.ListDao;
import com.kita.organizer.data.dao.TaskDao;
import com.kita.organizer.data.db.OrganizerDatabase;
import com.kita.organizer.data.entity.ListEntity;
import com.kita.organizer.data.entity.RepeatOption;
import com.kita.organizer.data.entity.Task;
import com.kita.organizer.service.GoogleSpeechService;
import com.kita.organizer.util.DatabaseLogger;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class NewTaskActivity extends AppCompatActivity {
    private static final String TAG = NewTaskActivity.class.getSimpleName();
    private GoogleSpeechService googleSpeechService;
    private final ActivityResultLauncher<Intent> speechLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                googleSpeechService.handleSpeechResult(result.getResultCode(), result.getData());
            });
    private String[] repeatOptions;
    private TextView editDate, editTime, textViewSetTime, textViewRepeat;
    private EditText editTask;
    private ImageButton imageBtnDate, imageBtnTime, imageBtnSpeak, imgBtnClearDate, imgBtnClearTime, imgBtnNewList;
    private Button btnRepeat, btnAddToList;

    private LocalDate selectedDate;
    private LocalTime selectedTime;
    private Integer selectedRepeatOption = RepeatOption.NO_REPEAT.getValue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prevents UI elements from being obscured by the system bars.
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_task);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve repeat options from strings.xml
        repeatOptions = getResources().getStringArray(R.array.repeat_options);

        setupToolbar();
        setupUIElements();

        googleSpeechService = new GoogleSpeechService(this, speechLauncher);
        googleSpeechService.setSpeechListener(result -> editTask.setText(result));

        // Log the lists present in the database when the app starts
        DatabaseLogger.logListsInDatabase(TAG, getApplicationContext()); // todo remove
        DatabaseLogger.logTasksInDatabase(TAG, getApplicationContext()); // todo remove
    }

    private void setupUIElements() {
        // find UI elements
        imageBtnDate = findViewById(R.id.imageBtnDate);
        imageBtnTime = findViewById(R.id.imageBtnTime);
        imgBtnClearDate = findViewById(R.id.imgBtnClearDate);
        imgBtnClearTime = findViewById(R.id.imgBtnClearTime);
        imgBtnNewList = findViewById(R.id.imgBtnNewList);

        btnRepeat = findViewById(R.id.btnRepeat);
        btnRepeat.setText(repeatOptions[0]); // Default Repeat Option is NO_REPEAT

        btnAddToList = findViewById(R.id.btnAddToList);
        btnAddToList.setText("Default"); // Preselect “Default” at Launch todo add internationalization?

        imageBtnSpeak = findViewById(R.id.imageBtnSpeak);
        editTask = findViewById(R.id.editTask);
        textViewSetTime = findViewById(R.id.textViewSetTime);
        textViewRepeat = findViewById(R.id.textViewRepeat);
        editDate = findViewById(R.id.editDate);
        editTime = findViewById(R.id.editTime);

        // Set listeners
        imageBtnSpeak.setOnClickListener(v -> googleSpeechService.startVoiceRecognition());

        imageBtnDate.setOnClickListener(v -> showDatePickerDialog());
        editDate.setOnClickListener(v -> showDatePickerDialog());
        imgBtnClearDate.setOnClickListener(v -> {
            editDate.setText("");
            editTime.setText("");
            btnRepeat.setText(repeatOptions[0]); // Default Repeat Option is NO_REPEAT
            hideTimeSelectorSection();
            hideRepeatOptionSelectorSection();
            hideDateBtnClear();
        });

        imageBtnTime.setOnClickListener(v -> showTimePickerDialog());
        editTime.setOnClickListener(v -> showTimePickerDialog());
        imgBtnClearTime.setOnClickListener(v -> {
            editTime.setText("");
            btnRepeat.setText(repeatOptions[0]); // Default Repeat Option is NO_REPEAT
            hideRepeatOptionSelectorSection();
            hideTimeBtnClear();
        });

        btnRepeat.setOnClickListener(v -> showRepeatOptionDialog());
        btnAddToList.setOnClickListener(v -> showListSelectionDialog());

        imgBtnNewList.setOnClickListener(v -> showAddListDialog());

        // Request focus -> it'll show keyboard on activity startup
        editTask.requestFocus();

        hideTimeSelectorSection();
        hideRepeatOptionSelectorSection();
        hideDateBtnClear();
        hideTimeBtnClear();
    }

    /**
     * Sets up the custom toolbar.
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarNewTask);
        // Set Back button
        toolbar.setNavigationIcon(R.drawable.ic_action_back_arrow);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        // When Apply button is clicked, save the task
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.create_new_task) {
                String taskText = editTask.getText().toString();
                String listName = btnAddToList.getText().toString();
                RepeatOption repeatOption = RepeatOption.fromValue(selectedRepeatOption);

                saveTaskToDatabase(taskText, selectedDate, selectedTime, repeatOption, listName);
            }
            return false;
        });
    }

    private void showDatePickerDialog() {
        // Get the current date using LocalDate
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue() - 1; // DatePicker expects month to be 0-based
        int day = today.getDayOfMonth();

        // Create and show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay); // Adding 1 to month for LocalDate
                    updateEditDate(selectedDate);
                    showDateBtnClear();
                    showTimeSelectorSection();
                },
                year, month, day
        );
        datePickerDialog.show();
    }


    private void showTimePickerDialog() {
        // Get the current time
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();

        // Create and show the TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    selectedTime = LocalTime.of(selectedHour, selectedMinute);
                    updateEditTime(selectedTime);
                    showTimeBtnClear();
                    showRepeatOptionSelectorSection();
                },
                hour, minute, true // true for 24-hour format, false for 12-hour format TODO add settings time format picker
        );

        timePickerDialog.show();
    }

    private void updateEditDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM dd, yyyy", Locale.getDefault());
        editDate.setText(date.format(formatter));
    }

    private void updateEditTime(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
        editTime.setText(time.format(formatter));
    }

    private void showRepeatOptionDialog() {
        // Get the current text of the button
        String currentButtonText = btnRepeat.getText().toString();

        // Find the index of the repeat option that matches the button text
        int preselectedIndex = -1;
        for (int i = 0; i < repeatOptions.length; i++) {
            if (repeatOptions[i].equals(currentButtonText)) {
                preselectedIndex = i;
                break;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(repeatOptions, preselectedIndex, (dialog, which) -> {
            // Update button text with selected option
            btnRepeat.setText(repeatOptions[which]);
            selectedRepeatOption = which;  // Update the selected option (index)
            dialog.dismiss();
        }).create().show();
    }

    // todo add internationalization
    private void saveTaskToDatabase(String taskText, LocalDate date, LocalTime time, RepeatOption repeatOption, String listName) {
        taskText = taskText.trim();
        if (taskText.isEmpty()) {
            Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show();
            return;
        }

        String finalTaskText = taskText;
        new Thread(() -> {
            // Create an instance of the ListDao to get the ListEntity by name
            ListDao listDao = OrganizerDatabase.getInstance(getApplicationContext()).listDao();
            ListEntity listEntity = listDao.getByName(listName);
            if (listEntity == null) {
                // If the ListEntity doesn't exist, fallback to the "Default" list
                listEntity = listDao.getByName("Default");
            }

            Task task = new Task(finalTaskText, date, time, repeatOption, listEntity.getId());
            TaskDao taskDao = OrganizerDatabase.getInstance(getApplicationContext()).taskDao();
            taskDao.insert(task);

            runOnUiThread(() -> Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show());
            Log.d(TAG, "Task saved: " + task);
            runOnUiThread(this::finish); // close NewTaskActivity
        }).start();
    }

    private void showListSelectionDialog() {
        new Thread(() -> {
            OrganizerDatabase db = OrganizerDatabase.getInstance(getApplicationContext());
            ListDao listDao = db.listDao();
            List<ListEntity> lists = listDao.getAll();

            // Convert to array of names
            String[] listNames = new String[lists.size()];
            int defaultIndex = -1;
            String currentListName = btnAddToList.getText().toString();  // Get the current text from the button

            for (int i = 0; i < lists.size(); i++) {
                listNames[i] = lists.get(i).getName();
                if (currentListName.equalsIgnoreCase(listNames[i])) {
                    defaultIndex = i;  // Set the selected index based on the button text
                }
            }

            // If no match was found, fall back to "Default" list as default
            if (defaultIndex == -1) {
                for (int i = 0; i < lists.size(); i++) {
                    if ("Default".equalsIgnoreCase(listNames[i])) {
                        defaultIndex = i;
                        break;
                    }
                }
            }

            int finalDefaultIndex = defaultIndex;

            runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewTaskActivity.this);
                builder.setSingleChoiceItems(listNames, finalDefaultIndex, (dialog, which) -> {
                    btnAddToList.setText(listNames[which]); // Set button text
                    dialog.dismiss();
                }).show();
            });
        }).start();
    }

    // TODO add internationalization
    private void showAddListDialog() {
        // Create a LinearLayout container with vertical orientation
        LinearLayout container = new LinearLayout(NewTaskActivity.this);
        container.setOrientation(LinearLayout.VERTICAL);

        // Convert dp values to pixels: horizontal margin and top margin
        int horizontalMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        int topMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());

        // Create layout parameters for EditText with margins
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(horizontalMargin, topMargin, horizontalMargin, 0);

        // Create the EditText and apply layout parameters
        final EditText input = new EditText(NewTaskActivity.this);
        input.setHint("Enter list name");
        input.setLayoutParams(lp);
        container.addView(input);

        // Build the dialog with the custom container
        AlertDialog.Builder builder = new AlertDialog.Builder(NewTaskActivity.this);
        builder.setTitle("New List")
                .setView(container)
                .setPositiveButton("Add", null)  // Override later
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        final AlertDialog dialog = builder.create();

        // Override the positive button’s behavior
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                String listName = input.getText().toString().trim();
                if (listName.isEmpty()) {
                    Toast.makeText(NewTaskActivity.this, "List name cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    // Extracted the duplicate check and insertion logic into another method
                    validateAndAddList(listName, dialog);
                }
            });
        });

        dialog.show();
    }

    /**
     * Checks if a list with the given name already exists. If it does, shows a warning;
     * otherwise, adds the new list.
     */
    private void validateAndAddList(String listName, AlertDialog dialog) {
        new Thread(() -> {
            OrganizerDatabase db = OrganizerDatabase.getInstance(getApplicationContext());
            ListDao listDao = db.listDao();

            // Check for an existing list with the same name
            ListEntity existingList = listDao.getByName(listName);
            runOnUiThread(() -> {
                if (existingList != null) {
                    Toast.makeText(NewTaskActivity.this, "List already exists", Toast.LENGTH_SHORT).show();
                } else {
                    addNewListToDatabase(listName);
                    dialog.dismiss();
                }
            });
        }).start();
    }

    /**
     * Inserts a new list into the database. Todo add internationalization
     */
    private void addNewListToDatabase(String newListName) {
        new Thread(() -> {
            OrganizerDatabase db = OrganizerDatabase.getInstance(getApplicationContext());
            ListDao listDao = db.listDao();
            listDao.insert(new ListEntity(newListName));
            runOnUiThread(() -> {
                Toast.makeText(NewTaskActivity.this, "List '" + newListName + "' added", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private void hideKeyboard(@NonNull View v) {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showKeyboard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideTimeSelectorSection() {
        textViewSetTime.setVisibility(View.GONE);
        editTime.setVisibility(View.GONE);
        imageBtnTime.setVisibility(View.GONE);
        imgBtnClearTime.setVisibility(View.GONE);
    }

    private void showTimeSelectorSection() {
        textViewSetTime.setVisibility(View.VISIBLE);
        editTime.setVisibility(View.VISIBLE);
        imageBtnTime.setVisibility(View.VISIBLE);
        if (editTime.getText().toString().isEmpty()) {
            imgBtnClearTime.setVisibility(View.GONE);
        } else {
            imgBtnClearTime.setVisibility(View.VISIBLE);
        }
    }

    private void hideRepeatOptionSelectorSection() {
        textViewRepeat.setVisibility(View.GONE);
        btnRepeat.setVisibility(View.GONE);
    }

    private void showRepeatOptionSelectorSection() {
        textViewRepeat.setVisibility(View.VISIBLE);
        btnRepeat.setVisibility(View.VISIBLE);
    }

    private void hideDateBtnClear() {
        imgBtnClearDate.setVisibility(View.GONE);
    }

    private void showDateBtnClear() {
        imgBtnClearDate.setVisibility(View.VISIBLE);
    }

    private void hideTimeBtnClear() {
        imgBtnClearTime.setVisibility(View.GONE);
    }

    private void showTimeBtnClear() {
        imgBtnClearTime.setVisibility(View.VISIBLE);
    }

}