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

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;

import static org.solovyev.android.calculator.wizard.CalculatorMode.*;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:59 PM
 */
public class ChooseModeWizardStep extends WizardFragment implements AdapterView.OnItemSelectedListener {

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

	private Spinner spinner;
	private TextView description;

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

		spinner = (Spinner) root.findViewById(R.id.wizard_mode_spinner);
		spinner.setAdapter(new MyArrayAdapter(getActivity()));
		spinner.setOnItemSelectedListener(this);

		description = (TextView) root.findViewById(R.id.wizard_mode_description);
		updateDescription();
	}

	private void updateDescription() {
		description.setText(mode == simple ? R.string.cpp_wizard_mode_simple_description : R.string.cpp_wizard_mode_engineer_description);
	}

	@Nonnull
	CalculatorMode getSelectedMode() {
		if (spinner != null) {
			return mode;
		}

		return getDefaultMode();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putSerializable(MODE, mode);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		mode = position == 0 ? simple : engineer;
		updateDescription();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	private static final class MyArrayAdapter extends ArrayAdapter<String> {

		public MyArrayAdapter(Context context) {
			super(context, android.R.layout.simple_spinner_item, context.getResources().getStringArray(R.array.cpp_modes));
			setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final View view = super.getView(position, convertView, parent);
			if (view instanceof TextView) {
				((TextView) view).setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
			}
			return view;
		}
	}
}
