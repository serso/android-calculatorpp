/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.MathEngine;
import jscl.NumeralBase;
import jscl.math.function.Function;
import jscl.math.function.IConstant;
import jscl.math.operator.Operator;

import javax.annotation.Nonnull;

import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.R;
import org.solovyev.android.prefs.BooleanPreference;
import org.solovyev.android.prefs.Preference;
import org.solovyev.android.prefs.StringPreference;
import org.solovyev.common.text.EnumMapper;
import org.solovyev.common.text.NumberMapper;
import org.solovyev.common.text.Strings;

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: serso
 * Date: 9/12/11
 * Time: 11:38 PM
 */

public class AndroidCalculatorEngine implements CalculatorEngine, SharedPreferences.OnSharedPreferenceChangeListener {

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
		public static final Preference<String> groupingSeparator = StringPreference.of(GROUPING_SEPARATOR_P_KEY, JsclMathEngine.GROUPING_SEPARATOR_DEFAULT);
		public static final Preference<String> multiplicationSign = StringPreference.of(MULTIPLICATION_SIGN_P_KEY, MULTIPLICATION_SIGN_DEFAULT);
		public static final Preference<Integer> precision = StringPreference.ofTypedValue(RESULT_PRECISION_P_KEY, RESULT_PRECISION_DEFAULT, NumberMapper.of(Integer.class));
		public static final Preference<Boolean> roundResult = BooleanPreference.of(ROUND_RESULT_P_KEY, ROUND_RESULT_DEFAULT);
		public static final Preference<NumeralBase> numeralBase = StringPreference.ofTypedValue(NUMERAL_BASES_P_KEY, NUMERAL_BASES_DEFAULT, EnumMapper.of(NumeralBase.class));
		public static final Preference<AngleUnit> angleUnit = StringPreference.ofTypedValue(ANGLE_UNITS_P_KEY, ANGLE_UNITS_DEFAULT, EnumMapper.of(AngleUnit.class));
		public static final Preference<Boolean> scienceNotation = BooleanPreference.of(SCIENCE_NOTATION_P_KEY, SCIENCE_NOTATION_DEFAULT);
		public static final Preference<Integer> maxCalculationTime = StringPreference.ofTypedValue(MAX_CALCULATION_TIME_P_KEY, MAX_CALCULATION_TIME_DEFAULT, NumberMapper.of(Integer.class));

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

