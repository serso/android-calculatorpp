/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import bsh.EvalError;
import bsh.Interpreter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.JsclOperation;
import org.solovyev.common.NumberMapper;
import org.solovyev.common.utils.MutableObject;

/**
 * User: serso
 * Date: 9/12/11
 * Time: 11:38 PM
 */

public enum CalculatorModel {

	instance;

	private static final String RESULT_PRECISION_P_KEY = "org.solovyev.android.calculator.CalculatorModel_result_precision";
	private static final String RESULT_PRECISION_DEFAULT = "5";

	@NotNull
	private Interpreter interpreter;

	@NotNull
	private final Object lock = new Object();

	private int numberOfFractionDigits = 5;

	@NotNull
	public final TextProcessor preprocessor = new ToJsclTextProcessor();

	@NotNull
	public final TextProcessor postprocessor = new FromJsclTextProcessor();

	@NotNull
	private final VarsRegisterImpl varsRegister = new VarsRegisterImpl();

	public String evaluate(@NotNull JsclOperation operation, @NotNull String expression) throws EvalError, ParseException {
		synchronized (lock) {
			final StringBuilder sb = new StringBuilder();

			sb.append(preprocessor.process(expression));

			//Log.d(CalculatorModel.class.getName(), "Preprocessed expression: " + preprocessedExpression);

			final Object evaluationObject = interpreter.eval(ToJsclTextProcessor.wrap(operation, sb.toString()));

			return postprocessor.process(String.valueOf(evaluationObject).trim());
		}
	}

	public int getNumberOfFractionDigits() {
		return numberOfFractionDigits;
	}

	public void setNumberOfFractionDigits(int numberOfFractionDigits) {
		this.numberOfFractionDigits = numberOfFractionDigits;
	}

	public void init(@Nullable Context context) throws EvalError {
		synchronized (lock) {
			reset(context);
			resetInterpreter();
		}
	}

	public void reset(@Nullable Context context) {
		synchronized (lock) {
			if (context != null) {
				final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

				final NumberMapper<Integer> integerNumberMapper = new NumberMapper<Integer>(Integer.class);
				//noinspection ConstantConditions
				this.setNumberOfFractionDigits(integerNumberMapper.parseValue(preferences.getString(RESULT_PRECISION_P_KEY, RESULT_PRECISION_DEFAULT)));
			}

			varsRegister.init(context);
		}
	}

	public void resetInterpreter() {
		synchronized (lock) {
			try {
				interpreter = new Interpreter();
				interpreter.eval(ToJsclTextProcessor.wrap(JsclOperation.importCommands, "/jscl/editorengine/commands"));
			} catch (EvalError evalError) {
				throw new RuntimeException(evalError);
			}
		}
	}

	@NotNull
	public VarsRegister getVarsRegister() {
		return varsRegister;
	}

	/*	private String commands(String str) {
			return commands(str, false);
		}


		private void exec(String str) throws EvalError {
			interpreter.eval(str);
		}

		private String eval(String str) throws EvalError {
			return interpreter.eval(commands(str)).toString();
		}

		private String commands(String str, boolean found) {
			for (int i = 0; i < cmds.length; i++) {
				int n = str.length() - cmds[i].length() - 1;
				if (n >= 0 && (" " + cmds[i].toLowerCase()).equals(str.substring(n)))
					return commands(str.substring(0, n), true) + "." + cmds[i] + "()";
			}
			str = str.replaceAll("\n", "");
			return found ? "jscl.math.Expression.valueOf(\"" + str + "\")" : str;
		}

		private static final String cmds[] = new String[]{"expand", "factorize", "elementary", "simplify", "numeric", "toMathML", "toJava"};*/
}
