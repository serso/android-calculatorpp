/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.model;

import android.content.Context;
import android.content.SharedPreferences;
import jscl.*;
import jscl.math.Generic;
import jscl.math.function.Function;
import jscl.math.function.IConstant;
import jscl.math.operator.Operator;
import jscl.text.ParseInterruptedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorApplication;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.prefs.BooleanPreference;
import org.solovyev.android.prefs.Preference;
import org.solovyev.android.prefs.StringPreference;
import org.solovyev.common.MutableObject;
import org.solovyev.common.msg.MessageRegistry;
import org.solovyev.common.text.EnumMapper;
import org.solovyev.common.text.NumberMapper;
import org.solovyev.common.text.StringUtils;

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * User: serso
 * Date: 9/12/11
 * Time: 11:38 PM
 */

public enum CalculatorEngine {

	instance;

	private static final String GROUPING_SEPARATOR_P_KEY = "org.solovyev.android.calculator.CalculatorActivity_calc_grouping_separator";

	private static final String MULTIPLICATION_SIGN_P_KEY = "org.solovyev.android.calculator.CalculatorActivity_calc_multiplication_sign";
	private static final String MULTIPLICATION_SIGN_DEFAULT = "×";

	private static final String MAX_CALCULATION_TIME_P_KEY = "calculation.max_calculation_time";
	private static final String MAX_CALCULATION_TIME_DEFAULT = "5";

	private static final String SCIENCE_NOTATION_P_KEY = "calculation.output.science_notation";
	private static final boolean SCIENCE_NOTATION_DEFAULT = false;

	private static final String ROUND_RESULT_P_KEY = "org.solovyev.android.calculator.CalculatorModel_round_result";
	private static final boolean ROUND_RESULT_DEFAULT = true;

	private static final String RESULT_PRECISION_P_KEY = "org.solovyev.android.calculator.CalculatorModel_result_precision";
	private static final String RESULT_PRECISION_DEFAULT = "5";

	private static final String NUMERAL_BASES_P_KEY = "org.solovyev.android.calculator.CalculatorActivity_numeral_bases";
	private static final String NUMERAL_BASES_DEFAULT = "dec";

	private static final String ANGLE_UNITS_P_KEY = "org.solovyev.android.calculator.CalculatorActivity_angle_units";
	private static final String ANGLE_UNITS_DEFAULT = "deg";

	public static class Preferences {
		public static final Preference<String> groupingSeparator = StringPreference.newInstance(GROUPING_SEPARATOR_P_KEY, JsclMathEngine.GROUPING_SEPARATOR_DEFAULT);
		public static final Preference<String> multiplicationSign = StringPreference.newInstance(MULTIPLICATION_SIGN_P_KEY, MULTIPLICATION_SIGN_DEFAULT);
		public static final Preference<Integer> precision = StringPreference.newInstance(RESULT_PRECISION_P_KEY, RESULT_PRECISION_DEFAULT, new NumberMapper<Integer>(Integer.class));
		public static final Preference<Boolean> roundResult = new BooleanPreference(ROUND_RESULT_P_KEY, ROUND_RESULT_DEFAULT);
		public static final Preference<NumeralBase> numeralBase = StringPreference.newInstance(NUMERAL_BASES_P_KEY, NUMERAL_BASES_DEFAULT, EnumMapper.newInstance(NumeralBase.class));
		public static final Preference<AngleUnit> angleUnit = StringPreference.newInstance(ANGLE_UNITS_P_KEY, ANGLE_UNITS_DEFAULT, EnumMapper.newInstance(AngleUnit.class));
		public static final Preference<Boolean> scienceNotation = new BooleanPreference(SCIENCE_NOTATION_P_KEY, SCIENCE_NOTATION_DEFAULT);
		public static final Preference<Integer> maxCalculationTime = StringPreference.newInstance(MAX_CALCULATION_TIME_P_KEY, MAX_CALCULATION_TIME_DEFAULT, new NumberMapper<Integer>(Integer.class));

		private static final List<String> preferenceKeys = new ArrayList<String>();

		static {
			preferenceKeys.add(groupingSeparator.getKey());
			preferenceKeys.add(multiplicationSign.getKey());
			preferenceKeys.add(precision.getKey());
			preferenceKeys.add(roundResult.getKey());
			preferenceKeys.add(numeralBase.getKey());
			preferenceKeys.add(angleUnit.getKey());
			preferenceKeys.add(scienceNotation.getKey());
			preferenceKeys.add(maxCalculationTime.getKey());
		}

		@NotNull
		public static List<String> getPreferenceKeys() {
			return Collections.unmodifiableList(preferenceKeys);
		}
	}

	@NotNull
	private final Object lock = new Object();

	@NotNull
	private MathEngine engine = JsclMathEngine.instance;

	@NotNull
	public final TextProcessor<PreparedExpression, String> preprocessor = ToJsclTextProcessor.getInstance();

