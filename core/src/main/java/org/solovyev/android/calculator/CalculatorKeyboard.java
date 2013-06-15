package org.solovyev.android.calculator;

import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 1:08 PM
 */
public interface CalculatorKeyboard {

	void buttonPressed(@Nullable String text);

	void roundBracketsButtonPressed();

	void pasteButtonPressed();

	void clearButtonPressed();

	void copyButtonPressed();

	void moveCursorLeft();

	void moveCursorRight();
}
