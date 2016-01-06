package org.solovyev.android.calculator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import javax.annotation.Nonnull;

public final class CalculatorReceiver extends BroadcastReceiver {

    public static final String ACTION_BUTTON_ID_EXTRA = "buttonId";
    public static final String ACTION_BUTTON_PRESSED = "org.solovyev.android.calculator.BUTTON_PRESSED";

    @Nonnull
    public static Intent newButtonClickedIntent(@Nonnull Context context, @Nonnull CalculatorButton button) {
        final Intent intent = new Intent(context, CalculatorReceiver.class);
        intent.setAction(ACTION_BUTTON_PRESSED);
        intent.putExtra(ACTION_BUTTON_ID_EXTRA, button.getButtonId());
        return intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (!TextUtils.equals(action, ACTION_BUTTON_PRESSED)) {
            return;
        }

        final int buttonId = intent.getIntExtra(ACTION_BUTTON_ID_EXTRA, 0);
        final CalculatorButton button = CalculatorButton.getById(buttonId);
        if (button == null) {
            return;
        }

        button.onClick();
    }
}
