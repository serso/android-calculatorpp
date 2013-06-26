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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.solovyev.android.calculator.wizard.CalculatorLayout.*;

/**
 * User: serso
 * Date: 6/19/13
 * Time: 12:33 AM
 */
public class ChooseLayoutWizardStep extends Fragment {

	/*
	**********************************************************************
	*
	*                           CONSTANTS
	*
	**********************************************************************
	*/

	static final String LAYOUT = "layout";

	/*
	**********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/

	@Nullable
	private RadioButton optimizedRadioButton;

	@Nullable
	private RadioButton bigButtonsRadioButton;

	private CalculatorLayout layout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			layout = (CalculatorLayout) savedInstanceState.getSerializable(LAYOUT);
		}

		if (layout == null) {
			layout = (CalculatorLayout) getArguments().getSerializable(LAYOUT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.cpp_wizard_step_choose_layout, null);
	}

	@Override
	public void onViewCreated(View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);

		optimizedRadioButton = (RadioButton) root.findViewById(R.id.wizard_optimized_radiobutton);
		bigButtonsRadioButton = (RadioButton) root.findViewById(R.id.wizard_big_buttons_radiobutton);

		switch (layout) {
			case big_buttons:
				bigButtonsRadioButton.setChecked(true);
				optimizedRadioButton.setChecked(false);
				break;
			case optimized:
				bigButtonsRadioButton.setChecked(false);
				optimizedRadioButton.setChecked(true);
				break;
		}
	}

	@Nonnull
	CalculatorLayout getSelectedLayout() {
		CalculatorLayout layout = getDefaultLayout();

		if (bigButtonsRadioButton != null && bigButtonsRadioButton.isChecked()) {
			layout = big_buttons;
		} else if (optimizedRadioButton != null && optimizedRadioButton.isChecked()) {
			layout = optimized;
		}

		return layout;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putSerializable(LAYOUT, layout);
	}
}
