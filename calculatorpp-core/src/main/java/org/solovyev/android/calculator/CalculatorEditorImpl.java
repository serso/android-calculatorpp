package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: Solovyev_S
 * Date: 21.09.12
 * Time: 11:53
 */
public class CalculatorEditorImpl implements CalculatorEditor {

    @Nullable
    private CalculatorEditorView view;

    @NotNull
    private final Object viewLock = new Object();

    @NotNull
    private CalculatorEditorViewState lastViewState = CalculatorEditorViewStateImpl.newDefaultInstance();

    @Override
    public void setView(@Nullable CalculatorEditorView view) {
        synchronized (viewLock) {
            this.view = view;

            if ( view != null ) {
                view.setState(lastViewState);
            }
        }
    }

    @NotNull
    @Override
    public CalculatorEditorViewState getViewState() {
        return lastViewState;
    }

    @Override
    public void setViewState(@NotNull CalculatorEditorViewState viewState) {
        synchronized (viewLock) {
            this.lastViewState = viewState;
            if (this.view != null) {
                this.view.setState(viewState);
            }
        }
    }

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData,
                                  @NotNull CalculatorEventType calculatorEventType,
                                  @Nullable Object data) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setCursorOnStart() {
        synchronized (viewLock) {
            newSelectionViewState(0);
        }
    }

    private void newSelectionViewState(int newSelection) {
        setViewState(CalculatorEditorViewStateImpl.newSelection(this.lastViewState, newSelection));
    }

    public void setCursorOnEnd() {
        synchronized (viewLock) {
            newSelectionViewState(this.lastViewState.getText().length());
        }
    }

    public void moveCursorLeft() {
        synchronized (viewLock) {
            if (this.lastViewState.getSelection() > 0) {
                newSelectionViewState(this.lastViewState.getSelection() - 1);
            }
        }
    }

    public void moveCursorRight() {
        synchronized (viewLock) {
            if (this.lastViewState.getSelection() < this.lastViewState.getText().length()) {
                newSelectionViewState(this.lastViewState.getSelection() + 1);
            }
        }
    }
}
