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

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.R;

import javax.annotation.Nullable;


public class OnScreenCalculatorWizardStep extends WizardFragment implements CompoundButton.OnCheckedChangeListener {

    @Nullable
    private CheckBox checkbox;

    @Override
    protected int getViewResId() {
        return R.layout.cpp_wizard_step_onscreen;
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        final Boolean enabled = Preferences.Onscreen.showAppIcon.getPreference(App.getPreferences());
        checkbox = (CheckBox) root.findViewById(R.id.wizard_onscreen_app_enabled_checkbox);
        checkbox.setChecked(enabled);
        checkbox.setOnCheckedChangeListener(this);

        if (App.getTheme().light) {
            final TextView message = (TextView) root.findViewById(R.id.wizard_onscreen_message);
            message.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.logo_wizard_window_light, 0, 0);
        }
    }

    @Nullable
    CheckBox getCheckbox() {
        return checkbox;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
        Preferences.Onscreen.showAppIcon.putPreference(App.getPreferences(), checked);
    }
}

