/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import bsh.EvalError;
import bsh.Interpreter;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.exceptions.SersoException;
import org.solovyev.common.utils.MathUtils;
import org.solovyev.util.math.Complex;

/**
 * User: serso
 * Date: 9/12/11
 * Time: 11:38 PM
 */

public class CalculatorModel {

	@NotNull
	private Interpreter interpreter;

	private int numberOfFractionDigits = 5;

	@NotNull
	public Preprocessor preprocessor = new ToJsclPreprocessor();

	public CalculatorModel() throws EvalError {
		interpreter = new Interpreter();

		interpreter.eval(ToJsclPreprocessor.wrap(JsclOperation.importCommands, "/jscl/editorengine/commands"));
	}

	public String evaluate(@NotNull JsclOperation operation, @NotNull String expression) throws EvalError, ParseException {

		final String preprocessedExpression = preprocessor.process(expression);

		//Log.d(CalculatorModel.class.getName(), "Preprocessed expression: " + preprocessedExpression);

		Object evaluationObject = interpreter.eval(ToJsclPreprocessor.wrap(operation, preprocessedExpression));
		String result = String.valueOf(evaluationObject).trim();

		try {
			result = String.valueOf(round(result));
		} catch (NumberFormatException e) {
			if (result.contains("sqrt(-1)")) {
				try {
					result = createResultForComplexNumber(result.replace("sqrt(-1)", "i"));
				} catch (NumberFormatException e1) {
					// throw original one
					throw new ParseException(e);
				}

			} else {
				throw new ParseException(e);
			}
		}

		return result;
	}

	public String createResultForComplexNumber(@NotNull final String s) {
		final Complex complex = new Complex();

		String result = "";
		// may be it's just complex number
		int plusIndex = s.lastIndexOf("+");
		if (plusIndex >= 0) {
			complex.setReal(round(s.substring(0, plusIndex)));
			result += complex.getReal();
			result += "+";
		} else {
			plusIndex = s.lastIndexOf("-");
			if (plusIndex >= 0) {
				complex.setReal(round(s.substring(0, plusIndex)));
				result += complex.getReal();
				result += "-";
			}
		}


		int multiplyIndex = s.indexOf("*");
		if (multiplyIndex >= 0) {
			complex.setImaginary(round(s.substring(plusIndex >= 0 ? plusIndex + 1 : 0, multiplyIndex)));
			result += complex.getImaginary();

		}

		result += "i";

		return result;
	}

	private Double round(@NotNull String result) {
		final Double dResult = Double.valueOf(result);
		return MathUtils.round(dResult, numberOfFractionDigits);
	}

	public static class ParseException extends SersoException {
		public ParseException(Throwable cause) {
			super(cause);
		}
	}

	public int getNumberOfFractionDigits() {
		return numberOfFractionDigits;
	}

	public void setNumberOfFractionDigits(int numberOfFractionDigits) {
		this.numberOfFractionDigits = numberOfFractionDigits;
	}
}
