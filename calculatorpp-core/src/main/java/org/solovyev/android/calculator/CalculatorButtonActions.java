package org.solovyev.android.calculator;

/**
 * User: Solovyev_S
 * Date: 19.10.12
 * Time: 17:31
 */
public final class CalculatorButtonActions {

    public static final String ERASE = "erase";
    public static final String PASTE = "paste";
    public static final String COPY = "copy";
    public static final String CLEAR = "clear";
    public static final String SHOW_FUNCTIONS = "functions";
    public static final String SHOW_VARS = "vars";
    public static final String SHOW_OPERATORS = "operators";

    private CalculatorButtonActions() {
        throw new AssertionError();
    }

    public static final String SHOW_HISTORY = "history";
    public static final String MOVE_CURSOR_RIGHT = "▶";
    public static final String MOVE_CURSOR_LEFT = "◀";
}
