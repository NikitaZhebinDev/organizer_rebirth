package com.kita.organizer.service;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Voice Recognition via Google Speech API
 */
public class GoogleSpeechService {

    private final Context context;
    private final ActivityResultLauncher<Intent> speechLauncher;
    private SpeechListener listener;

    public interface SpeechListener {
        void onSpeechResult(String result);
    }

    public GoogleSpeechService(Context context, ActivityResultLauncher<Intent> speechLauncher) {
        this.context = context;
        this.speechLauncher = speechLauncher;
    }

    public void setSpeechListener(SpeechListener listener) {
        this.listener = listener;
    }

    public void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now..."); // TODO add string

        try {
            speechLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Speech recognition is not supported on your device", Toast.LENGTH_SHORT).show(); // TODO add string
        }
    }

    public void handleSpeechResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> speechResults = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (speechResults != null && !speechResults.isEmpty() && listener != null) {
                listener.onSpeechResult(speechResults.get(0));
            }
        }
    }

}
