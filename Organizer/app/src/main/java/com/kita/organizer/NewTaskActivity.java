package com.kita.organizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MenuItem;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kita.organizer.service.GoogleSpeechService;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Locale;

public class NewTaskActivity extends AppCompatActivity {
    private GoogleSpeechService googleSpeechService;
    private final ActivityResultLauncher<Intent> speechLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                googleSpeechService.handleSpeechResult(result.getResultCode(), result.getData());
            });
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
        initButtons();
        initTextViews();

        googleSpeechService = new GoogleSpeechService(this, speechLauncher);
        googleSpeechService.setSpeechListener(result -> editTask.setText(result));
    }

    private void initButtons() {
        imageBtnDate = findViewById(R.id.imageBtnDate);
        imageBtnTime = findViewById(R.id.imageBtnTime);
        imgBtnClearDate = findViewById(R.id.imgBtnClearDate);
        imgBtnClearTime = findViewById(R.id.imgBtnClearTime);
        imgBtnNewList = findViewById(R.id.imgBtnNewList);
        btnRepeat = findViewById(R.id.btnRepeat);
        btnAddToList = findViewById(R.id.btnAddToList);
        imageBtnSpeak = findViewById(R.id.imageBtnSpeak);

        /* TODO
        imageBtnDate.setOnClickListener(this);
        imageBtnTime.setOnClickListener(this);
        imgBtnClearDate.setOnClickListener(this);
        imgBtnClearTime.setOnClickListener(this);
        imgBtnNewList.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);
        btnAddToList.setOnClickListener(this);*/
        imageBtnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSpeechService.startVoiceRecognition();
            }
        });

        // TODO btnRepeat.setText(DataBaseLists.listRepeatNames[0]);
        // TODO btnAddToList.setText(DataBaseLists.defaultListsNames[0]);
    }

    private void initTextViews() {
        editTask = findViewById(R.id.editTask);
        textViewSetTime = findViewById(R.id.textViewSetTime);
        textViewRepeat = findViewById(R.id.textViewRepeat);
        editDate = findViewById(R.id.editDate);
        editTime = findViewById(R.id.editTime);

        /* TODO
        editTask.setOnClickListener(this);
        editDate.setOnClickListener(this);
        editTime.setOnClickListener(this);*/

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



}