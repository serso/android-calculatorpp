package org.solovyev.android.calculator.wizard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import com.actionbarsherlock.app.SherlockFragment;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.solovyev.android.calculator.wizard.CalculatorMode.*;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:59 PM
 */
public class ChooseModeWizardStep extends SherlockFragment {

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

		if(savedInstanceState != null) {
			mode = (CalculatorMode) savedInstanceState.getSerializable(MODE);
		}

		if (mode == null) {
			mode = (CalculatorMode) getArguments().getSerializable(MODE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.cpp_wizard_step_choose_mode, null);
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
