package org.solovyev.android.calculator.wizard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import org.solovyev.android.calculator.CalculatorPreferences;
import org.solovyev.android.calculator.R;

import javax.annotation.Nullable;

public class OnScreenCalculatorWizardStep extends Fragment {

	/*
	**********************************************************************
	*
	*                           CONSTANTS
	*
	**********************************************************************
	*/

	static final String ONSCREEN_CALCULATOR_ENABLED = "onscreen_calculator_enabled";

	/*
	**********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/

	@Nullable
	private CheckBox onscreenCalculatorCheckbox;

	private Boolean onscreenCalculatorEnabled;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(savedInstanceState != null && savedInstanceState.containsKey(ONSCREEN_CALCULATOR_ENABLED)) {
			onscreenCalculatorEnabled = savedInstanceState.getBoolean(ONSCREEN_CALCULATOR_ENABLED);
		}

		if (onscreenCalculatorEnabled == null) {
			onscreenCalculatorEnabled = getArguments().getBoolean(ONSCREEN_CALCULATOR_ENABLED, CalculatorPreferences.OnscreenCalculator.showAppIcon.getDefaultValue());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.cpp_wizard_step_onscreen, null);
	}

	@Override
	public void onViewCreated(View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);

		onscreenCalculatorCheckbox = (CheckBox) root.findViewById(R.id.wizard_onscreen_app_enabled_checkbox);
		onscreenCalculatorCheckbox.setChecked(onscreenCalculatorEnabled);
	}

	public Boolean isOnscreenCalculatorEnabled() {
		boolean enabled = CalculatorPreferences.OnscreenCalculator.showAppIcon.getDefaultValue();

		if (onscreenCalculatorCheckbox != null) {
			enabled = onscreenCalculatorCheckbox.isChecked();
		}

		return enabled;
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean(ONSCREEN_CALCULATOR_ENABLED, onscreenCalculatorEnabled);
	}

	@Nullable
	CheckBox getOnscreenCalculatorCheckbox() {
		return onscreenCalculatorCheckbox;
	}
}

