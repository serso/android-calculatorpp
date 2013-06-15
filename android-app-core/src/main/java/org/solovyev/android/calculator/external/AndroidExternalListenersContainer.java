package org.solovyev.android.calculator.external;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import org.solovyev.android.App;
import org.solovyev.android.calculator.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

	public static final String INIT_ACTION = "org.solovyev.android.calculator.INIT";
	public static final String INIT_ACTION_CREATE_VIEW_EXTRA = "createView";

	public static final String EDITOR_STATE_CHANGED_ACTION = "org.solovyev.android.calculator.EDITOR_STATE_CHANGED";
	public static final String EDITOR_STATE_EXTRA = "editorState";

	public static final String DISPLAY_STATE_CHANGED_ACTION = "org.solovyev.android.calculator.DISPLAY_STATE_CHANGED";
	public static final String DISPLAY_STATE_EXTRA = "displayState";

	private static final String TAG = "Calculator++ External Listener Helper";

	@Nonnull
	private final Set<Class<?>> externalListeners = new HashSet<Class<?>>();

	@Nonnull
	private final CalculatorEventHolder lastEvent = new CalculatorEventHolder(CalculatorUtils.createFirstEventDataId());

	public AndroidExternalListenersContainer(@Nonnull Calculator calculator) {
		calculator.addCalculatorEventListener(this);
	}

	public void onEditorStateChanged(@Nonnull Context context,
									 @Nonnull CalculatorEventData calculatorEventData,
									 @Nonnull CalculatorEditorViewState editorViewState) {

		for (Class<?> externalListener : getExternalListenersSync()) {
			final Intent intent = new Intent(EDITOR_STATE_CHANGED_ACTION);
			intent.setClass(context, externalListener);
			intent.putExtra(EVENT_ID_EXTRA, calculatorEventData.getEventId());
			intent.putExtra(EDITOR_STATE_EXTRA, (Parcelable) new ParcelableCalculatorEditorViewState(editorViewState));
			context.sendBroadcast(intent);
			Locator.getInstance().getNotifier().showDebugMessage(TAG, "Editor state changed broadcast sent");
		}
	}

	private void onDisplayStateChanged(@Nonnull Context context,
									   @Nonnull CalculatorEventData calculatorEventData,
									   @Nonnull CalculatorDisplayViewState displayViewState) {
		for (Class<?> externalListener : getExternalListenersSync()) {
			final Intent intent = new Intent(DISPLAY_STATE_CHANGED_ACTION);
			intent.setClass(context, externalListener);
			intent.putExtra(EVENT_ID_EXTRA, calculatorEventData.getEventId());
			intent.putExtra(DISPLAY_STATE_EXTRA, (Parcelable) new ParcelableCalculatorDisplayViewState(displayViewState));
			context.sendBroadcast(intent);
			Locator.getInstance().getNotifier().showDebugMessage(TAG, "Display state changed broadcast sent");
		}
	}

	@Nonnull
	private Set<Class<?>> getExternalListenersSync() {
		synchronized (externalListeners) {
			return new HashSet<Class<?>>(externalListeners);
		}
	}

	@Override
	public void addExternalListener(@Nonnull Class<?> externalCalculatorClass) {
		synchronized (externalListeners) {
			externalListeners.add(externalCalculatorClass);
		}
	}

	@Override
	public boolean removeExternalListener(@Nonnull Class<?> externalCalculatorClass) {
		synchronized (externalListeners) {
			return externalListeners.remove(externalCalculatorClass);
		}
	}

	@Override
	public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
		final CalculatorEventHolder.Result result = lastEvent.apply(calculatorEventData);
		if (result.isNewAfter()) {
			switch (calculatorEventType) {
				case editor_state_changed_light:
				case editor_state_changed:
					final CalculatorEditorChangeEventData editorChangeData = (CalculatorEditorChangeEventData) data;
					final CalculatorEditorViewState newEditorState = editorChangeData.getNewValue();

					Locator.getInstance().getNotifier().showDebugMessage(TAG, "Editor state changed: " + newEditorState.getText());

					onEditorStateChanged(App.getApplication(), calculatorEventData, newEditorState);
					break;

				case display_state_changed:
					final CalculatorDisplayChangeEventData displayChangeData = (CalculatorDisplayChangeEventData) data;
					final CalculatorDisplayViewState newDisplayState = displayChangeData.getNewValue();

					Locator.getInstance().getNotifier().showDebugMessage(TAG, "Display state changed: " + newDisplayState.getText());

					onDisplayStateChanged(App.getApplication(), calculatorEventData, newDisplayState);
					break;
			}
		}
	}
}
