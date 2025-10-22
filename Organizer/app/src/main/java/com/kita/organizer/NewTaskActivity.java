package com.kita.organizer;

import static com.kita.organizer.utils.DialogUtils.wrapInVerticalContainer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.kita.organizer.data.entity.TaskEntity;
import com.kita.organizer.service.GoogleSpeechService;
import com.kita.organizer.utils.DatabaseLogger;
import com.kita.organizer.utils.DialogUtils;
import com.kita.organizer.utils.KeyboardUtils;

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
    private Boolean isEditMode;

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

        // check for EXTRA_TASK. If present, you’re editing; otherwise, you’re creating
        TaskEntity editing = getIntent().getParcelableExtra("EXTRA_TASK");
        if (editing != null) {
            isEditMode = true;
            //taskId = editing.getId(); todo remove?
            editTask.setText(editing.getText());

            LocalDate d = editing.getDate();
            selectedDate = d;
            updateEditDate(d);
            showDateBtnClear();
            showTimeSelectorSection();

            LocalTime t = editing.getTime();
            selectedTime = t;
            updateEditTime(t);
            showTimeBtnClear();
            showRepeatOptionSelectorSection();

            selectedRepeatOption = editing.getRepeatOption().getValue();
            btnRepeat.setText(
                    getResources().getStringArray(R.array.repeat_options)[selectedRepeatOption]
            );

            // Load list name asynchronously (or pass listName in Intent)
            new Thread(() -> {
                String listName = OrganizerDatabase
                        .getInstance(getApplicationContext())
                        .listDao()
                        .getById(editing.getListId())
                        .getName();
                runOnUiThread(() -> btnAddToList.setText(listName));
            }).start();
        }

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
        if (date == null) {
            editDate.setText("");
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM dd, yyyy", Locale.getDefault());
        editDate.setText(date.format(formatter));
    }

    private void updateEditTime(LocalTime time) {
        if (time == null) {
            editTime.setText("");
            return;
        }
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

            TaskEntity taskEntity = new TaskEntity(finalTaskText, date, time, repeatOption, listEntity.getId());
            TaskDao taskDao = OrganizerDatabase.getInstance(getApplicationContext()).taskDao();
            taskDao.insert(taskEntity);

            runOnUiThread(() -> Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show());
            Log.d(TAG, "Task saved: " + taskEntity);
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
    /**
     * Displays an AlertDialog that allows the user to create a new list
     * by entering its name. Handles validation and calls logic to add
     * the list if input is valid.
     */
    private void showAddListDialog() {
        EditText listNameInput = DialogUtils.createStyledEditText(this, "Enter list name", null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New List")
                .setView(wrapInVerticalContainer(listNameInput))
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(v -> {
                String name = listNameInput.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(this, "List name cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    validateAndAddList(name, dialog);
                }
            });
        });

        dialog.show();
        KeyboardUtils.showKeyboard(this, listNameInput);
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
                // pre-select the new list name
                btnAddToList.setText(newListName);
            });
        }).start();
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