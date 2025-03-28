package com.kita.organizer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import com.kita.organizer.service.GoogleSpeechService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewTaskActivity extends AppCompatActivity {
    private GoogleSpeechService googleSpeechService;
    private final ActivityResultLauncher<Intent> speechLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                googleSpeechService.handleSpeechResult(result.getResultCode(), result.getData());
            });
    private final Calendar calendar = Calendar.getInstance();
    private String[] repeatOptions;
    private TextView editDate, editTime, textViewSetTime, textViewRepeat;
    private EditText editTask;
    private ImageButton imageBtnDate, imageBtnTime, imageBtnSpeak, imgBtnClearDate, imgBtnClearTime, imgBtnNewList;
    private Button btnRepeat, btnAddToList;

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

        setupToolbar();
        initUIElements();

        googleSpeechService = new GoogleSpeechService(this, speechLauncher);
        googleSpeechService.setSpeechListener(result -> editTask.setText(result));

        // Retrieve repeat options from strings.xml
        repeatOptions = getResources().getStringArray(R.array.repeat_options);
    }

    private void initUIElements() {
        // find UI elements
        imageBtnDate = findViewById(R.id.imageBtnDate);
        imageBtnTime = findViewById(R.id.imageBtnTime);
        imgBtnClearDate = findViewById(R.id.imgBtnClearDate);
        imgBtnClearTime = findViewById(R.id.imgBtnClearTime);
        imgBtnNewList = findViewById(R.id.imgBtnNewList);
        btnRepeat = findViewById(R.id.btnRepeat);
        btnAddToList = findViewById(R.id.btnAddToList);
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
        imgBtnClearDate.setOnClickListener(v -> editDate.setText("")); // Clears the EditText field

        imageBtnTime.setOnClickListener(v -> showTimePickerDialog());
        editTime.setOnClickListener(v -> showTimePickerDialog());
        imgBtnClearTime.setOnClickListener(v -> editTime.setText("")); // Clears the EditText field

        btnRepeat.setOnClickListener(v -> showRepeatOptionDialog());

        /* TODO
        imgBtnNewList.setOnClickListener(this);
        btnAddToList.setOnClickListener(this);*/

        // TODO btnRepeat.setText(DataBaseLists.listRepeatNames[0]);
        // TODO btnAddToList.setText(DataBaseLists.defaultListsNames[0]);

        // Request focus and show keyboard on activity startup
        editTask.requestFocus();
        showKeyboard();
    }

    private void setupToolbar() {
        // remove default toolbar
        // removed by theme adjustment with no action bar for activity in manifest file
        /*getSupportActionBar().hide();*/

        // Set the custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbarNewTask);
        // Set Back button
        toolbar.setNavigationIcon(R.drawable.ic_action_back_arrow);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.create_new_task) {
                // TODO: save task logic
                Toast.makeText(NewTaskActivity.this, "It worked!", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
    }

    private void hideKeyboard(@NonNull View v) {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showKeyboard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void showDatePickerDialog() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    updateEditDate();
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void updateEditDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, MMM dd, yyyy", Locale.getDefault());
        editDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void showTimePickerDialog() {
        // Get the current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create and show the TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    // Format the time (e.g., 08:05 or 13:45)
                    String formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    editTime.setText(formattedTime);
                },
                hour, minute, true // true for 24-hour format, false for 12-hour format TODO add settings time format picker
        );

        timePickerDialog.show();
    }

    private void showRepeatOptionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(repeatOptions, -1, (dialog, which) -> {
            // Update button text with selected option
            btnRepeat.setText(repeatOptions[which]); // which = RepeatOption from enum
            dialog.dismiss();
        });

        // Show dialog
        builder.create().show();
    }

}