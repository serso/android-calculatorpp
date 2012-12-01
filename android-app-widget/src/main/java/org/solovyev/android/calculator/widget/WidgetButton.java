package org.solovyev.android.calculator.widget;

import android.content.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorSpecialButton;
import org.solovyev.android.calculator.Locator;

import java.util.HashMap;
import java.util.Map;

/**
* User: serso
* Date: 10/20/12
* Time: 12:05 AM
*/
public enum WidgetButton {

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
    vars(R.id.vars_button, CalculatorSpecialButton.vars_detached),
    functions(R.id.functions_button, CalculatorSpecialButton.functions_detached),
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
    erase(R.id.eraseButton, CalculatorSpecialButton.erase, CalculatorSpecialButton.clear),
    copy(R.id.copyButton, CalculatorSpecialButton.copy),
    paste(R.id.pasteButton, CalculatorSpecialButton.paste),

    /*equals*/
    equals(R.id.equalsButton, CalculatorSpecialButton.equals);


    private final int buttonId;

    @NotNull
    private final String onClickText;

	@Nullable
	private final String onLongClickText;

    @NotNull
    private static Map<Integer, WidgetButton> buttonsByIds = new HashMap<Integer, WidgetButton>();

    WidgetButton(int buttonId, @NotNull CalculatorSpecialButton onClickButton, @Nullable CalculatorSpecialButton onLongClickButton) {
		this(buttonId, onClickButton.getActionCode(), onLongClickButton == null ? null : onLongClickButton.getActionCode());
	}

    WidgetButton(int buttonId, @NotNull CalculatorSpecialButton onClickButton) {
        this(buttonId, onClickButton, null);
    }

    WidgetButton(int buttonId, @NotNull String onClickText, @Nullable String onLongClickText) {
		this.buttonId = buttonId;
		this.onClickText = onClickText;
		this.onLongClickText = onLongClickText;

	}

    WidgetButton(int buttonId, @NotNull String onClickText) {
        this(buttonId, onClickText, null);
    }

    public void onLongClick(@NotNull Context context) {
		Locator.getInstance().getNotifier().showDebugMessage("Calculator++ Widget", "Button pressed: " + onLongClickText);
		if (onLongClickText != null) {
			Locator.getInstance().getKeyboard().buttonPressed(onLongClickText);
		}
	}

    public void onClick(@NotNull Context context) {
        Locator.getInstance().getNotifier().showDebugMessage("Calculator++ Widget", "Button pressed: " + onClickText);
        Locator.getInstance().getKeyboard().buttonPressed(onClickText);
    }

    @Nullable
    public static WidgetButton getById(int buttonId) {
        initButtonsByIdsMap();

        return buttonsByIds.get(buttonId);
    }

    private static void initButtonsByIdsMap() {
        if ( buttonsByIds.isEmpty() ) {
            // if not initialized

            final WidgetButton[] widgetButtons = values();

            final Map<Integer, WidgetButton> localButtonsByIds = new HashMap<Integer, WidgetButton>(widgetButtons.length);
            for (WidgetButton widgetButton : widgetButtons) {
                localButtonsByIds.put(widgetButton.getButtonId(), widgetButton);
            }

            buttonsByIds = localButtonsByIds;
        }
    }

    public int getButtonId() {
        return buttonId;
    }
}
