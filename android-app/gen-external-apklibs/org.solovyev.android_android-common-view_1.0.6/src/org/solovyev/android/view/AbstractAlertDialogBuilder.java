package org.solovyev.android.view;

import android.app.AlertDialog;
import android.content.Context;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 8/2/12
 * Time: 4:25 PM
 */
public abstract class AbstractAlertDialogBuilder<D extends AlertDialog> implements DialogBuilder<D> {

    @NotNull
    private Context context;

    protected AbstractAlertDialogBuilder(@NotNull Context context) {
        this.context = context;
    }

    @NotNull
    protected Context getContext() {
        return context;
    }
}
