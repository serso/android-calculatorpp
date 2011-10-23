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
import org.solovyev.common.utils.MutableObject;
import org.solovyev.common.utils.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * User: serso
 * Date: 9/12/11
 * Time: 11:38 PM
 */

public enum CalculatorEngine {

	instance;

	public static final String GROUPING_SEPARATOR_P_KEY = "org.solovyev.android.calculator.CalculatorActivity_calc_grouping_separator";
	private static final String GROUPING_SEPARATOR_DEFAULT = " ";

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

	@NotNull
	private final static Set<String> tooLongExecutionCache = new HashSet<String>();

	@NotNull
	private DecimalFormatSymbols decimalGroupSymbols = new DecimalFormatSymbols(Locale.getDefault());
	{
		decimalGroupSymbols.setDecimalSeparator('.');
		decimalGroupSymbols.setGroupingSeparator(GROUPING_SEPARATOR_DEFAULT.charAt(0));
	}

	private boolean useGroupingSeparator = true;

	@NotNull
	private ThreadKiller threadKiller = new AndroidThreadKiller();

	@NotNull
	public String format(@NotNull Double value) {
		if (!value.isInfinite() && !value.isNaN()) {
			final DecimalFormat df = new DecimalFormat();
			df.setDecimalFormatSymbols(decimalGroupSymbols);
			df.setGroupingUsed(useGroupingSeparator);
			df.setMaximumFractionDigits(instance.getPrecision());
			return df.format(new BigDecimal(value).setScale(instance.getPrecision(), BigDecimal.ROUND_HALF_UP).doubleValue());
		} else {
			return String.valueOf(value);
		}
	}

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

			final String jsclExpression = ToJsclTextProcessor.wrap(operation, sb.toString());

			final String result;
			if (!tooLongExecutionCache.contains(jsclExpression)) {
				final MutableObject<Object> calculationResult = new MutableObject<Object>(null);
				final MutableObject<EvalError> exception = new MutableObject<EvalError>(null);
				final MutableObject<Thread> calculationThread = new MutableObject<Thread>(null);

				final CountDownLatch latch = new CountDownLatch(1);

				new Thread(new Runnable() {
					@Override
					public void run() {
						final Thread thread = Thread.currentThread();
						try {
							//Log.d(CalculatorEngine.class.getName(), "Calculation thread started work: " + thread.getName());
							calculationThread.setObject(thread);
							calculationResult.setObject(interpreter.eval(jsclExpression));
						} catch (EvalError evalError) {
							exception.setObject(evalError);
						} finally {
							//Log.d(CalculatorEngine.class.getName(), "Calculation thread ended work: " + thread.getName());
							calculationThread.setObject(null);
							latch.countDown();
						}
					}
				}).start();

				try {
					//Log.d(CalculatorEngine.class.getName(), "Main thread is waiting: " + Thread.currentThread().getName());
					latch.await(3, TimeUnit.SECONDS);
					//Log.d(CalculatorEngine.class.getName(), "Main thread got up: " + Thread.currentThread().getName());

					final EvalError evalErrorLocal = exception.getObject();
					final Object calculationResultLocal = calculationResult.getObject();
					final Thread calculationThreadLocal = calculationThread.getObject();

					if (calculationThreadLocal != null) {
						// todo serso: interrupt doesn't stop the thread but it MUST be killed
						threadKiller.killThread(calculationThreadLocal);
						//calculationThreadLocal.stop();
						resetInterpreter();
					}

					if ( evalErrorLocal != null ) {
						throw evalErrorLocal;
					}

					if ( calculationResultLocal == null ) {
						tooLongExecutionCache.add(jsclExpression);
						throw new ParseException("Too long calculation for: " + jsclExpression);
					}

				} catch (InterruptedException e) {
					throw new ParseException(e);
				}

				result = String.valueOf(calculationResult.getObject()).trim();
			} else {
				throw new ParseException("Too long calculation for: " + jsclExpression);
			}

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

				final String groupingSeparator = preferences.getString(GROUPING_SEPARATOR_P_KEY, GROUPING_SEPARATOR_DEFAULT);
				if (StringUtils.isEmpty(groupingSeparator)) {
					this.useGroupingSeparator = false;
				} else {
					this.useGroupingSeparator = true;
					this.decimalGroupSymbols.setGroupingSeparator(groupingSeparator.charAt(0));
				}
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

	//for tests only
	void setDecimalGroupSymbols(@NotNull DecimalFormatSymbols decimalGroupSymbols) {
		synchronized (lock) {
			this.decimalGroupSymbols = decimalGroupSymbols;
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

	// for tests only
	void setThreadKiller(@NotNull ThreadKiller threadKiller) {
		this.threadKiller = threadKiller;
	}

	private static interface ThreadKiller {
		void killThread(@NotNull Thread thread);
	}

	private static class AndroidThreadKiller implements ThreadKiller {
		@Override
		public void killThread(@NotNull Thread thread) {
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.interrupt();
		}
	}

	public static class ThreadKillerImpl implements ThreadKiller {
		@Override
		public void killThread(@NotNull Thread thread) {
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.stop();
		}
	}
}
