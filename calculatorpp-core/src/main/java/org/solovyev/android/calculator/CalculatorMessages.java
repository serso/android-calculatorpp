package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 8:10 PM
 */
public final class CalculatorMessages {

    @NotNull
    private static final Map<Locale, ResourceBundle> bundlesByLocale = new HashMap<Locale, ResourceBundle>();

    private CalculatorMessages() {
        throw new AssertionError();
    }

    @NotNull
    public static ResourceBundle getBundle() {
        return getBundle(Locale.getDefault());
    }

    @NotNull
    public static ResourceBundle getBundle(@NotNull Locale locale) {
        synchronized (bundlesByLocale) {
            ResourceBundle result = bundlesByLocale.get(locale);
            if (result == null) {
                result = ResourceBundle.getBundle("org/solovyev/android/calculator/messages", locale);
                bundlesByLocale.put(locale, result);
            }

            return result;
        }
    }

    /* Arithmetic error occurred: {0} */
    @NotNull
    public static final String msg_001 = "msg_1";

    /* Too complex expression*/
    @NotNull
    public static final String msg_002 = "msg_2";

    /* Too long execution time - check the expression*/
    @NotNull
    public static final String msg_003 = "msg_3";

    /* Evaluation was cancelled*/
    @NotNull
    public static final String msg_004 = "msg_4";

    /* No parameters are specified for function: {0}*/
    @NotNull
    public static final String msg_005 = "msg_5";

    /* Infinite loop is detected in expression*/
    @NotNull
    public static final String msg_006 = "msg_6";

    /* Error */
    @NotNull
    public static final String syntax_error = "syntax_error";
}
