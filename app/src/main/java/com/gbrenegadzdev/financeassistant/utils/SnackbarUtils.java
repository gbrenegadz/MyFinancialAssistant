package com.gbrenegadzdev.financeassistant.utils;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class SnackbarUtils {

    public Snackbar createIndefinite(View view, String text, String actionText, View.OnClickListener clickListener) {
        return Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE)
                .setAction(actionText, clickListener);
    }

    public Snackbar create(View view, String text) {
        return Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setAction("OK", null);
    }
}
