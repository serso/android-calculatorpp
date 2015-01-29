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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.solovyev.android.calculator.wizard.CalculatorMode.*;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:59 PM
 */
public class ChooseModeWizardStep extends WizardFragment {

	/*
	**********************************************************************
	*
	*                           CONSTANTS
	*
	**********************************************************************
	*/

	static final String MODE = "mode";

	/*
	**********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/

	@Nullable
	private RadioButton simpleModeRadioButton;

	@Nullable
	private RadioButton engineerModeRadioButton;

	private CalculatorMode mode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			mode = (CalculatorMode) savedInstanceState.getSerializable(MODE);
		}

		if (mode == null) {
			mode = (CalculatorMode) getArguments().getSerializable(MODE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		setupNextButton(R.string.acl_wizard_next);
		setupPrevButton(R.string.acl_wizard_back);
		return view;
	}

	@Override
	protected int getViewResId() {
		return R.layout.cpp_wizard_step_choose_mode;
	}

	@Override
	public void onViewCreated(View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);

		simpleModeRadioButton = (RadioButton) root.findViewById(R.id.wizard_simple_mode_radiobutton);
		engineerModeRadioButton = (RadioButton) root.findViewById(R.id.wizard_engineer_mode_radiobutton);

		switch (mode) {
			case simple:
				simpleModeRadioButton.setChecked(true);
				engineerModeRadioButton.setChecked(false);
				break;
			case engineer:
				simpleModeRadioButton.setChecked(false);
				engineerModeRadioButton.setChecked(true);
				break;
		}
	}

	@Nonnull
	CalculatorMode getSelectedMode() {
		CalculatorMode mode = getDefaultMode();

		if (simpleModeRadioButton != null && simpleModeRadioButton.isChecked()) {
			mode = simple;
		}

		if (engineerModeRadioButton != null && engineerModeRadioButton.isChecked()) {
			mode = engineer;
		}

		return mode;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putSerializable(MODE, mode);
	}
}