		@Nonnull
		public static List<String> getPreferenceKeys() {
			return Collections.unmodifiableList(preferenceKeys);
		}
	}

	@Nonnull
	private final Context context;

	@Nonnull
	private final CalculatorEngine calculatorEngine;

	@Nonnull
	private final Object lock;

	public AndroidCalculatorEngine(@Nonnull Application application) {
		this.context = application;

		PreferenceManager.getDefaultSharedPreferences(application).registerOnSharedPreferenceChangeListener(this);

		this.lock = new Object();

		final JsclMathEngine engine = JsclMathEngine.getInstance();
		this.calculatorEngine = new CalculatorEngineImpl(engine,
				new CalculatorVarsRegistry(engine.getConstantsRegistry(), new AndroidMathEntityDao<Var>("org.solovyev.android.calculator.CalculatorModel_vars", application, Vars.class)),
				new CalculatorFunctionsMathRegistry(engine.getFunctionsRegistry(), new AndroidMathEntityDao<AFunction>("org.solovyev.android.calculator.CalculatorModel_functions", application, Functions.class)),
				new CalculatorOperatorsMathRegistry(engine.getOperatorsRegistry(), new AndroidMathEntityDao<MathPersistenceEntity>(null, application, null)),
				new CalculatorPostfixFunctionsRegistry(engine.getPostfixFunctionsRegistry(), new AndroidMathEntityDao<MathPersistenceEntity>(null, application, null)),
				this.lock);
	}

	@Override
	@Nonnull
	public CalculatorMathRegistry<IConstant> getVarsRegistry() {
		return calculatorEngine.getVarsRegistry();
	}

	@Override
	@Nonnull
	public CalculatorMathRegistry<Function> getFunctionsRegistry() {
		return calculatorEngine.getFunctionsRegistry();
	}

	@Override
	@Nonnull
	public CalculatorMathRegistry<Operator> getOperatorsRegistry() {
		return calculatorEngine.getOperatorsRegistry();
	}

	@Override
	@Nonnull
	public CalculatorMathRegistry<Operator> getPostfixFunctionsRegistry() {
		return calculatorEngine.getPostfixFunctionsRegistry();
	}

	@Override
	@Nonnull
	public CalculatorMathEngine getMathEngine() {
		return calculatorEngine.getMathEngine();
	}

	@Nonnull
	@Override
	public MathEngine getMathEngine0() {
		return calculatorEngine.getMathEngine0();
	}

	@Nonnull
	@Override
	public NumeralBase getNumeralBase() {
		return calculatorEngine.getNumeralBase();
	}

	@Override
	public void init() {
		synchronized (lock) {
			reset();
		}
	}

	@Override
	public void reset() {
		synchronized (lock) {
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

			softReset(preferences);

			calculatorEngine.reset();
		}
	}

	@Override
	public void softReset() {
		synchronized (lock) {
			softReset(PreferenceManager.getDefaultSharedPreferences(context));

			calculatorEngine.softReset();
		}
	}

	@Override
	public void setUseGroupingSeparator(boolean useGroupingSeparator) {
		calculatorEngine.setUseGroupingSeparator(useGroupingSeparator);
	}

	@Override
	public void setGroupingSeparator(char groupingSeparator) {
		calculatorEngine.setGroupingSeparator(groupingSeparator);
	}

	@Override
	public void setPrecision(@Nonnull Integer precision) {
		calculatorEngine.setPrecision(precision);
	}

	@Override
	public void setRoundResult(@Nonnull Boolean round) {
		calculatorEngine.setRoundResult(round);
	}

	@Nonnull
	@Override
	public AngleUnit getAngleUnits() {
		return calculatorEngine.getAngleUnits();
	}

	@Override
	public void setAngleUnits(@Nonnull AngleUnit angleUnits) {
		calculatorEngine.setAngleUnits(angleUnits);
	}

	@Override
	public void setNumeralBase(@Nonnull NumeralBase numeralBase) {
		calculatorEngine.setNumeralBase(numeralBase);
	}

	@Override
	public void setMultiplicationSign(@Nonnull String multiplicationSign) {
		calculatorEngine.setMultiplicationSign(multiplicationSign);
	}

	@Override
	public void setScienceNotation(@Nonnull Boolean scienceNotation) {
		calculatorEngine.setScienceNotation(scienceNotation);
	}

	@Override
	public void setTimeout(@Nonnull Integer timeout) {
		calculatorEngine.setTimeout(timeout);
	}

	private void softReset(@Nonnull SharedPreferences preferences) {
		this.setPrecision(Preferences.precision.getPreference(preferences));
		this.setRoundResult(Preferences.roundResult.getPreference(preferences));
		this.setAngleUnits(getAngleUnitsFromPrefs(preferences));
		this.setNumeralBase(getNumeralBaseFromPrefs(preferences));
		this.setMultiplicationSign(Preferences.multiplicationSign.getPreference(preferences));
		this.setScienceNotation(Preferences.scienceNotation.getPreference(preferences));
		this.setTimeout(Preferences.maxCalculationTime.getPreference(preferences));

		final String groupingSeparator = Preferences.groupingSeparator.getPreference(preferences);
		if (Strings.isEmpty(groupingSeparator)) {
			this.setUseGroupingSeparator(false);
		} else {
			this.setUseGroupingSeparator(true);
			setGroupingSeparator(groupingSeparator.charAt(0));
		}
	}

	@Nonnull
	public static NumeralBase getNumeralBaseFromPrefs(@Nonnull SharedPreferences preferences) {
		return Preferences.numeralBase.getPreference(preferences);
	}

	@Nonnull
	public static AngleUnit getAngleUnitsFromPrefs(@Nonnull SharedPreferences preferences) {
		return Preferences.angleUnit.getPreference(preferences);
	}

	//for tests only
	public void setDecimalGroupSymbols(@Nonnull DecimalFormatSymbols decimalGroupSymbols) {
		this.calculatorEngine.setDecimalGroupSymbols(decimalGroupSymbols);
	}

	@Override
	@Nonnull
	public String getMultiplicationSign() {
		return calculatorEngine.getMultiplicationSign();
	}


	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (Preferences.getPreferenceKeys().contains(key)) {
			this.softReset();
		}
	}

}
