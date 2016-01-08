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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;

import static org.solovyev.android.calculator.wizard.CalculatorLayout.big_buttons;
import static org.solovyev.android.calculator.wizard.CalculatorLayout.optimized;

/**
 * User: serso
 * Date: 6/19/13
 * Time: 12:33 AM
 */
public class ChooseLayoutWizardStep extends WizardFragment implements AdapterView.OnItemSelectedListener {

    private ImageView image;

    @Override
    protected int getViewResId() {
        return R.layout.cpp_wizard_step_choose_layout;
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        final CalculatorLayout layout = CalculatorLayout.fromGuiLayout(Preferences.Gui.layout.getPreference(App.getPreferences()));

        image = (ImageView) root.findViewById(R.id.wizard_layout_image);
        final Spinner spinner = (Spinner) root.findViewById(R.id.wizard_layout_spinner);
        spinner.setAdapter(WizardArrayAdapter.create(getActivity(), R.array.cpp_layouts));
        spinner.setSelection(layout == big_buttons ? 0 : 1);
        spinner.setOnItemSelectedListener(this);

        updateImage(layout);
    }

    private void updateImage(@Nonnull CalculatorLayout layout) {
        image.setImageResource(layout == big_buttons ? R.drawable.layout_big_buttons : R.drawable.layout_optimized);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final CalculatorLayout layout = position == 0 ? big_buttons : optimized;
        layout.apply(App.getPreferences());
        updateImage(layout);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
