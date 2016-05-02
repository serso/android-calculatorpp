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

import org.solovyev.android.calculator.Engine;
import org.solovyev.android.calculator.Preferences;

import javax.annotation.Nonnull;

import jscl.AngleUnit;

enum CalculatorMode {

    simple() {
        @Override
        protected void apply(@Nonnull SharedPreferences preferences) {
            final SharedPreferences.Editor editor = preferences.edit();

            Preferences.Gui.mode.putPreference(editor, Preferences.Gui.Mode.simple);
            Engine.Preferences.angleUnit.putPreference(editor, AngleUnit.deg);
            Engine.Preferences.Output.notation.putPreference(editor, Engine.Notation.dec);
            Engine.Preferences.Output.round.putPreference(editor, true);

            editor.apply();
        }
    },

    engineer() {
        @Override
        protected void apply(@Nonnull SharedPreferences preferences) {
            final SharedPreferences.Editor editor = preferences.edit();

            Preferences.Gui.mode.putPreference(editor, Preferences.Gui.Mode.engineer);
            Engine.Preferences.angleUnit.putPreference(editor, AngleUnit.rad);
            Engine.Preferences.Output.notation.putPreference(editor, Engine.Notation.eng);
            Engine.Preferences.Output.round.putPreference(editor, false);

            editor.apply();
        }
    };

    @Nonnull
    static CalculatorMode getDefaultMode() {
        return engineer;
    }

    @Nonnull
    static CalculatorMode fromGuiLayout(@Nonnull Preferences.Gui.Mode mode) {
        switch (mode) {
            case engineer:
                return engineer;
            case simple:
                return simple;
            default:
                return getDefaultMode();
        }
    }

    protected abstract void apply(@Nonnull SharedPreferences preferences);
}