	@NotNull
	private final AndroidMathRegistry<IConstant> varsRegistry = new AndroidVarsRegistryImpl(engine.getConstantsRegistry());

	@NotNull
	private final AndroidMathRegistry<jscl.math.function.Function> functionsRegistry = new AndroidFunctionsMathRegistry(engine.getFunctionsRegistry());

	@NotNull
	private final AndroidMathRegistry<Operator> operatorsRegistry = new AndroidOperatorsMathRegistry(engine.getOperatorsRegistry());

	private final AndroidMathRegistry<Operator> postfixFunctionsRegistry = new AndroidPostfixFunctionsRegistry(engine.getPostfixFunctionsRegistry());

	@Nullable
	private ThreadKiller threadKiller = new AndroidThreadKiller();

	// calculation thread timeout in seconds, after timeout thread would be interrupted
	private int timeout = Integer.valueOf(MAX_CALCULATION_TIME_DEFAULT);

	@NotNull
	private String multiplicationSign = MULTIPLICATION_SIGN_DEFAULT;

	CalculatorEngine() {
		this.engine.setRoundResult(true);
		this.engine.setUseGroupingSeparator(true);
	}

	@NotNull
	public String getMultiplicationSign() {
		return multiplicationSign;
	}

	public void setMultiplicationSign(@NotNull String multiplicationSign) {
		this.multiplicationSign = multiplicationSign;
	}

	public static class Result {

		@NotNull
		private Generic genericResult;

		@NotNull
		private String result;

		@NotNull
		private JsclOperation userOperation;

		public Result(@NotNull String result, @NotNull JsclOperation userOperation, @NotNull Generic genericResult) {
			this.result = result;
			this.userOperation = userOperation;
			this.genericResult = genericResult;
		}

		@NotNull
		public String getResult() {
			return result;
		}

		@NotNull
		public JsclOperation getUserOperation() {
			return userOperation;
		}

		@NotNull
		public Generic getGenericResult() {
			return genericResult;
		}
	}

	public Result evaluate(@NotNull JsclOperation operation,
						   @NotNull String expression) throws CalculatorParseException, CalculatorEvalException {
		return evaluate(operation, expression, null);
	}

