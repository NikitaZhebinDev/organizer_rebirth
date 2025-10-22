package com.kita.organizer.utils;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/***
 * Deprecated since it causes incorrect margin between elements (tasks/lists) but
 * TODO that will be fixed in the future (bounce on touch works fine)
 */
@Deprecated
public class AnimationUtils {

    /**
     * Animate the appearance of a view by fading its alpha and slide-up from translationY.
     *
     * @param view The view to animate.
     */
    public static void animateItemAppearance(View view) {
        view.setAlpha(0f);
        view.setTranslationY(50f);
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    /**
     * Applies a bounce animation to the view when touched.
     *
     * @param view The view to set the touch listener on.
     */
    public static void setBounceTouchAnimation(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate()
                            .scaleX(0.98f)
                            .scaleY(0.98f)
                            .setDuration(120)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                    break;
                case MotionEvent.ACTION_UP:
                    v.animate()
                            .scaleX(1.02f)
                            .scaleY(1.02f)
                            .setDuration(75)
                            .withEndAction(() -> v.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(150)
                                    .setInterpolator(new OvershootInterpolator(2f))
                                    .start())
                            .start();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(150)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                    break;
            }
            return false;
        });
    }

}
