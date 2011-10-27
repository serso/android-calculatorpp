/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.model;

import android.content.Context;
import android.content.SharedPreferences;
import jscl.math.Expression;
import jscl.text.ParseInterruptedException;
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

	public static final String ROUND_RESULT_P_KEY = "org.solovyev.android.calculator.CalculatorModel_round_result";
	public static final boolean ROUND_RESULT_DEFAULT = true;

	public static final String RESULT_PRECISION_P_KEY = "org.solovyev.android.calculator.CalculatorModel_result_precision";
	public static final String RESULT_PRECISION_DEFAULT = "5";

	@NotNull
	private final Object lock = new Object();

	private boolean roundResult = true;

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

	// calculation thread timeout in milliseconds, after timeout thread would be interrupted
	private int timeout = 3000;

	@NotNull
	public String format(@NotNull Double value) {
		return format(value, true);
	}

	@NotNull
	public String format(@NotNull Double value, boolean round) {
		if (!value.isInfinite() && !value.isNaN()) {
			final DecimalFormat df = new DecimalFormat();
			df.setDecimalFormatSymbols(decimalGroupSymbols);
			df.setGroupingUsed(useGroupingSeparator);
			if (round ) {
				if (isRoundResult()) {
					df.setMaximumFractionDigits(instance.getPrecision());
					return df.format(new BigDecimal(value).setScale(instance.getPrecision(), BigDecimal.ROUND_HALF_UP).doubleValue());
				} else {
					return String.valueOf(value);
				}
			} else {
				return df.format(value);
			}
		} else {
			return String.valueOf(value);
		}
	}

	public static class Result {
		@NotNull
		private String result;

		@NotNull
		private JsclOperation userOperation;

		public Result(@NotNull String result, @NotNull JsclOperation userOperation) {
			this.result = result;
			this.userOperation = userOperation;
		}

		@NotNull
		public String getResult() {
			return result;
		}

		@NotNull
		public JsclOperation getUserOperation() {
			return userOperation;
		}
	}

	public Result evaluate(@NotNull JsclOperation operation,
						   @NotNull String expression) throws ParseException {
		return evaluate(operation, expression, null);
	}

	public Result evaluate(@NotNull JsclOperation operation,
						   @NotNull String expression,
						   @Nullable MessageRegistry<AndroidMessage> mr) throws ParseException {
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

			final String jsclExpression = sb.toString();
			final JsclOperation finalOperation = operation;

			final String result;
			if (!tooLongExecutionCache.contains(jsclExpression)) {
				final MutableObject<Object> calculationResult = new MutableObject<Object>(null);
				final MutableObject<ParseException> exception = new MutableObject<ParseException>(null);
				final MutableObject<Thread> calculationThread = new MutableObject<Thread>(null);

				final CountDownLatch latch = new CountDownLatch(1);

				new Thread(new Runnable() {
					@Override
					public void run() {
						final Thread thread = Thread.currentThread();
						try {
							//Log.d(CalculatorEngine.class.getName(), "Calculation thread started work: " + thread.getName());
							calculationThread.setObject(thread);
							calculationResult.setObject(finalOperation.evaluate(Expression.valueOf(jsclExpression)));
						} catch (jscl.text.ParseException e) {
							exception.setObject(new ParseException(e));
						} catch (ParseInterruptedException e) {
							System.out.print("Interrupted!");
						  // do nothing - we ourselves interrupt the calculations
						} finally {
							//Log.d(CalculatorEngine.class.getName(), "Calculation thread ended work: " + thread.getName());
							calculationThread.setObject(null);
							latch.countDown();
						}
					}
				}).start();

				try {
					//Log.d(CalculatorEngine.class.getName(), "Main thread is waiting: " + Thread.currentThread().getName());
					latch.await(timeout, TimeUnit.MILLISECONDS);
					//Log.d(CalculatorEngine.class.getName(), "Main thread got up: " + Thread.currentThread().getName());

					final ParseException evalErrorLocal = exception.getObject();
					final Object calculationResultLocal = calculationResult.getObject();
					final Thread calculationThreadLocal = calculationThread.getObject();

					if (calculationThreadLocal != null) {
						// todo serso: interrupt doesn't stop the thread but it MUST be killed
						threadKiller.killThread(calculationThreadLocal);
						//calculationThreadLocal.stop();
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

			return new Result(operation.getFromProcessor().process(result), operation);
		}
	}

	private int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	private boolean isRoundResult() {
		return roundResult;
	}

	public void setRoundResult(boolean roundResult) {
		this.roundResult = roundResult;
	}

	public void init(@Nullable Context context, @Nullable SharedPreferences preferences) {
		synchronized (lock) {
			reset(context, preferences);
		}
	}

	public void reset(@Nullable Context context, @Nullable SharedPreferences preferences) {
		synchronized (lock) {
			if (preferences != null) {
				final NumberMapper<Integer> integerNumberMapper = new NumberMapper<Integer>(Integer.class);
				//noinspection ConstantConditions
				this.setPrecision(integerNumberMapper.parseValue(preferences.getString(RESULT_PRECISION_P_KEY, RESULT_PRECISION_DEFAULT)));
				this.setRoundResult(preferences.getBoolean(ROUND_RESULT_P_KEY, ROUND_RESULT_DEFAULT));

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

	// for tests
	void setTimeout(int timeout) {
		this.timeout = timeout;
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
