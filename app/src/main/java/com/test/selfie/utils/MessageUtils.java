package com.test.selfie.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

public class MessageUtils {

    public static void showError(@NonNull Activity activity, @NonNull String error) {
        showError(activity.getWindow().getDecorView(), error);
    }

    public static void showError(@NonNull View view, @NonNull String error) {
        Snackbar.make(view, error, Snackbar.LENGTH_LONG)
                .show();
    }
}
