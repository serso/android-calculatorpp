package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: Solovyev_S
 * Date: 21.09.12
 * Time: 13:46
 */
public interface CalculatorEditorChangeEventData {

    @NotNull
    CalculatorEditorViewState getOldState();

    @NotNull
    CalculatorEditorViewState getNewState();
}
