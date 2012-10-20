package org.solovyev.android.calculator.widget;

import android.content.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorLocatorImpl;
import org.solovyev.android.calculator.CalculatorSpecialButton;
import org.solovyev.android.calculator.R;

/**
* User: serso
* Date: 10/20/12
* Time: 12:05 AM
*/
enum WidgetButton {

    /*digits*/
    one(R.id.oneDigitButton, "1"),
    two(R.id.twoDigitButton, "2"),
    three(R.id.threeDigitButton, "3"),
    four(R.id.fourDigitButton, "4"),
    five(R.id.fiveDigitButton, "5"),
    six(R.id.sixDigitButton, "6"),
    seven(R.id.sevenDigitButton, "7"),
    eight(R.id.eightDigitButton, "8"),
    nine(R.id.nineDigitButton, "9"),
    zero(R.id.zeroDigitButton, "0"),

    period(R.id.periodButton, "."),
    brackets(R.id.roundBracketsButton, "()"),

    settings(R.id.settingsButton, CalculatorSpecialButton.settings_detached),
    like(R.id.likeButton, CalculatorSpecialButton.like),

    /*last row*/
    left(R.id.leftButton, CalculatorSpecialButton.cursor_left),
    right(R.id.rightButton, CalculatorSpecialButton.cursor_right),
    vars(R.id.varsButton, CalculatorSpecialButton.vars_detached),
    functions(R.id.functionsButton, CalculatorSpecialButton.functions_detached),
    app(R.id.appButton, CalculatorSpecialButton.open_app),
    history(R.id.historyButton, CalculatorSpecialButton.history_detached),

    /*operations*/
    multiplication(R.id.multiplicationButton, "*"),
    division(R.id.divisionButton, "/"),
    plus(R.id.plusButton, "+"),
    subtraction(R.id.subtractionButton, "-"),
    percent(R.id.percentButton, "%"),
    power(R.id.powerButton, "^"),

    /*last column*/
    clear(R.id.clearButton, CalculatorSpecialButton.clear),
    erase(R.id.eraseButton, CalculatorSpecialButton.erase),
    copy(R.id.copyButton, CalculatorSpecialButton.copy),
    paste(R.id.pasteButton, CalculatorSpecialButton.paste),

    /*equals*/
    equals(R.id.equalsButton, CalculatorSpecialButton.equals);


    private final int buttonId;

    @NotNull
    private final String text;

    WidgetButton(int buttonId, @NotNull CalculatorSpecialButton button) {
        this(buttonId, button.getActionCode());
    }

    WidgetButton(int buttonId, @NotNull String text) {
        this.buttonId = buttonId;
        this.text = text;
    }

    public void onClick(@NotNull Context context) {
        CalculatorLocatorImpl.getInstance().getNotifier().showDebugMessage("Calculator++ Widget", "Button pressed: " + text);
        CalculatorLocatorImpl.getInstance().getKeyboard().buttonPressed(text);
    }

    @Nullable
    public static WidgetButton getById(int buttonId) {
        for (WidgetButton widgetButton : values()) {
            if (widgetButton.buttonId == buttonId) {
                return widgetButton;
            }
        }

        return null;
    }

    public int getButtonId() {
        return buttonId;
    }
}
