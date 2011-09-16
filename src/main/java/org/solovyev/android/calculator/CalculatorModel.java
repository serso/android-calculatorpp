/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.util.Log;
import bsh.EvalError;
import bsh.Interpreter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.exceptions.SersoException;
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

	public String evaluate(@NotNull JsclOperation operation, @NotNull String expression ) throws EvalError, ParseException {

		final String preprocessedExpression = Preprocessor.process(expression);

		Log.d(CalculatorModel.class.getName(), "Preprocessed expression: " + preprocessedExpression);

		Object evaluationObject = interpreter.eval(Preprocessor.wrap(operation, preprocessedExpression));
		String result = String.valueOf(evaluationObject).trim();

		try {
			final Double dResult = Double.valueOf(result);
			result = String.valueOf(MathUtils.round(dResult, NUMBER_OF_FRACTION_DIGITS));
		} catch (NumberFormatException e) {
			throw new ParseException(e);
		}

		return result;
	}

	public static class ParseException extends SersoException {
		public ParseException(Throwable cause) {
			super(cause);
		}
	}
}
