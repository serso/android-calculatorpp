/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
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
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.CalculatorEngine;
import org.solovyev.android.calculator.CalculatorEngineImpl;
import org.solovyev.android.calculator.CalculatorMathEngine;
import org.solovyev.android.calculator.CalculatorMathRegistry;
import org.solovyev.android.prefs.BooleanPreference;
import org.solovyev.android.prefs.Preference;
import org.solovyev.android.prefs.StringPreference;
import org.solovyev.common.text.EnumMapper;
import org.solovyev.common.text.NumberMapper;
import org.solovyev.common.text.StringUtils;

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
    private final Context context;

    @NotNull
    private final CalculatorEngine calculatorEngine;

    @NotNull
    private final Object lock;

    public AndroidCalculatorEngine(@NotNull Application application) {
        this.context = application;

        PreferenceManager.getDefaultSharedPreferences(application).registerOnSharedPreferenceChangeListener(this);

        this.lock = new Object();

        final JsclMathEngine engine = JsclMathEngine.instance;
        this.calculatorEngine = new CalculatorEngineImpl(engine,
                new AndroidVarsRegistryImpl(engine.getConstantsRegistry(), application),
                new AndroidFunctionsMathRegistry(engine.getFunctionsRegistry(), application),
                new AndroidOperatorsMathRegistry(engine.getOperatorsRegistry(), application),
                new AndroidPostfixFunctionsRegistry(engine.getPostfixFunctionsRegistry(), application),
                this.lock);
    }

    @Override
    @NotNull
    public CalculatorMathRegistry<IConstant> getVarsRegistry() {
        return calculatorEngine.getVarsRegistry();
    }

    @Override
    @NotNull
    public CalculatorMathRegistry<Function> getFunctionsRegistry() {
        return calculatorEngine.getFunctionsRegistry();
    }

    @Override
    @NotNull
    public CalculatorMathRegistry<Operator> getOperatorsRegistry() {
        return calculatorEngine.getOperatorsRegistry();
    }

    @Override
    @NotNull
    public CalculatorMathRegistry<Operator> getPostfixFunctionsRegistry() {
        return calculatorEngine.getPostfixFunctionsRegistry();
    }

    @Override
    @NotNull
    public CalculatorMathEngine getMathEngine() {
        return calculatorEngine.getMathEngine();
    }

    @NotNull
    @Override
    public MathEngine getMathEngine0() {
        return calculatorEngine.getMathEngine0();
    }

    @NotNull
    @Override
    public NumeralBase getNumeralBase() {
        return calculatorEngine.getNumeralBase();
    }

    @Override
    public void init() {
        synchronized (lock) {
            calculatorEngine.init();
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
    public void setPrecision(@NotNull Integer precision) {
        calculatorEngine.setPrecision(precision);
    }

    @Override
    public void setRoundResult(@NotNull Boolean round) {
        calculatorEngine.setRoundResult(round);
    }

    @NotNull
    @Override
    public AngleUnit getAngleUnits() {
        return calculatorEngine.getAngleUnits();
    }

    @Override
    public void setAngleUnits(@NotNull AngleUnit angleUnits) {
        calculatorEngine.setAngleUnits(angleUnits);
    }

    @Override
    public void setNumeralBase(@NotNull NumeralBase numeralBase) {
        calculatorEngine.setNumeralBase(numeralBase);
    }

    @Override
    public void setMultiplicationSign(@NotNull String multiplicationSign) {
        calculatorEngine.setMultiplicationSign(multiplicationSign);
    }

    @Override
    public void setScienceNotation(@NotNull Boolean scienceNotation) {
        calculatorEngine.setScienceNotation(scienceNotation);
    }

    @Override
    public void setTimeout(@NotNull Integer timeout) {
        calculatorEngine.setTimeout(timeout);
    }

    private void softReset(@NotNull SharedPreferences preferences) {
        this.setPrecision(Preferences.precision.getPreference(preferences));
        this.setRoundResult(Preferences.roundResult.getPreference(preferences));
        this.setAngleUnits(getAngleUnitsFromPrefs(preferences));
        this.setNumeralBase(getNumeralBaseFromPrefs(preferences));
        this.setMultiplicationSign(Preferences.multiplicationSign.getPreference(preferences));
        this.setScienceNotation(Preferences.scienceNotation.getPreference(preferences));
        this.setTimeout(Preferences.maxCalculationTime.getPreference(preferences));

        final String groupingSeparator = Preferences.groupingSeparator.getPreference(preferences);
        if (StringUtils.isEmpty(groupingSeparator)) {
            this.setUseGroupingSeparator(false);
        } else {
            this.setUseGroupingSeparator(true);
            setGroupingSeparator(groupingSeparator.charAt(0));
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
    public void setDecimalGroupSymbols(@NotNull DecimalFormatSymbols decimalGroupSymbols) {
        this.calculatorEngine.setDecimalGroupSymbols(decimalGroupSymbols);
    }

    @Override
    @NotNull
    public String getMultiplicationSign() {
        return calculatorEngine.getMultiplicationSign();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ( Preferences.getPreferenceKeys().contains(key) ) {
            this.softReset();
        }
    }

}
