package org.solovyev.android.calculator.function;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;

public class FunctionRemovalDialog {

    @Nonnull
    private final Activity activity;
    @Nonnull
    private final String functionName;
    @Nonnull
    private final DialogInterface.OnClickListener listener;

    private FunctionRemovalDialog(@Nonnull Activity activity, @Nonnull String functionName, @Nonnull DialogInterface.OnClickListener listener) {
        this.activity = activity;
        this.functionName = functionName;
        this.listener = listener;
    }

    public static void show(@Nonnull Activity activity, @Nonnull String functionName, @Nonnull DialogInterface.OnClickListener listener) {
        new FunctionRemovalDialog(activity, functionName, listener).show();
    }

    public void show() {
        new AlertDialog.Builder(activity, App.getTheme().alertDialogTheme)
                .setCancelable(true)
                .setTitle(R.string.removal_confirmation)
                .setMessage(activity.getString(R.string.function_removal_confirmation_question, functionName))
                .setNegativeButton(R.string.c_no, null)
                .setPositiveButton(R.string.c_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        dialog.dismiss();
                    }
                })
                .create().show();
    }
}
