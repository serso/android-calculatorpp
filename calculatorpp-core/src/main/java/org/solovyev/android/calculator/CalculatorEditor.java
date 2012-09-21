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

    /*
    **********************************************************************
    *
    *                           CURSOR CONTROL
    *
    **********************************************************************
    */
    /**
     * Method sets the cursor to the beginning
     */
    @NotNull
    public CalculatorEditorViewState setCursorOnStart();

    /**
     * Method sets the cursor to the end
     */
    @NotNull
    public CalculatorEditorViewState setCursorOnEnd();

    /**
     * Method moves cursor to the left of current position
     */
    @NotNull
    public CalculatorEditorViewState moveCursorLeft();

    /**
     * Method moves cursor to the right of current position
     */
    @NotNull
    public CalculatorEditorViewState moveCursorRight();


    /*
    **********************************************************************
    *
    *                           EDITOR OPERATIONS
    *
    **********************************************************************
    */
    @NotNull
    CalculatorEditorViewState erase();

    @NotNull
    CalculatorEditorViewState setText(@NotNull String text);

    @NotNull
    CalculatorEditorViewState setText(@NotNull String text, int selection);

    @NotNull
    CalculatorEditorViewState insert(@NotNull String text);

    @NotNull
    CalculatorEditorViewState moveSelection(int offset);

    @NotNull
    CalculatorEditorViewState setSelection(int selection);
}
