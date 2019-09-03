package com.gbrenegadzdev.financeassistant.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;

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

    public AlertDialog.Builder showStringListDialogNoAction(final Context context, String title, String[] stringArray) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle(title);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.preference_category);
        for (String string : stringArray) {
            arrayAdapter.add(string);
        }

        builderSingle.setNegativeButton(context.getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();

        return builderSingle;
    }
}
