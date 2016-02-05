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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.Bind;
import butterknife.ButterKnife;
import org.solovyev.android.calculator.buttons.CppButtons;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static org.solovyev.android.calculator.Engine.Preferences.multiplicationSign;
import static org.solovyev.android.calculator.Engine.Preferences.numeralBase;
import static org.solovyev.android.calculator.NumeralBaseButtons.toggleNumericDigits;
import static org.solovyev.android.calculator.Preferences.Gui.hideNumeralBaseDigits;
import static org.solovyev.android.calculator.Preferences.Gui.showEqualsButton;

public class KeyboardFragment extends BaseFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    SharedPreferences preferences;
    @Bind(R.id.cpp_button_multiplication)
    Button multiplicationButton;
    @Nullable
    @Bind(R.id.cpp_button_equals)
    Button equalsButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void inject(@Nonnull AppComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);
        return view;
    }

    @Nonnull
    @Override
    protected FragmentUi createUi() {
        final Preferences.Gui.Layout layout = Preferences.Gui.getLayout(preferences);
        if (!layout.optimized) {
            return new FragmentUi(R.layout.cpp_app_keyboard_mobile);
        } else {
            return new FragmentUi(R.layout.cpp_app_keyboard);
        }
    }

    @Override
    public void onDestroy() {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (numeralBase.isSameKey(key) || hideNumeralBaseDigits.isSameKey(key)) {
            toggleNumericDigits(this.getActivity(), preferences);
        }

        if (equalsButton != null && showEqualsButton.isSameKey(key)) {
            CppButtons.toggleEqualsButton(preferences, getActivity(), equalsButton);
        }

        if (multiplicationSign.isSameKey(key)) {
            multiplicationButton.setText(Engine.Preferences.multiplicationSign.getPreference(preferences));
        }
    }
}

