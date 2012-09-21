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

    @NotNull
    private final Calculator calculator;

    public CalculatorEditorImpl(@NotNull Calculator calculator) {
        this.calculator = calculator;
    }

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
    public void setViewState(@NotNull CalculatorEditorViewState newViewState) {
        synchronized (viewLock) {
            final CalculatorEditorViewState oldViewState = this.lastViewState;

            this.lastViewState = newViewState;
            if (this.view != null) {
                this.view.setState(newViewState);
            }

            calculator.fireCalculatorEvent(CalculatorEventType.editor_state_changed, new CalculatorEditorChangeEventDataImpl(oldViewState, newViewState));
        }
    }

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData,
                                  @NotNull CalculatorEventType calculatorEventType,
                                  @Nullable Object data) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    public CalculatorEditorViewState setCursorOnStart() {
        synchronized (viewLock) {
            return newSelectionViewState(0);
        }
    }

    @NotNull
    private CalculatorEditorViewState newSelectionViewState(int newSelection) {
        if (this.lastViewState.getSelection() != newSelection) {
            final CalculatorEditorViewState result = CalculatorEditorViewStateImpl.newSelection(this.lastViewState, newSelection);
            setViewState(result);
            return result;
        } else {
            return this.lastViewState;
        }
    }

    @NotNull
    public CalculatorEditorViewState setCursorOnEnd() {
        synchronized (viewLock) {
            return newSelectionViewState(this.lastViewState.getText().length());
        }
    }

    @NotNull
    public CalculatorEditorViewState moveCursorLeft() {
        synchronized (viewLock) {
            if (this.lastViewState.getSelection() > 0) {
                return newSelectionViewState(this.lastViewState.getSelection() - 1);
            } else {
                return this.lastViewState;
            }
        }
    }

    @NotNull
    public CalculatorEditorViewState moveCursorRight() {
        synchronized (viewLock) {
            if (this.lastViewState.getSelection() < this.lastViewState.getText().length()) {
                return newSelectionViewState(this.lastViewState.getSelection() + 1);
            } else {
                return this.lastViewState;
            }
        }
    }

    @NotNull
    @Override
    public CalculatorEditorViewState erase() {
        synchronized (viewLock) {
            int selection = this.lastViewState.getSelection();
            final String text = this.lastViewState.getText();
            if (selection > 0 && text.length() > 0 && selection <= text.length()) {
                final StringBuilder newText = new StringBuilder(text.length() - 1);
                newText.append(text.substring(0, selection - 1)).append(text.substring(selection, text.length()));

                final CalculatorEditorViewState result = CalculatorEditorViewStateImpl.newInstance(newText.toString(), selection - 1);
                setViewState(result);
                return result;
            } else {
                return this.lastViewState;
            }
        }
    }

    @NotNull
    @Override
    public CalculatorEditorViewState setText(@NotNull String text) {
        synchronized (viewLock) {
            final CalculatorEditorViewState result = CalculatorEditorViewStateImpl.newInstance(text, text.length());
            setViewState(result);
            return result;
        }
    }

    @NotNull
    @Override
    public CalculatorEditorViewState setText(@NotNull String text, int selection) {
        synchronized (viewLock) {
            selection = correctSelection(selection, text);

            final CalculatorEditorViewState result = CalculatorEditorViewStateImpl.newInstance(text, selection);
            setViewState(result);
            return result;
        }
    }

    @NotNull
    @Override
    public CalculatorEditorViewState insert(@NotNull String text) {
        synchronized (viewLock) {
            final int selection = this.lastViewState.getSelection();
            final String oldText = this.lastViewState.getText();

            final StringBuilder newText = new StringBuilder(text.length() + oldText.length());

            newText.append(oldText.substring(0, selection));
            newText.append(text);
            newText.append(oldText.substring(selection));

            final CalculatorEditorViewState result = CalculatorEditorViewStateImpl.newInstance(newText.toString(), text.length() + selection);
            setViewState(result);
            return result;
        }
    }

    @NotNull
    @Override
    public CalculatorEditorViewState moveSelection(int offset) {
        synchronized (viewLock) {
            int selection = this.lastViewState.getSelection() + offset;

            return setSelection(selection);
        }
    }

    @NotNull
    @Override
    public CalculatorEditorViewState setSelection(int selection) {
        synchronized (viewLock) {
            selection = correctSelection(selection, this.lastViewState.getText());

            final CalculatorEditorViewState result = CalculatorEditorViewStateImpl.newSelection(this.lastViewState, selection);
            setViewState(result);
            return result;
        }
    }

    private int correctSelection(int selection, @NotNull String text) {
        int result = Math.max(selection, 0);
        result = Math.min(result, text.length());
        return result;
    }
}
