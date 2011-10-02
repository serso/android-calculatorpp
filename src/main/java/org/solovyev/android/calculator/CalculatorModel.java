/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import bsh.EvalError;
import bsh.Interpreter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.NumberMapper;
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

	@NotNull
	private final Object interpreterMonitor = new Object();

	private int numberOfFractionDigits = 5;

	@NotNull
	public final Preprocessor preprocessor = new ToJsclPreprocessor();

	@NotNull
	private final VarsRegister varsRegister = new VarsRegister();

	private static CalculatorModel instance;

	private CalculatorModel(@Nullable Context context) {
		load(context);

		reset();
	}

	public void reset() {
		synchronized (interpreterMonitor) {
			try {
				interpreter = new Interpreter();
				interpreter.eval(ToJsclPreprocessor.wrap(JsclOperation.importCommands, "/jscl/editorengine/commands"));

				/*for (Var var : varsRegister.getVars()) {
					if (!var.isSystem()) {
						exec(var.getName() + "=" + var.getValue() + ";");
					}
				}*/
			} catch (EvalError evalError) {
				throw new RuntimeException(evalError);
			}
		}
	}

	public String evaluate(@NotNull JsclOperation operation, @NotNull String expression) throws EvalError, ParseException {

		final StringBuilder sb = new StringBuilder();

		sb.append(preprocessor.process(expression));

		//Log.d(CalculatorModel.class.getName(), "Preprocessed expression: " + preprocessedExpression);

		final Object evaluationObject;
		synchronized (interpreterMonitor) {
			evaluationObject = interpreter.eval(ToJsclPreprocessor.wrap(operation, sb.toString()));
		}
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

	public synchronized void load(@Nullable Context context) {
		if (context != null) {
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

			final NumberMapper<Integer> integerNumberMapper = new NumberMapper<Integer>(Integer.class);
			this.setNumberOfFractionDigits(integerNumberMapper.parseValue(preferences.getString(context.getString(R.string.p_calc_result_precision_key), context.getString(R.string.p_calc_result_precision))));
		}

		varsRegister.load(context);
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

	public static synchronized void init(@Nullable Context context) throws EvalError {
		if (!isLoaded()) {
			instance = new CalculatorModel(context);
		} else {
			throw new RuntimeException("Calculator model already instantiated!");
		}
	}

	public static CalculatorModel getInstance() {
		if (!isLoaded()) {
			throw new RuntimeException("CalculatorModel must be instantiated!");
		}

		return instance;
	}

	public static boolean isLoaded() {
		return instance != null;
	}


	private void exec(String str) throws EvalError {
		interpreter.eval(str);
	}

	private String eval(String str) throws EvalError {
		return interpreter.eval(commands(str)).toString();
	}


	@NotNull
	public VarsRegister getVarsRegister() {
		return varsRegister;
	}

	String commands(String str) {
		return commands(str, false);
	}

	String commands(String str, boolean found) {
		for (int i = 0; i < cmds.length; i++) {
			int n = str.length() - cmds[i].length() - 1;
			if (n >= 0 && (" " + cmds[i].toLowerCase()).equals(str.substring(n)))
				return commands(str.substring(0, n), true) + "." + cmds[i] + "()";
		}
		str = str.replaceAll("\n", "");
		return found ? "jscl.math.Expression.valueOf(\"" + str + "\")" : str;
	}

	static final String cmds[] = new String[]{"expand", "factorize", "elementary", "simplify", "numeric", "toMathML", "toJava"};
}
