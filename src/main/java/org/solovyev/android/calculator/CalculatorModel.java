package org.solovyev.android.calculator;

import bsh.EvalError;
import bsh.Interpreter;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.utils.MathUtils;

/**
 * User: serso
 * Date: 9/12/11
 * Time: 11:38 PM
 */

public class CalculatorModel {

	@NotNull
	private Interpreter interpreter;

	private int NUMBER_OF_FRACTION_DIGITS = 5;

	public CalculatorModel() throws EvalError {
		interpreter = new Interpreter();

		interpreter.eval(Preprocessor.wrap(JsclOperation.importCommands, "/jscl/editorengine/commands"));
	}

	public String evaluate(@NotNull JsclOperation operation, @NotNull String expression ) throws EvalError {

		final String preprocessedString = Preprocessor.process(String.valueOf(expression));

		String result = String.valueOf(interpreter.eval(Preprocessor.wrap(operation, preprocessedString))).trim();

		try {
			final Double dResult = Double.valueOf(result);
			result = String.valueOf(MathUtils.round(dResult, NUMBER_OF_FRACTION_DIGITS));
		} catch (NumberFormatException e) {
			// do nothing  => it's normal if sometimes we don't have doubles as result
		}

		return result;
	}
}
