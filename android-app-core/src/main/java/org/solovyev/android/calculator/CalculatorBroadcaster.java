package org.solovyev.android.calculator;

import android.content.Context;
import android.content.Intent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CalculatorBroadcaster implements CalculatorEventListener {

	public static final String ACTION_INIT = "org.solovyev.android.calculator.INIT";
	public static final String ACTION_EDITOR_STATE_CHANGED = "org.solovyev.android.calculator.EDITOR_STATE_CHANGED";
	public static final String ACTION_DISPLAY_STATE_CHANGED = "org.solovyev.android.calculator.DISPLAY_STATE_CHANGED";

	@Nonnull
	private final Context context;

	public CalculatorBroadcaster(@Nonnull Context context) {
		this.context = context;
	}

	@Override
	public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
		switch (calculatorEventType) {
			case editor_state_changed:
			case editor_state_changed_light:
				sendEditorStateChangedIntent();
				break;
			case display_state_changed:
				sendDisplayStateChanged();
				break;
		}
	}

	public void sendDisplayStateChanged() {
		sendBroadcastIntent(ACTION_DISPLAY_STATE_CHANGED);
	}

	public void sendEditorStateChangedIntent() {
		sendBroadcastIntent(ACTION_EDITOR_STATE_CHANGED);
	}

	public void sendBroadcastIntent(@Nonnull String action) {
		context.sendBroadcast(new Intent(action));
	}
}
