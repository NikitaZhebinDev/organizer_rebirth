package com.kita.organizer.utils;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class DialogUtils {

    public static EditText createStyledEditText(Context context, String hint, @Nullable String prefillText) {
        // Create a LinearLayout.LayoutParams with margins
        int horizontalMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        int topMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics());

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(horizontalMargin, topMargin, horizontalMargin, 0);

        // Create and configure EditText
        EditText input = new EditText(context);
        input.setLayoutParams(lp);
        input.setHint(hint);
        if (prefillText != null) {
            input.setText(prefillText);
            input.setSelection(prefillText.length());
        }

        return input;
    }

    public static View wrapInVerticalContainer(View child) {
        Context context = child.getContext();
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.addView(child);
        return container;
    }

}
