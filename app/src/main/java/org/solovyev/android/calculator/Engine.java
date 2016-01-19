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

package org.solovyev.android.calculator;

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
import org.solovyev.android.calculator.model.AndroidMathEntityDao;
import org.solovyev.android.calculator.model.Functions;
import org.solovyev.android.calculator.model.Vars;
import org.solovyev.android.prefs.BooleanPreference;
import org.solovyev.android.prefs.Preference;
import org.solovyev.android.prefs.StringPreference;
import org.solovyev.common.text.EnumMapper;
import org.solovyev.common.text.NumberMapper;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Engine implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String MULTIPLICATION_SIGN_DEFAULT = "×";

    private static final String GROUPING_SEPARATOR_P_KEY = "org.solovyev.android.calculator.CalculatorActivity_calc_grouping_separator";

    private static final String MULTIPLICATION_SIGN_P_KEY = "org.solovyev.android.calculator.CalculatorActivity_calc_multiplication_sign";

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
    @Nonnull
    private final Context context;
    @Nonnull
    private final Object lock;
    @Nonnull
    private final MathEngine mathEngine;
    @Nonnull
    private final EntitiesRegistry<IConstant> varsRegistry;
    @Nonnull
    private final EntitiesRegistry<Function> functionsRegistry;
    @Nonnull
    private final EntitiesRegistry<Operator> operatorsRegistry;
    @Nonnull
    private final EntitiesRegistry<Operator> postfixFunctionsRegistry;
    @Nonnull
    private String multiplicationSign = MULTIPLICATION_SIGN_DEFAULT;

    public Engine(@Nonnull Context context, @Nonnull MathEngine mathEngine, @Nonnull EntitiesRegistry<IConstant> varsRegistry, @Nonnull EntitiesRegistry<Function> functionsRegistry, @Nonnull EntitiesRegistry<Operator> operatorsRegistry, @Nonnull EntitiesRegistry<Operator> postfixFunctionsRegistry) {
        this.context = context;
        this.lock = new Object();
        this.mathEngine = mathEngine;
        this.varsRegistry = varsRegistry;
        this.functionsRegistry = functionsRegistry;
        this.operatorsRegistry = operatorsRegistry;
        this.postfixFunctionsRegistry = postfixFunctionsRegistry;
    }

    public Engine(@Nonnull Application application) {
        this.mathEngine = JsclMathEngine.getInstance();
        this.context = application;

        PreferenceManager.getDefaultSharedPreferences(application).registerOnSharedPreferenceChangeListener(this);

        this.lock = new Object();
        this.mathEngine.setRoundResult(true);
        this.mathEngine.setUseGroupingSeparator(true);

        this.varsRegistry = new VarsRegistry(mathEngine.getConstantsRegistry(), new AndroidMathEntityDao<>("org.solovyev.android.calculator.CalculatorModel_vars", application, Vars.class));
        this.functionsRegistry = new FunctionsRegistry(mathEngine.getFunctionsRegistry(), new AndroidMathEntityDao<>("org.solovyev.android.calculator.CalculatorModel_functions", application, Functions.class));
        this.operatorsRegistry = new OperatorsRegistry(mathEngine.getOperatorsRegistry(), new AndroidMathEntityDao<>(null, application, null));
        this.postfixFunctionsRegistry = new PostfixFunctionsRegistry(mathEngine.getPostfixFunctionsRegistry(), new AndroidMathEntityDao<>(null, application, null));
    }

    @Nonnull
    public static NumeralBase getNumeralBaseFromPrefs(@Nonnull SharedPreferences preferences) {
        return Preferences.numeralBase.getPreference(preferences);
    }

    @Nonnull
    public static AngleUnit getAngleUnitsFromPrefs(@Nonnull SharedPreferences preferences) {
        return Preferences.angleUnit.getPreference(preferences);
    }

    @Nonnull
    public EntitiesRegistry<IConstant> getVarsRegistry() {
        return this.varsRegistry;
    }

    @Nonnull
    public EntitiesRegistry<Function> getFunctionsRegistry() {
        return this.functionsRegistry;
    }

    @Nonnull
    public EntitiesRegistry<Operator> getOperatorsRegistry() {
        return this.operatorsRegistry;
    }

    @Nonnull
    public EntitiesRegistry<Operator> getPostfixFunctionsRegistry() {
        return this.postfixFunctionsRegistry;
    }

    @Nonnull
    public MathEngine getMathEngine() {
        return mathEngine;
    }

    public void init() {
        synchronized (lock) {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            resetPreferences(preferences);
            safeLoadRegistry(varsRegistry);
            safeLoadRegistry(functionsRegistry);
            safeLoadRegistry(operatorsRegistry);
            safeLoadRegistry(postfixFunctionsRegistry);
        }
    }

    public void resetPreferences() {
        resetPreferences(App.getPreferences());
    }

    private void safeLoadRegistry(@Nonnull EntitiesRegistry<?> registry) {
        try {
            registry.load();
        } catch (Exception e) {
            Locator.getInstance().getErrorReporter().onException(e);
        }
    }

    private void resetPreferences(@Nonnull SharedPreferences preferences) {
        setPrecision(Preferences.precision.getPreference(preferences));
        setRoundResult(Preferences.roundResult.getPreference(preferences));
        setAngleUnits(getAngleUnitsFromPrefs(preferences));
        setNumeralBase(getNumeralBaseFromPrefs(preferences));
        setMultiplicationSign(Preferences.multiplicationSign.getPreference(preferences));
        setScienceNotation(Preferences.scienceNotation.getPreference(preferences));

        final String groupingSeparator = Preferences.groupingSeparator.getPreference(preferences);
        if (Strings.isEmpty(groupingSeparator)) {
            setUseGroupingSeparator(false);
        } else {
            setUseGroupingSeparator(true);
            setGroupingSeparator(groupingSeparator.charAt(0));
        }
        Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.engine_preferences_changed, null);
    }

    @Nonnull
    public String getMultiplicationSign() {
        return this.multiplicationSign;
    }

    public void setMultiplicationSign(@Nonnull String multiplicationSign) {
        this.multiplicationSign = multiplicationSign;
    }

    public void setUseGroupingSeparator(boolean useGroupingSeparator) {
        synchronized (lock) {
            this.mathEngine.setUseGroupingSeparator(useGroupingSeparator);
        }
    }

    public void setGroupingSeparator(char groupingSeparator) {
        synchronized (lock) {
            this.mathEngine.setGroupingSeparator(groupingSeparator);
        }
    }

    public void setPrecision(@Nonnull Integer precision) {
        synchronized (lock) {
            this.mathEngine.setPrecision(precision);
        }
    }

    public void setRoundResult(@Nonnull Boolean round) {
        synchronized (lock) {
            this.mathEngine.setRoundResult(round);
        }
    }

    @Nonnull
    public AngleUnit getAngleUnits() {
        synchronized (lock) {
            return this.mathEngine.getAngleUnits();
        }
    }

    public void setAngleUnits(@Nonnull AngleUnit angleUnits) {
        synchronized (lock) {
            this.mathEngine.setAngleUnits(angleUnits);
        }
    }

    @Nonnull
    public NumeralBase getNumeralBase() {
        synchronized (lock) {
            return this.mathEngine.getNumeralBase();
        }
    }

    public void setNumeralBase(@Nonnull NumeralBase numeralBase) {
        synchronized (lock) {
            this.mathEngine.setNumeralBase(numeralBase);
        }
    }

    public void setScienceNotation(@Nonnull Boolean scienceNotation) {
        synchronized (lock) {
            this.mathEngine.setScienceNotation(scienceNotation);
        }
    }

    public void setDecimalGroupSymbols(@Nonnull DecimalFormatSymbols decimalGroupSymbols) {
        synchronized (lock) {
            this.mathEngine.setDecimalGroupSymbols(decimalGroupSymbols);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Preferences.getPreferenceKeys().contains(key)) {
            this.resetPreferences();
        }
    }

    public static class Preferences {
        public static final Preference<String> groupingSeparator = StringPreference.of(GROUPING_SEPARATOR_P_KEY, JsclMathEngine.GROUPING_SEPARATOR_DEFAULT);
        public static final Preference<String> multiplicationSign = StringPreference.of(MULTIPLICATION_SIGN_P_KEY, MULTIPLICATION_SIGN_DEFAULT);
        public static final Preference<Integer> precision = StringPreference.ofTypedValue(RESULT_PRECISION_P_KEY, RESULT_PRECISION_DEFAULT, NumberMapper.of(Integer.class));
        public static final Preference<Boolean> roundResult = BooleanPreference.of(ROUND_RESULT_P_KEY, ROUND_RESULT_DEFAULT);
        public static final Preference<NumeralBase> numeralBase = StringPreference.ofTypedValue(NUMERAL_BASES_P_KEY, NUMERAL_BASES_DEFAULT, EnumMapper.of(NumeralBase.class));
        public static final Preference<AngleUnit> angleUnit = StringPreference.ofTypedValue(ANGLE_UNITS_P_KEY, ANGLE_UNITS_DEFAULT, EnumMapper.of(AngleUnit.class));
        public static final Preference<Boolean> scienceNotation = BooleanPreference.of(SCIENCE_NOTATION_P_KEY, SCIENCE_NOTATION_DEFAULT);

        private static final List<String> preferenceKeys = new ArrayList<>();

        static {
            preferenceKeys.add(groupingSeparator.getKey());
            preferenceKeys.add(multiplicationSign.getKey());
            preferenceKeys.add(precision.getKey());
            preferenceKeys.add(roundResult.getKey());
            preferenceKeys.add(numeralBase.getKey());
            preferenceKeys.add(angleUnit.getKey());
            preferenceKeys.add(scienceNotation.getKey());
        }

        @Nonnull
        public static List<String> getPreferenceKeys() {
            return Collections.unmodifiableList(preferenceKeys);
        }
    }
}
