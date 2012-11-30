package org.solovyev.android.view;

import org.solovyev.android.ActivityDestroyerController;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.DialogOnActivityDestroyedListener;
import org.solovyev.android.view.R;

/**
 * User: serso
 * Date: 4/29/12
 * Time: 1:25 PM
 */
public class ConfirmationDialogBuilder implements DialogBuilder<AlertDialog> {

    @NotNull
    private final Context context;

    private final int messageResId;

    private int titleResId = R.string.c_confirmation;

    private int positiveButtonTextResId = android.R.string.ok;

    private int negativeButtonTextResId = android.R.string.cancel;

    @Nullable
    private DialogInterface.OnClickListener positiveHandler;

    @Nullable
    private DialogInterface.OnClickListener negativeHandler;

    public ConfirmationDialogBuilder(@NotNull Context context, int messageResId) {
        this.context = context;
        this.messageResId = messageResId;
    }

    @NotNull
    public ConfirmationDialogBuilder setTitleResId(int titleResId) {
        this.titleResId = titleResId;
        return this;
    }

    @NotNull
    public ConfirmationDialogBuilder setPositiveButtonTextResId(int positiveButtonTextResId) {
        this.positiveButtonTextResId = positiveButtonTextResId;
        return this;
    }

    @NotNull
    public ConfirmationDialogBuilder setNegativeButtonTextResId(int negativeButtonTextResId) {
        this.negativeButtonTextResId = negativeButtonTextResId;
        return this;
    }

    @NotNull
    public ConfirmationDialogBuilder setPositiveHandler(@Nullable DialogInterface.OnClickListener positiveHandler) {
        this.positiveHandler = positiveHandler;
        return this;
    }

    @NotNull
    public ConfirmationDialogBuilder setNegativeHandler(@Nullable DialogInterface.OnClickListener negativeHandler) {
        this.negativeHandler = negativeHandler;
        return this;
    }

    @NotNull
    @Override
    public AlertDialog build() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(titleResId);
        builder.setMessage(messageResId);
        builder.setPositiveButton(positiveButtonTextResId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ( positiveHandler != null ) {
                    positiveHandler.onClick(dialog, which);
                }
            }
        });

        builder.setNegativeButton(negativeButtonTextResId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ( negativeHandler != null ) {
                    negativeHandler.onClick(dialog, which);
                }
            }
        });


        final  AlertDialog result = builder.create();
        if ( context instanceof Activity) {
            ActivityDestroyerController.getInstance().put((Activity)context, new DialogOnActivityDestroyedListener(result));
        }
        return result;
    }
}
