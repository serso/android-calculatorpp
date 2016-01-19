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

package org.solovyev.android.calculator.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.widget.TextView;

import org.solovyev.android.calculator.Engine;
import org.solovyev.android.calculator.Locator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 12/10/11
 * Time: 10:34 PM
 */
public class CalculatorAdditionalTitle extends TextView implements SharedPreferences.OnSharedPreferenceChangeListener {

    public CalculatorAdditionalTitle(Context context) {
        super(context);
    }

    public CalculatorAdditionalTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalculatorAdditionalTitle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(@Nonnull SharedPreferences preferences) {
        onSharedPreferenceChanged(preferences, null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, @Nullable String key) {
        setText(((Engine) Locator.getInstance().getEngine()).getNumeralBaseFromPrefs(preferences)
                + " / " +
                ((Engine) Locator.getInstance().getEngine()).getAngleUnitsFromPrefs(preferences));
    }
}