	public Result evaluate(@NotNull final JsclOperation operation,
						   @NotNull String expression,
						   @Nullable MessageRegistry mr) throws CalculatorParseException, CalculatorEvalException {
		synchronized (lock) {
			final StringBuilder sb = new StringBuilder();

			final PreparedExpression preparedExpression = preprocessor.process(expression);
			sb.append(preparedExpression);

			//Log.d(CalculatorEngine.class.getName(), "Preprocessed expression: " + preparedExpression);
			/*if (operation == JsclOperation.numeric && preparedExpression.isExistsUndefinedVar()) {
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
			}*/

			final String jsclExpression = sb.toString();

			final MutableObject<Generic> calculationResult = new MutableObject<Generic>(null);
			final MutableObject<CalculatorParseException> parseException = new MutableObject<CalculatorParseException>(null);
			final MutableObject<CalculatorEvalException> evalException = new MutableObject<CalculatorEvalException>(null);
			final MutableObject<Thread> calculationThread = new MutableObject<Thread>(null);

			final CountDownLatch latch = new CountDownLatch(1);

			new Thread(new Runnable() {
				@Override
				public void run() {
					final Thread thread = Thread.currentThread();
					try {
						//Log.d(CalculatorEngine.class.getName(), "Calculation thread started work: " + thread.getName());
						//System.out.println(jsclExpression);
						calculationThread.setObject(thread);
						final Generic genericResult = operation.evaluateGeneric(jsclExpression);

						// NOTE: toString() method must be called here as ArithmeticOperationException may occur in it (just to avoid later check!)
						genericResult.toString();

						calculationResult.setObject(genericResult);
					} catch (AbstractJsclArithmeticException e) {
						evalException.setObject(new CalculatorEvalException(e, e, jsclExpression));
					} catch (ArithmeticException e) {
						//System.out.println(e.getMessage());
						parseException.setObject(new CalculatorParseException(R.string.msg_1, CalculatorApplication.getInstance(), jsclExpression, e.getMessage()));
					} catch (StackOverflowError e) {
						//System.out.println(StringUtils.fromStackTrace(e.getStackTrace()));
						parseException.setObject(new CalculatorParseException(R.string.msg_2, CalculatorApplication.getInstance(), jsclExpression));
					} catch (jscl.text.ParseException e) {
						//System.out.println(e.getMessage());
						parseException.setObject(new CalculatorParseException(e));
					} catch (ParseInterruptedException e) {
						//System.out.println(e.getMessage());
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
				latch.await(timeout, TimeUnit.SECONDS);
				//Log.d(CalculatorEngine.class.getName(), "Main thread got up: " + Thread.currentThread().getName());

				final CalculatorParseException parseExceptionObject = parseException.getObject();
				final CalculatorEvalException evalExceptionObject = evalException.getObject();
				final Object calculationResultLocal = calculationResult.getObject();
				final Thread calculationThreadLocal = calculationThread.getObject();

				if (calculationThreadLocal != null) {
                    if (threadKiller != null) {
                        threadKiller.killThread(calculationThreadLocal);
                    }
                    //calculationThreadLocal.stop();
				}

				if (parseExceptionObject != null || evalExceptionObject != null) {
					if (operation == JsclOperation.numeric &&
							(preparedExpression.isExistsUndefinedVar() || (evalExceptionObject != null && evalExceptionObject.getCause() instanceof NumeralBaseException))) {
						return evaluate(JsclOperation.simplify, expression, mr);
					}

					if (parseExceptionObject != null) {
						throw parseExceptionObject;
					} else {
						throw evalExceptionObject;
					}
				}

				if (calculationResultLocal == null) {
					throw new CalculatorParseException(R.string.msg_3, CalculatorApplication.getInstance(), jsclExpression);
				}

			} catch (InterruptedException e) {
				throw new CalculatorParseException(R.string.msg_4, CalculatorApplication.getInstance(), jsclExpression);
			}

			final Generic genericResult = calculationResult.getObject();

			return new Result(operation.getFromProcessor().process(genericResult), operation, genericResult);
		}
	}

	public void setPrecision(int precision) {
		this.getEngine().setPrecision(precision);
	}

	public void setRoundResult(boolean roundResult) {
		this.getEngine().setRoundResult(roundResult);
	}

	public void init(@Nullable Context context, @Nullable SharedPreferences preferences) {
		synchronized (lock) {
			reset(context, preferences);
		}
	}

	public void reset(@Nullable Context context, @Nullable SharedPreferences preferences) {
		synchronized (lock) {
			softReset(context, preferences);

			varsRegistry.load(context, preferences);
			functionsRegistry.load(context, preferences);
			operatorsRegistry.load(context, preferences);
			postfixFunctionsRegistry.load(context, preferences);
		}
	}

	public void softReset(@Nullable Context context, @Nullable SharedPreferences preferences) {
		synchronized (lock) {
			if (preferences != null) {
				this.setPrecision(Preferences.precision.getPreference(preferences));
				this.setRoundResult(Preferences.roundResult.getPreference(preferences));
				this.setAngleUnits(getAngleUnitsFromPrefs(preferences));
				this.setNumeralBase(getNumeralBaseFromPrefs(preferences));
				this.setMultiplicationSign(Preferences.multiplicationSign.getPreference(preferences));
				this.setScienceNotation(Preferences.scienceNotation.getPreference(preferences));
				this.setTimeout(Preferences.maxCalculationTime.getPreference(preferences));

				final String groupingSeparator = Preferences.groupingSeparator.getPreference(preferences);
				if (StringUtils.isEmpty(groupingSeparator)) {
					this.getEngine().setUseGroupingSeparator(false);
				} else {
					this.getEngine().setUseGroupingSeparator(true);
					this.getEngine().setGroupingSeparator(groupingSeparator.charAt(0));
				}
			}
		}
	}


	@NotNull
	public NumeralBase getNumeralBaseFromPrefs(@NotNull SharedPreferences preferences) {
		return Preferences.numeralBase.getPreference(preferences);
	}

	@NotNull
	public AngleUnit getAngleUnitsFromPrefs(@NotNull SharedPreferences preferences) {
		return Preferences.angleUnit.getPreference(preferences);
	}

	//for tests only
	void setDecimalGroupSymbols(@NotNull DecimalFormatSymbols decimalGroupSymbols) {
		synchronized (lock) {
			this.getEngine().setDecimalGroupSymbols(decimalGroupSymbols);
		}
	}

	@NotNull
	public AndroidMathRegistry<IConstant> getVarsRegistry() {
		return varsRegistry;
	}

	@NotNull
	public AndroidMathRegistry<Function> getFunctionsRegistry() {
		return functionsRegistry;
	}

	@NotNull
	public AndroidMathRegistry<Operator> getOperatorsRegistry() {
		return operatorsRegistry;
	}

	@NotNull
	public AndroidMathRegistry<Operator> getPostfixFunctionsRegistry() {
		return postfixFunctionsRegistry;
	}

	@NotNull
	public MathEngine getEngine() {
		return engine;
	}

	// package protected for tests
	void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setAngleUnits(@NotNull AngleUnit angleUnits) {
		getEngine().setAngleUnits(angleUnits);
	}

	public void setScienceNotation(boolean scienceNotation) {
		getEngine().setScienceNotation(scienceNotation);
	}

	public void setNumeralBase(@NotNull NumeralBase numeralBase) {
		getEngine().setNumeralBase(numeralBase);
	}

	// for tests only
	void setThreadKiller(@Nullable ThreadKiller threadKiller) {
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
