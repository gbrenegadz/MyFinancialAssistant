package com.gbrenegadzdev.financeassistant.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.gbrenegadzdev.financeassistant.R;

public class DialogUtils {

    public AlertDialog showCustomDialog(Context context, String title,
                                        String positiveText, String negativeText,
                                        View view,
                                        final DialogInterface.OnClickListener positiveClick, final DialogInterface.OnClickListener negativeClick) {

        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view)
                .setPositiveButton(positiveText, positiveClick)
                .setNegativeButton(negativeText, negativeClick)
                .setCancelable(false)
                .create();
    }

    public AlertDialog showYesNoDialog(Context context, String title, DialogInterface.OnClickListener positiveClick) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setPositiveButton(context.getString(R.string.yes), positiveClick)
                .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
    }
}
