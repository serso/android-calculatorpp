package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import javax.annotation.Nonnull;

public class RemovalConfirmationDialog {

    @Nonnull
    private final Activity activity;
    @Nonnull
    private final String name;
    @Nonnull
    private final DialogInterface.OnClickListener listener;
    @StringRes
    private final int message;

    private RemovalConfirmationDialog(@Nonnull Activity activity, @Nonnull String name, @Nonnull DialogInterface.OnClickListener listener, int message) {
        this.activity = activity;
        this.name = name;
        this.listener = listener;
        this.message = message;
    }

    public static void showForFunction(@Nonnull Activity activity, @Nonnull String name, @Nonnull DialogInterface.OnClickListener listener) {
        new RemovalConfirmationDialog(activity, name, listener, R.string.function_removal_confirmation_question).show();
    }

    public static void showForVariable(@Nonnull Activity activity, @Nonnull String name, @Nonnull DialogInterface.OnClickListener listener) {
        new RemovalConfirmationDialog(activity, name, listener, R.string.c_var_removal_confirmation_question).show();
    }

    private void show() {
        new AlertDialog.Builder(activity, App.getTheme().alertDialogTheme)
                .setCancelable(true)
                .setTitle(R.string.removal_confirmation)
                .setMessage(activity.getString(message, name))
                .setNegativeButton(R.string.cpp_no, null)
                .setPositiveButton(R.string.cpp_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        dialog.dismiss();
                    }
                })
                .create().show();
    }
}
