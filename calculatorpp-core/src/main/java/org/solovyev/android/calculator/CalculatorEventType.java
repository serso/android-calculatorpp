package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:40
 */
public enum CalculatorEventType {

    /*
    **********************************************************************
    *
    * org.solovyev.android.calculator.CalculatorEvaluationEventData
    *
    **********************************************************************
    */

    // @NotNull org.solovyev.android.calculator.CalculatorInput
    calculation_started,

    // @NotNull org.solovyev.android.calculator.CalculatorOutput
    calculation_result,

    calculation_cancelled,

    calculation_finished,

    // @NotNull org.solovyev.android.calculator.CalculatorFailure
    calculation_failed,

    /*
    **********************************************************************
    *
    *                           CONVERSION
    *
    **********************************************************************
    */
    conversion_started,

    // @NotNull String conversion result
    conversion_finished,

    /*
    **********************************************************************
    *
    *                           EDITOR
    *
    **********************************************************************
    */

    // @NotNull org.solovyev.android.calculator.CalculatorEditorChangeEventData
    editor_state_changed,

    // @NotNull CalculatorDisplayChangeEventData
    display_state_changed;

    public boolean isOfType(@NotNull CalculatorEventType... types) {
        for (CalculatorEventType type : types) {
            if ( this == type ) {
                return true;
            }
        }

        return false;
    }

}
