package org.solovyev.android.calculator;

import android.content.Context;
import jscl.JsclMathEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mockito.Mockito;
import org.solovyev.android.calculator.history.CalculatorHistory;

/**
 * User: serso
 * Date: 10/7/12
 * Time: 8:56 PM
 */
public class CalculatorTestUtils {

    public static void staticSetUp(@Nullable Context context) throws Exception {
        Locator.getInstance().init(new CalculatorImpl(), newCalculatorEngine(), Mockito.mock(CalculatorClipboard.class), Mockito.mock(CalculatorNotifier.class), Mockito.mock(CalculatorHistory.class), new SystemOutCalculatorLogger(), Mockito.mock(CalculatorPreferenceService.class), Mockito.mock(CalculatorKeyboard.class));
        Locator.getInstance().getEngine().init();

        if ( context != null ) {
            initViews(context);
        }
    }

    public static void initViews(@NotNull Context context) {
        final AndroidCalculatorEditorView editor = new AndroidCalculatorEditorView(context);
        editor.init(context);
        Locator.getInstance().getEditor().setView(editor);

        final AndroidCalculatorDisplayView display = new AndroidCalculatorDisplayView(context);
        display.init(context);
        Locator.getInstance().getDisplay().setView(display);
    }

    public static void staticSetUp() throws Exception {
        staticSetUp(null);
    }


    @NotNull
    static CalculatorEngineImpl newCalculatorEngine() {
        final MathEntityDao mathEntityDao = Mockito.mock(MathEntityDao.class);

        final JsclMathEngine jsclEngine = JsclMathEngine.getInstance();

        final CalculatorVarsRegistry varsRegistry = new CalculatorVarsRegistry(jsclEngine.getConstantsRegistry(), mathEntityDao);
        final CalculatorFunctionsMathRegistry functionsRegistry = new CalculatorFunctionsMathRegistry(jsclEngine.getFunctionsRegistry(), mathEntityDao);
        final CalculatorOperatorsMathRegistry operatorsRegistry = new CalculatorOperatorsMathRegistry(jsclEngine.getOperatorsRegistry(), mathEntityDao);
        final CalculatorPostfixFunctionsRegistry postfixFunctionsRegistry = new CalculatorPostfixFunctionsRegistry(jsclEngine.getPostfixFunctionsRegistry(), mathEntityDao);

        return new CalculatorEngineImpl(jsclEngine, varsRegistry, functionsRegistry, operatorsRegistry, postfixFunctionsRegistry, null);
    }
}
