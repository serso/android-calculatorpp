/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.model;

import android.content.Context;
import android.content.SharedPreferences;
import bsh.EvalError;
import bsh.Interpreter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.msg.AndroidMessage;
import org.solovyev.common.NumberMapper;
import org.solovyev.common.msg.MessageRegistry;
import org.solovyev.common.msg.MessageType;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.Formatter;

/**
 * User: serso
 * Date: 9/12/11
 * Time: 11:38 PM
 */

public enum CalculatorEngine {

	instance;

	private static final String RESULT_PRECISION_P_KEY = "org.solovyev.android.calculator.CalculatorModel_result_precision";
	private static final String RESULT_PRECISION_DEFAULT = "5";

	@NotNull
	private Interpreter interpreter;

	@NotNull
	private final Object lock = new Object();

	private int precision = 5;

	@NotNull
	public final TextProcessor<PreparedExpression> preprocessor = new ToJsclTextProcessor();

	@NotNull
	private final VarsRegisterImpl varsRegister = new VarsRegisterImpl();

	public String evaluate(@NotNull JsclOperation operation,
						   @NotNull String expression) throws EvalError, ParseException {
		return evaluate(operation, expression, null);
	}

	public String evaluate(@NotNull JsclOperation operation,
						   @NotNull String expression,
						   @Nullable MessageRegistry<AndroidMessage> mr) throws EvalError, ParseException {
		synchronized (lock) {
			final StringBuilder sb = new StringBuilder();

			final PreparedExpression preparedExpression = preprocessor.process(expression);
			sb.append(preparedExpression);

			//Log.d(CalculatorEngine.class.getName(), "Preprocessed expression: " + preprocessedExpression);
			if (operation == JsclOperation.numeric && preparedExpression.isExistsUndefinedVar()) {
				operation = JsclOperation.simplify;

				if (mr != null) {
					final String undefinedVars = CollectionsUtils.formatValue(preparedExpression.getUndefinedVars(), ", ", new Formatter<Var>() {
						@Override
						public String formatValue(@Nullable Var var) throws IllegalArgumentException {
							return var != null ? var.getName() : "";
						}
					});

					mr.addMessage(new AndroidMessage(R.string.c_simplify_instead_of_numeric, MessageType.info, undefinedVars));
				}
			}

			final Object evaluationObject = interpreter.eval(ToJsclTextProcessor.wrap(operation, sb.toString()));

			final String result = String.valueOf(evaluationObject).trim();

			return operation.getFromProcessor().process(result);
		}
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public void init(@Nullable Context context, @Nullable SharedPreferences preferences) throws EvalError {
		synchronized (lock) {
			reset(context, preferences);
			resetInterpreter();
		}
	}

	public void reset(@Nullable Context context, @Nullable SharedPreferences preferences) {
		synchronized (lock) {
			if (preferences != null) {
				final NumberMapper<Integer> integerNumberMapper = new NumberMapper<Integer>(Integer.class);
				//noinspection ConstantConditions
				this.setPrecision(integerNumberMapper.parseValue(preferences.getString(RESULT_PRECISION_P_KEY, RESULT_PRECISION_DEFAULT)));
			}

			varsRegister.init(context, preferences);
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
