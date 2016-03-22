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

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import org.solovyev.android.calculator.BaseActivity;
import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.keyboard.BaseKeyboardUi;
import org.solovyev.android.views.Adjuster;
import org.solovyev.android.views.dragbutton.DirectionDragButton;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static org.solovyev.android.calculator.App.cast;
import static org.solovyev.android.calculator.wizard.CalculatorMode.engineer;
import static org.solovyev.android.calculator.wizard.CalculatorMode.simple;
import static org.solovyev.android.views.dragbutton.DragDirection.*;

public class ChooseModeWizardStep extends WizardFragment implements AdapterView.OnItemSelectedListener {

    private DirectionDragButton button;
    private TextView description;

    @Inject
    Typeface typeface;

    @Override
    protected int getViewResId() {
        return R.layout.cpp_wizard_step_choose_mode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cast(this).getComponent().inject(this);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        final CalculatorMode mode = CalculatorMode.fromGuiLayout(Preferences.Gui.mode.getPreference(preferences));
        final Spinner spinner = (Spinner) root.findViewById(R.id.wizard_mode_spinner);
        spinner.setAdapter(WizardArrayAdapter.create(getActivity(), R.array.cpp_modes));
        spinner.setSelection(mode == simple ? 0 : 1);
        spinner.setOnItemSelectedListener(this);

        button = (DirectionDragButton) root.findViewById(R.id.wizard_mode_button);
        BaseActivity.setFont(button, typeface);
        Adjuster.adjustText(button, BaseKeyboardUi.getTextScale(getActivity()));
        description = (TextView) root.findViewById(R.id.wizard_mode_description);
        updateDescription(mode);
    }

    private void updateDescription(@Nonnull CalculatorMode mode) {
        boolean simple = mode == CalculatorMode.simple;
        description.setText(simple ? R.string.cpp_wizard_mode_simple_description : R.string.cpp_wizard_mode_engineer_description);
        if (simple) {
            button.setText(up, "");
            button.setText(down, "");
            button.setText(left, "");
        } else {
            button.setText(up, "sin");
            button.setText(down, "ln");
            button.setText(left, "i");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final CalculatorMode mode = position == 0 ? simple : engineer;
        mode.apply(preferences);
        updateDescription(mode);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
