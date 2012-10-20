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
    *                           CALCULATION
    *                           org.solovyev.android.calculator.CalculatorEvaluationEventData
    *
    **********************************************************************
    */


    // @NotNull CalculatorEditorViewState
    manual_calculation_requested,

    // @NotNull org.solovyev.android.calculator.CalculatorOutput
    calculation_result,

    calculation_cancelled,

    // @NotNull org.solovyev.android.calculator.CalculatorFailure
    calculation_failed,

    /*
    **********************************************************************
    *
    *                           CONVERSION
    *                           CalculatorConversionEventData
    *
    **********************************************************************
    */
    conversion_started,

    // @NotNull String conversion result
    conversion_result,

    // @NotNull ConversionFailure
    conversion_failed,

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
    editor_state_changed_light,

    // @NotNull CalculatorDisplayChangeEventData
    display_state_changed,

    /*
    **********************************************************************
    *
    *                           ENGINE
    *
    **********************************************************************
    */

    engine_preferences_changed,

    /*
    **********************************************************************
    *
    *                           HISTORY
    *
    **********************************************************************
    */

    // @NotNull CalculatorHistoryState
   history_state_added,

    // @NotNull CalculatorHistoryState
   use_history_state,

   clear_history_requested,

    /*
    **********************************************************************
    *
    *                           MATH ENTITIES
    *
    **********************************************************************
    */

    // @NotNull IConstant
    use_constant,

    // @NotNull Function
    use_function,

    // @NotNull Operator
    use_operator,

    // @NotNull IConstant
    constant_added,

    // @NotNull Change<IConstant>
    constant_changed,

    // @NotNull IConstant
    constant_removed,

    /*
    **********************************************************************
    *
    *                           OTHER
    *
    **********************************************************************
    */
    show_history,
    show_history_detached,

    show_functions,
    show_functions_detached,

    show_vars,
    show_vars_detached,

    open_app,

    show_operators,
    show_operators_detached,

    show_settings,
    show_settings_detached,

    show_like_dialog;

    public boolean isOfType(@NotNull CalculatorEventType... types) {
        for (CalculatorEventType type : types) {
            if ( this == type ) {
                return true;
            }
        }

        return false;
    }

}
