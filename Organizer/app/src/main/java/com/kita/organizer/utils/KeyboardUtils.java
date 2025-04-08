package com.kita.organizer.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtils {

    private static final int KEYBOARD_DELAY_MS = 50; // Adjust the delay if needed

    /**
     * Shows the keyboard for a given view with a slight delay to ensure UI initialization.
     *
     * @param context The application context.
     * @param view The view that should receive input focus.
     */
    public static void showKeyboard(Context context, View view) {
        if (view == null) return;

        view.requestFocus(); // Ensure the view has focus

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }, KEYBOARD_DELAY_MS);
    }

    /**
     * Hides the keyboard for a given view.
     *
     * @param context The application context.
     * @param view Any view within the activity.
     */
    public static void hideKeyboard(Context context, View view) {
        if (view == null) return;

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
