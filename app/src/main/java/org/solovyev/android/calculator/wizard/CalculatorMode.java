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

package org.solovyev.android.calculator.wizard;

import android.content.SharedPreferences;
import jscl.AngleUnit;
import org.solovyev.android.calculator.Engine;
import org.solovyev.android.calculator.Preferences;

import javax.annotation.Nonnull;

import static org.solovyev.android.calculator.Preferences.Gui.Layout.main_calculator;

enum CalculatorMode {

    simple() {
        @Override
        protected void apply(@Nonnull SharedPreferences preferences) {
            final SharedPreferences.Editor editor = preferences.edit();

            Preferences.Gui.layout.putPreference(editor, Preferences.Gui.Layout.simple);
            Preferences.Calculations.preferredAngleUnits.putPreference(editor, AngleUnit.deg);
            Engine.Preferences.angleUnit.putPreference(editor, AngleUnit.deg);
            Engine.Preferences.Output.scientificNotation.putPreference(editor, false);
            Engine.Preferences.Output.round.putPreference(editor, true);

            editor.apply();
        }
    },

    engineer() {
        @Override
        protected void apply(@Nonnull SharedPreferences preferences) {
            final SharedPreferences.Editor editor = preferences.edit();

            Preferences.Gui.layout.putPreference(editor, main_calculator);
            Preferences.Calculations.preferredAngleUnits.putPreference(editor, AngleUnit.rad);
            Engine.Preferences.angleUnit.putPreference(editor, AngleUnit.rad);
            Engine.Preferences.Output.scientificNotation.putPreference(editor, true);
            Engine.Preferences.Output.round.putPreference(editor, false);

            editor.apply();
        }
    };

    @Nonnull
    static CalculatorMode getDefaultMode() {
        return engineer;
    }

    @Nonnull
    static CalculatorMode fromGuiLayout(@Nonnull Preferences.Gui.Layout layout) {
        switch (layout) {
            case main_calculator:
            case main_cellphone:
            case main_calculator_mobile:
                return engineer;
            case simple:
            case simple_mobile:
                return simple;
            default:
                return getDefaultMode();
        }
    }

    protected abstract void apply(@Nonnull SharedPreferences preferences);
}
