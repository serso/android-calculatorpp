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

import android.content.SharedPreferences;
import com.squareup.otto.Bus;
import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.MathEngine;
import jscl.NumeralBase;
import jscl.math.operator.Operator;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.model.EntityDao;
import org.solovyev.android.calculator.model.Vars;
import org.solovyev.android.prefs.BooleanPreference;
import org.solovyev.android.prefs.IntegerPreference;
import org.solovyev.android.prefs.Preference;
import org.solovyev.android.prefs.StringPreference;
import org.solovyev.common.text.EnumMapper;
import org.solovyev.common.text.NumberMapper;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

@Singleton
public class Engine implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Nonnull
    private final MathEngine mathEngine;
    @Nonnull
    private final VariablesRegistry variablesRegistry;

    @Nonnull
    private final EntitiesRegistry<Operator> operatorsRegistry;
    @Nonnull
    private final EntitiesRegistry<Operator> postfixFunctionsRegistry;
    @Inject
    SharedPreferences preferences;
    @Inject
    Bus bus;
    @Inject
    ErrorReporter errorReporter;
    @Inject
    FunctionsRegistry functionsRegistry;
    @Nonnull
    private String multiplicationSign = Preferences.multiplicationSign.getDefaultValue();

    public Engine(@Nonnull MathEngine mathEngine, @Nonnull VariablesRegistry variablesRegistry, @Nonnull FunctionsRegistry functionsRegistry, @Nonnull EntitiesRegistry<Operator> operatorsRegistry, @Nonnull EntitiesRegistry<Operator> postfixFunctionsRegistry) {
        this.mathEngine = mathEngine;
        this.variablesRegistry = variablesRegistry;
        this.functionsRegistry = functionsRegistry;
        this.operatorsRegistry = operatorsRegistry;
        this.postfixFunctionsRegistry = postfixFunctionsRegistry;
    }

    @Inject
    public Engine(@Nonnull SharedPreferences preferences, @Nonnull JsclMathEngine mathEngine) {
        this.mathEngine = mathEngine;

        this.mathEngine.setRoundResult(true);
        this.mathEngine.setUseGroupingSeparator(true);

        this.variablesRegistry = new VariablesRegistry(mathEngine.getConstantsRegistry(), new EntityDao<>("org.solovyev.android.calculator.CalculatorModel_vars", Vars.class, preferences));
        this.operatorsRegistry = new OperatorsRegistry(mathEngine.getOperatorsRegistry());
        this.postfixFunctionsRegistry = new PostfixFunctionsRegistry(mathEngine.getPostfixFunctionsRegistry());
    }

    private static void migratePreference(@Nonnull SharedPreferences preferences, @Nonnull BooleanPreference preference, @Nonnull String oldKey, @Nonnull SharedPreferences.Editor editor) {
        if (!preferences.contains(oldKey)) {
            return;
        }
        editor.putBoolean(preference.getKey(), preferences.getBoolean(oldKey, false));
    }

    private static void migratePreference(@Nonnull SharedPreferences preferences, @Nonnull StringPreference<?> preference, @Nonnull String oldKey, @Nonnull SharedPreferences.Editor editor) {
        if (!preferences.contains(oldKey)) {
            return;
        }
        editor.putString(preference.getKey(), preferences.getString(oldKey, null));
    }

    @Nonnull
    public VariablesRegistry getVariablesRegistry() {
        return variablesRegistry;
    }

    @Nonnull
    public FunctionsRegistry getFunctionsRegistry() {
        return functionsRegistry;
    }

    @Nonnull
    public EntitiesRegistry<Operator> getOperatorsRegistry() {
        return operatorsRegistry;
    }

    @Nonnull
    public EntitiesRegistry<Operator> getPostfixFunctionsRegistry() {
        return postfixFunctionsRegistry;
    }

    @Nonnull
    public MathEngine getMathEngine() {
        return mathEngine;
    }

    public void init(@Nonnull Executor initThread) {
        Check.isMainThread();
        checkPreferences();
        preferences.registerOnSharedPreferenceChangeListener(this);
        applyPreferences();
        initThread.execute(new Runnable() {
            @Override
            public void run() {
                initAsync();
            }
        });
    }

    private void checkPreferences() {
        final int oldVersion;
        if (Preferences.version.isSet(preferences)) {
            oldVersion = Preferences.version.getPreference(preferences);
        } else {
            oldVersion = 0;
        }
        final int newVersion = Preferences.version.getPreference(preferences);
        if (oldVersion == newVersion) {
            return;
        }
        final SharedPreferences.Editor editor = preferences.edit();
        if (oldVersion == 0) {
            migratePreference(preferences, Preferences.groupingSeparator, "org.solovyev.android.calculator.CalculatorActivity_calc_grouping_separator", editor);
            migratePreference(preferences, Preferences.multiplicationSign, "org.solovyev.android.calculator.CalculatorActivity_calc_multiplication_sign", editor);
            migratePreference(preferences, Preferences.numeralBase, "org.solovyev.android.calculator.CalculatorActivity_numeral_bases", editor);
            migratePreference(preferences, Preferences.angleUnit, "org.solovyev.android.calculator.CalculatorActivity_angle_units", editor);
            migratePreference(preferences, Preferences.Output.precision, "org.solovyev.android.calculator.CalculatorModel_result_precision", editor);
            migratePreference(preferences, Preferences.Output.scientificNotation, "calculation.output.science_notation", editor);
            migratePreference(preferences, Preferences.Output.round, "org.solovyev.android.calculator.CalculatorModel_round_result", editor);
        }
        Preferences.version.putDefault(preferences);
        editor.apply();
    }

    private void initAsync() {
        init(variablesRegistry);
        init(functionsRegistry);
        init(operatorsRegistry);
        init(postfixFunctionsRegistry);
    }

    private void init(@Nonnull EntitiesRegistry<?> registry) {
        try {
            registry.init();
        } catch (Exception e) {
            errorReporter.onException(e);
        }
    }

    private void applyPreferences() {
        Check.isMainThread();
        mathEngine.setAngleUnits(Preferences.angleUnit.getPreference(preferences));
        mathEngine.setNumeralBase(Preferences.numeralBase.getPreference(preferences));
        setMultiplicationSign(Preferences.multiplicationSign.getPreference(preferences));

        mathEngine.setPrecision(Preferences.Output.precision.getPreference(preferences));
        mathEngine.setScienceNotation(Preferences.Output.scientificNotation.getPreference(preferences));
        mathEngine.setRoundResult(Preferences.Output.round.getPreference(preferences));

        final String groupingSeparator = Preferences.groupingSeparator.getPreference(preferences);
        if (Strings.isEmpty(groupingSeparator)) {
            mathEngine.setUseGroupingSeparator(false);
        } else {
            mathEngine.setUseGroupingSeparator(true);
            mathEngine.setGroupingSeparator(groupingSeparator.charAt(0));
        }
        bus.post(ChangedEvent.INSTANCE);
    }

    @Nonnull
    public String getMultiplicationSign() {
        return this.multiplicationSign;
    }

    public void setMultiplicationSign(@Nonnull String multiplicationSign) {
        this.multiplicationSign = multiplicationSign;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Preferences.getPreferenceKeys().contains(key)) {
            applyPreferences();
        }
    }

    public static class ChangedEvent {
        static final ChangedEvent INSTANCE = new ChangedEvent();
        private ChangedEvent() {
        }
    }

    public static class Preferences {
        public static final StringPreference<String> groupingSeparator = StringPreference.of("engine.groupingSeparator", JsclMathEngine.GROUPING_SEPARATOR_DEFAULT);
        public static final StringPreference<String> multiplicationSign = StringPreference.of("engine.multiplicationSign", "×");
        public static final StringPreference<NumeralBase> numeralBase = StringPreference.ofTypedValue("engine.numeralBase", "dec", EnumMapper.of(NumeralBase.class));
        public static final StringPreference<AngleUnit> angleUnit = StringPreference.ofTypedValue("engine.angleUnit", "deg", EnumMapper.of(AngleUnit.class));
        public static final Preference<Integer> version = IntegerPreference.of("engine.version", 1);
        private static final List<String> preferenceKeys = new ArrayList<>();

        static {
            preferenceKeys.add(groupingSeparator.getKey());
            preferenceKeys.add(multiplicationSign.getKey());
            preferenceKeys.add(numeralBase.getKey());
            preferenceKeys.add(angleUnit.getKey());
            preferenceKeys.add(Output.precision.getKey());
            preferenceKeys.add(Output.scientificNotation.getKey());
            preferenceKeys.add(Output.round.getKey());
        }

        @Nonnull
        public static List<String> getPreferenceKeys() {
            return Collections.unmodifiableList(preferenceKeys);
        }

        public static class Output {
            public static final StringPreference<Integer> precision = StringPreference.ofTypedValue("engine.output.precision", "5", NumberMapper.of(Integer.class));
            public static final BooleanPreference scientificNotation = BooleanPreference.of("engine.output.scientificNotation", false);
            public static final BooleanPreference round = BooleanPreference.of("engine.output.round", true);
        }
    }
}
