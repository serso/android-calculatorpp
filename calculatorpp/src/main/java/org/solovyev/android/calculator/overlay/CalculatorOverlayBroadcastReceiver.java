package org.solovyev.android.calculator.overlay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.jetbrains.annotations.NotNull;

/**
* User: serso
* Date: 11/20/12
* Time: 11:05 PM
*/
public final class CalculatorOverlayBroadcastReceiver extends BroadcastReceiver {

    public CalculatorOverlayBroadcastReceiver() {
    }

    @Override
    public void onReceive(@NotNull Context context,
                          @NotNull Intent intent) {
        final Intent newIntent = new Intent(intent);
        newIntent.setClass(context, CalculatorOverlayService.class);
        context.startService(newIntent);
    }
}
