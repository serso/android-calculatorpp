package org.solovyev.android.calculator.onscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.jetbrains.annotations.NotNull;

/**
* User: serso
* Date: 11/20/12
* Time: 11:05 PM
*/
public final class CalculatorOnscreenBroadcastReceiver extends BroadcastReceiver {

    public CalculatorOnscreenBroadcastReceiver() {
    }

    @Override
    public void onReceive(@NotNull Context context,
                          @NotNull Intent intent) {
        final Intent newIntent = new Intent(intent);
        newIntent.setClass(context, CalculatorOnscreenService.class);
        context.startService(newIntent);
    }
}
