package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: Solovyev_S
 * Date: 21.09.12
 * Time: 11:47
 */
public interface CalculatorEditor extends CalculatorEventListener/*, CursorControl*/ {

    void setView(@Nullable CalculatorEditorView view);

    @NotNull
    CalculatorEditorViewState getViewState();

    void setViewState(@NotNull CalculatorEditorViewState viewState);
}
