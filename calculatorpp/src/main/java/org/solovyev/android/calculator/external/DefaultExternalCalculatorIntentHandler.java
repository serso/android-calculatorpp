package org.solovyev.android.calculator.external;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.CalculatorDisplayViewState;
import org.solovyev.android.calculator.CalculatorEditorViewState;
import org.solovyev.common.MutableObject;

/**
 * User: serso
 * Date: 11/20/12
 * Time: 10:34 PM
 */
public class DefaultExternalCalculatorIntentHandler implements ExternalCalculatorIntentHandler {

    private static final String TAG = ExternalCalculatorIntentHandler.class.getSimpleName();

    @NotNull
    private final MutableObject<Long> lastDisplayEventId = new MutableObject<Long>(0L);

    @NotNull
    private final MutableObject<Long> lastEditorEventId = new MutableObject<Long>(0L);

    @NotNull
    private final ExternalCalculatorStateUpdater stateUpdater;

    public DefaultExternalCalculatorIntentHandler(@NotNull ExternalCalculatorStateUpdater stateUpdater) {
        this.stateUpdater = stateUpdater;
    }

    @Override
    public void onIntent(@NotNull Context context, @NotNull Intent intent) {

        if (ExternalCalculatorHelper.EDITOR_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Locator.getInstance().getNotifier().showDebugMessage(TAG, "Editor state changed broadcast received!");

            final Long eventId = intent.getLongExtra(ExternalCalculatorHelper.EVENT_ID_EXTRA, 0L);

            boolean updateEditor = false;
            synchronized (lastEditorEventId) {
                if (eventId > lastEditorEventId.getObject()) {
                    lastEditorEventId.setObject(eventId);
                    updateEditor = true;
                }
            }

            if (updateEditor) {
                final Parcelable object = intent.getParcelableExtra(ExternalCalculatorHelper.EDITOR_STATE_EXTRA);
                if (object instanceof CalculatorEditorViewState) {
                    onEditorStateChanged(context, (CalculatorEditorViewState) object);
                }
            }
        } else if (ExternalCalculatorHelper.DISPLAY_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Locator.getInstance().getNotifier().showDebugMessage(TAG, "Display state changed broadcast received!");

            final Long eventId = intent.getLongExtra(ExternalCalculatorHelper.EVENT_ID_EXTRA, 0L);
            boolean updateDisplay = false;
            synchronized (lastDisplayEventId) {
                if (eventId > lastDisplayEventId.getObject()) {
                    lastDisplayEventId.setObject(eventId);
                    updateDisplay = true;
                }
            }

            if (updateDisplay) {
                final Parcelable object = intent.getParcelableExtra(ExternalCalculatorHelper.DISPLAY_STATE_EXTRA);
                if (object instanceof CalculatorDisplayViewState) {
                    onDisplayStateChanged(context, (CalculatorDisplayViewState) object);
                }
            }
        }
    }

    protected void updateState(@NotNull Context context,
                             @NotNull CalculatorEditorViewState editorViewState,
                             @NotNull CalculatorDisplayViewState displayViewState) {
        stateUpdater.updateState(context, editorViewState, displayViewState);
    }

    protected void onDisplayStateChanged(@NotNull Context context, @NotNull CalculatorDisplayViewState displayViewState) {
        updateState(context, Locator.getInstance().getEditor().getViewState(), displayViewState);
    }

    protected void onEditorStateChanged(@NotNull Context context, @NotNull CalculatorEditorViewState editorViewState) {
        updateState(context, editorViewState, Locator.getInstance().getDisplay().getViewState());
    }
}
