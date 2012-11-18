package org.solovyev.android.calculator.widget;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.*;

/**
 * User: serso
 * Date: 10/19/12
 * Time: 11:11 PM
 */
public class CalculatorWidgetHelper implements CalculatorEventListener {

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

                    AbstractCalculatorWidgetProvider.onEditorStateChanged(CalculatorApplication.getInstance(), calculatorEventData, newEditorState);
                    break;

                case display_state_changed:
                    final CalculatorDisplayChangeEventData displayChangeData = (CalculatorDisplayChangeEventData) data;
                    final CalculatorDisplayViewState newDisplayState = displayChangeData.getNewValue();

                    CalculatorLocatorImpl.getInstance().getNotifier().showDebugMessage(TAG, "Display state changed: " + newDisplayState.getText());

                    AbstractCalculatorWidgetProvider.onDisplayStateChanged(CalculatorApplication.getInstance(), calculatorEventData, newDisplayState);
                    break;
            }
        }
    }
}
