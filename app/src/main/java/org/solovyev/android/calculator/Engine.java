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
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.squareup.otto.Bus;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.functions.FunctionsRegistry;
import org.solovyev.android.calculator.operators.OperatorsRegistry;
import org.solovyev.android.calculator.operators.PostfixFunctionsRegistry;
import org.solovyev.android.prefs.BooleanPreference;
import org.solovyev.android.prefs.CharacterPreference;
import org.solovyev.android.prefs.IntegerPreference;
import org.solovyev.android.prefs.Preference;
import org.solovyev.android.prefs.StringPreference;
import org.solovyev.common.text.EnumMapper;
import org.solovyev.common.text.NumberMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.MathEngine;
import jscl.NumeralBase;
import jscl.math.operator.Operator;
import jscl.text.Identifier;
import jscl.text.Parser;
import midpcalc.Real;

@Singleton
public class Engine implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Nonnull
    private final MathEngine mathEngine;
    @Inject
    SharedPreferences preferences;
    @Inject
    Bus bus;
    @Inject
    ErrorReporter errorReporter;
    @Inject
    FunctionsRegistry functionsRegistry;
    @Inject
    VariablesRegistry variablesRegistry;
    @Inject
    OperatorsRegistry operatorsRegistry;
    @Inject
    PostfixFunctionsRegistry postfixFunctionsRegistry;
    @Nonnull
    private String multiplicationSign = Preferences.multiplicationSign.getDefaultValue();

    public Engine(@Nonnull MathEngine mathEngine, @Nonnull VariablesRegistry variablesRegistry, @Nonnull FunctionsRegistry functionsRegistry, @Nonnull OperatorsRegistry operatorsRegistry, @Nonnull PostfixFunctionsRegistry postfixFunctionsRegistry) {
        this.mathEngine = mathEngine;
        this.variablesRegistry = variablesRegistry;
        this.functionsRegistry = functionsRegistry;
        this.operatorsRegistry = operatorsRegistry;
        this.postfixFunctionsRegistry = postfixFunctionsRegistry;
    }

    @Inject
    public Engine(@Nonnull JsclMathEngine mathEngine) {
        this.mathEngine = mathEngine;

        this.mathEngine.setRoundResult(true);
        this.mathEngine.setGroupingSeparator(JsclMathEngine.GROUPING_SEPARATOR_DEFAULT);
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

    private static void migratePreference(@Nonnull SharedPreferences preferences, @Nonnull CharacterPreference preference, @Nonnull String oldKey, @Nonnull SharedPreferences.Editor editor) {
        if (!preferences.contains(oldKey)) {
            return;
        }
        final String s = preferences.getString(oldKey, null);
        editor.putInt(preference.getKey(), TextUtils.isEmpty(s) ? 0 : s.charAt(0));
    }


    public static boolean isValidName(@Nullable String name) {
        if (!TextUtils.isEmpty(name)) {
            try {
                Identifier.parser.parse(Parser.Parameters.get(name), null);
                return true;
            } catch (jscl.text.ParseException e) {
                // not valid name;
            }
        }

        return false;
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
        final int newVersion = Preferences.version.getDefaultValue();
        if (oldVersion == newVersion) {
            return;
        }
        final SharedPreferences.Editor editor = preferences.edit();
        if (oldVersion == 0) {
            migratePreference(preferences, Preferences.Output.separator, "org.solovyev.android.calculator.CalculatorActivity_calc_grouping_separator", editor);
            migratePreference(preferences, Preferences.multiplicationSign, "org.solovyev.android.calculator.CalculatorActivity_calc_multiplication_sign", editor);
            migratePreference(preferences, Preferences.numeralBase, "org.solovyev.android.calculator.CalculatorActivity_numeral_bases", editor);
            migratePreference(preferences, Preferences.angleUnit, "org.solovyev.android.calculator.CalculatorActivity_angle_units", editor);
            migratePreference(preferences, Preferences.Output.precision, "org.solovyev.android.calculator.CalculatorModel_result_precision", editor);
            migratePreference(preferences, Preferences.Output.scientificNotation, "calculation.output.science_notation", editor);
            migratePreference(preferences, Preferences.Output.round, "org.solovyev.android.calculator.CalculatorModel_round_result", editor);
        } else if (oldVersion == 1) {
            migratePreference(preferences, Preferences.Output.separator, "engine.groupingSeparator", editor);
        }
        Preferences.version.putDefault(editor);
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
        mathEngine.setGroupingSeparator(Preferences.Output.separator.getPreference(preferences));

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

    public enum Notation {
        dec(Real.NumberFormat.FSE_NONE, R.string.cpp_number_format_dec),
        eng(Real.NumberFormat.FSE_ENG, R.string.cpp_number_format_eng),
        sci(Real.NumberFormat.FSE_SCI, R.string.cpp_number_format_sci);

        public final int id;
        @StringRes
        public final int name;

        Notation(int id, @StringRes int name) {
            this.id = id;
            this.name = name;
        }
    }

    public static class ChangedEvent {
        static final ChangedEvent INSTANCE = new ChangedEvent();

        private ChangedEvent() {
        }
    }

    public static class Preferences {
        public static final StringPreference<String> multiplicationSign = StringPreference.of("engine.multiplicationSign", "×");
        public static final StringPreference<NumeralBase> numeralBase = StringPreference.ofTypedValue("engine.numeralBase", "dec", EnumMapper.of(NumeralBase.class));
        public static final StringPreference<AngleUnit> angleUnit = StringPreference.ofTypedValue("engine.angleUnit", "deg", EnumMapper.of(AngleUnit.class));
        public static final Preference<Integer> version = IntegerPreference.of("engine.version", 2);
        private static final List<String> preferenceKeys = new ArrayList<>();

        static {
            preferenceKeys.add(multiplicationSign.getKey());
            preferenceKeys.add(numeralBase.getKey());
            preferenceKeys.add(angleUnit.getKey());
            preferenceKeys.add(Output.precision.getKey());
            preferenceKeys.add(Output.scientificNotation.getKey());
            preferenceKeys.add(Output.round.getKey());
            preferenceKeys.add(Output.notation.getKey());
            preferenceKeys.add(Output.separator.getKey());
        }

        @Nonnull
        public static List<String> getPreferenceKeys() {
            return Collections.unmodifiableList(preferenceKeys);
        }

        public static class Output {
            public static final StringPreference<Integer> precision = StringPreference.ofTypedValue("engine.output.precision", "5", NumberMapper.of(Integer.class));
            // todo serso: remove
            public static final BooleanPreference scientificNotation = BooleanPreference.of("engine.output.scientificNotation", false);
            public static final BooleanPreference round = BooleanPreference.of("engine.output.round", true);
            public static final StringPreference<Notation> notation = StringPreference.ofEnum("engine.output.notation", Notation.dec, Notation.class);
            public static final CharacterPreference separator = CharacterPreference.of("engine.output.separator", JsclMathEngine.GROUPING_SEPARATOR_DEFAULT);

        }
    }
}
