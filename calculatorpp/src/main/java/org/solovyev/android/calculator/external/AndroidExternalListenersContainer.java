package org.solovyev.android.calculator.external;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.Calculator;
import org.solovyev.android.calculator.CalculatorApplication;
import org.solovyev.android.calculator.CalculatorDisplayChangeEventData;
import org.solovyev.android.calculator.CalculatorDisplayViewState;
import org.solovyev.android.calculator.CalculatorEditorChangeEventData;
import org.solovyev.android.calculator.CalculatorEditorViewState;
import org.solovyev.android.calculator.CalculatorEventData;
import org.solovyev.android.calculator.CalculatorEventHolder;
import org.solovyev.android.calculator.CalculatorEventListener;
import org.solovyev.android.calculator.CalculatorEventType;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.CalculatorUtils;
import org.solovyev.android.calculator.ParcelableCalculatorDisplayViewState;
import org.solovyev.android.calculator.ParcelableCalculatorEditorViewState;

import java.util.HashSet;
import java.util.Set;

/**
 * User: serso
 * Date: 10/19/12
 * Time: 11:11 PM
 */
public class AndroidExternalListenersContainer implements CalculatorExternalListenersContainer, CalculatorEventListener {

	/*
		**********************************************************************
		*
		*                           CONSTANTS
		*
		**********************************************************************
		*/
	public static final String EVENT_ID_EXTRA = "eventId";
	public static final String EDITOR_STATE_CHANGED_ACTION = "org.solovyev.calculator.widget.EDITOR_STATE_CHANGED";
	public static final String EDITOR_STATE_EXTRA = "editorState";
	public static final String DISPLAY_STATE_CHANGED_ACTION = "org.solovyev.calculator.widget.DISPLAY_STATE_CHANGED";
	public static final String DISPLAY_STATE_EXTRA = "displayState";

	private static final String TAG = "Calculator++ External Listener Helper";

	private final Set<Class<?>> externalListeners = new HashSet<Class<?>>();

	@NotNull
    private final CalculatorEventHolder lastEvent = new CalculatorEventHolder(CalculatorUtils.createFirstEventDataId());

    public AndroidExternalListenersContainer(@NotNull Calculator calculator) {
		calculator.addCalculatorEventListener(this);
	}

	public void onEditorStateChanged(@NotNull Context context,
											@NotNull CalculatorEventData calculatorEventData,
											@NotNull CalculatorEditorViewState editorViewState) {

		for (Class<?> externalListener : externalListeners) {
			final Intent intent = new Intent(EDITOR_STATE_CHANGED_ACTION);
			intent.setClass(context, externalListener);
			intent.putExtra(EVENT_ID_EXTRA, calculatorEventData.getEventId());
			intent.putExtra(EDITOR_STATE_EXTRA, (Parcelable) new ParcelableCalculatorEditorViewState(editorViewState));
			context.sendBroadcast(intent);
			Locator.getInstance().getNotifier().showDebugMessage(TAG, "Editor state changed broadcast sent");
		}
	}

	private void onDisplayStateChanged(@NotNull Context context,
											 @NotNull CalculatorEventData calculatorEventData,
											 @NotNull CalculatorDisplayViewState displayViewState) {
		for (Class<?> externalListener : externalListeners) {
			final Intent intent = new Intent(DISPLAY_STATE_CHANGED_ACTION);
			intent.setClass(context, externalListener);
			intent.putExtra(EVENT_ID_EXTRA, calculatorEventData.getEventId());
			intent.putExtra(DISPLAY_STATE_EXTRA, (Parcelable) new ParcelableCalculatorDisplayViewState(displayViewState));
			context.sendBroadcast(intent);
			Locator.getInstance().getNotifier().showDebugMessage(TAG, "Display state changed broadcast sent");
		}
	}

	@Override
	public void addExternalListener(@NotNull Class<?> externalCalculatorClass) {
		externalListeners.add(externalCalculatorClass);
	}

	@Override
	public boolean removeExternalListener(@NotNull Class<?> externalCalculatorClass) {
		return externalListeners.remove(externalCalculatorClass);
	}

	@Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
        final CalculatorEventHolder.Result result = lastEvent.apply(calculatorEventData);
        if (result.isNewAfter()) {
            switch (calculatorEventType) {
                case editor_state_changed_light:
                case editor_state_changed:
                    final CalculatorEditorChangeEventData editorChangeData = (CalculatorEditorChangeEventData) data;
                    final CalculatorEditorViewState newEditorState = editorChangeData.getNewValue();

                    Locator.getInstance().getNotifier().showDebugMessage(TAG, "Editor state changed: " + newEditorState.getText());

                    onEditorStateChanged(CalculatorApplication.getInstance(), calculatorEventData, newEditorState);
                    break;

                case display_state_changed:
                    final CalculatorDisplayChangeEventData displayChangeData = (CalculatorDisplayChangeEventData) data;
                    final CalculatorDisplayViewState newDisplayState = displayChangeData.getNewValue();

                    Locator.getInstance().getNotifier().showDebugMessage(TAG, "Display state changed: " + newDisplayState.getText());

                    onDisplayStateChanged(CalculatorApplication.getInstance(), calculatorEventData, newDisplayState);
                    break;
            }
        }
    }
}
