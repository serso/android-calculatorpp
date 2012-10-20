package org.solovyev.android.calculator.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.*;

/**
 * User: serso
 * Date: 10/19/12
 * Time: 11:11 PM
 */
public class CalculatorWidgetHelper extends BroadcastReceiver implements CalculatorEventListener {

    private static final String TAG = "Calculator++ Widget Helper";

    @NotNull
    private final CalculatorEventHolder lastEvent = new CalculatorEventHolder(CalculatorUtils.createFirstEventDataId());

    public CalculatorWidgetHelper() {
        CalculatorLocatorImpl.getInstance().getCalculator().addCalculatorEventListener(this);
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

                    CalculatorLocatorImpl.getInstance().getNotifier().showDebugMessage(TAG, "Editor state changed: " + newEditorState.getText());

                    CalculatorWidgetProvider.onEditorStateChanged(CalculatorApplication.getInstance(), newEditorState);
                    break;

                case display_state_changed:
                    final CalculatorDisplayChangeEventData displayChangeData = (CalculatorDisplayChangeEventData) data;
                    final CalculatorDisplayViewState newDisplayState = displayChangeData.getNewValue();

                    CalculatorLocatorImpl.getInstance().getNotifier().showDebugMessage(TAG, "Display state changed: " + newDisplayState.getText());

                    CalculatorWidgetProvider.onDisplayStateChanged(CalculatorApplication.getInstance(), newDisplayState);
                    break;
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (CalculatorWidgetProvider.BUTTON_PRESSED_ACTION.equals(intent.getAction())) {
            final int buttonId = intent.getIntExtra(CalculatorWidgetProvider.BUTTON_ID_EXTRA, 0);
            //Toast.makeText(context, "Button id: " + buttonId, Toast.LENGTH_SHORT).show();

            final WidgetButton button = WidgetButton.getById(buttonId);
            if ( button != null ) {
                button.onClick(context);
            }
        }
    }
}
