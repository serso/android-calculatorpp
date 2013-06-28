package org.solovyev.android.calculator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javax.annotation.Nonnull;

public final class CalculatorReceiver extends BroadcastReceiver {

	public static final String ACTION_BUTTON_ID_EXTRA = "buttonId";
	public static final String ACTION_BUTTON_PRESSED = "org.solovyev.android.calculator.BUTTON_PRESSED";

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();

		if (CalculatorReceiver.ACTION_BUTTON_PRESSED.equals(action)) {
			final int buttonId = intent.getIntExtra(CalculatorReceiver.ACTION_BUTTON_ID_EXTRA, 0);

			final CalculatorButton button = CalculatorButton.getById(buttonId);
			if (button != null) {
				button.onClick(context);
			}
		}
	}

	@Nonnull
	public static Intent newButtonClickedIntent(@Nonnull Context context, @Nonnull CalculatorButton button) {
		final Intent onButtonClickIntent = new Intent(context, CalculatorReceiver.class);
		onButtonClickIntent.setAction(ACTION_BUTTON_PRESSED);
		onButtonClickIntent.putExtra(ACTION_BUTTON_ID_EXTRA, button.getButtonId());
		return onButtonClickIntent;
	}
}
