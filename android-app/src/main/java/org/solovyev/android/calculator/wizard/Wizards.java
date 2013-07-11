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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.solovyev.android.calculator.CalculatorApplication;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static org.solovyev.android.calculator.wizard.WizardStep.last;
import static org.solovyev.android.calculator.wizard.WizardStep.welcome;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:23 PM
 */
public final class Wizards {

	public static final String FIRST_TIME_WIZARD = "first-wizard";
	public static final String DEFAULT_WIZARD_FLOW = "app-wizard";
	static final String FLOW = "flow";
	static final String FLOW_FINISHED = "flow_finished";
	static final String STEP = "step";

	private Wizards() {
		throw new AssertionError();
	}

	@Nonnull
	public static WizardFlow getWizardFlow(@Nonnull String name) {
		if (FIRST_TIME_WIZARD.equals(name)) {
			return newFirstTimeWizardFlow();
		} else if (DEFAULT_WIZARD_FLOW.equals(name)) {
			return newDefaultWizardFlow();
		} else {
			throw new IllegalArgumentException("Wizard flow " + name + " is not supported");
		}
	}

	public static boolean isWizardFinished(@Nonnull String name) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CalculatorApplication.getInstance());
		return preferences.getBoolean(makeFinishedPreferenceKey(name), false);
	}

	public static boolean isWizardStarted(@Nonnull String name) {
		return getLastSavedWizardStepName(name) != null;
	}

	static void saveLastWizardStep(@Nonnull WizardFlow flow, @Nonnull WizardStep step) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CalculatorApplication.getInstance());
		final SharedPreferences.Editor editor = preferences.edit();

		editor.putString(makeLastStepPreferenceKey(flow), step.name());

		editor.commit();
	}

	@Nullable
	static String getLastSavedWizardStepName(@Nonnull String name) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CalculatorApplication.getInstance());

		return preferences.getString(makeLastStepPreferenceKey(name), null);
	}

	static void saveWizardFinished(@Nonnull WizardFlow flow, @Nonnull WizardStep step, boolean forceFinish) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CalculatorApplication.getInstance());
		final SharedPreferences.Editor editor = preferences.edit();

		editor.putBoolean(makeFinishedPreferenceKey(flow), forceFinish || flow.getNextStep(step) == null);

		editor.commit();
	}

	@Nonnull
	private static String makeFinishedPreferenceKey(@Nonnull WizardFlow flow) {
		return makeFinishedPreferenceKey(flow.getName());
	}

	@Nonnull
	private static String makeFinishedPreferenceKey(@Nonnull String flowName) {
		return FLOW_FINISHED + ":" + flowName;
	}

	@Nonnull
	private static String makeLastStepPreferenceKey(@Nonnull WizardFlow flow) {
		return makeLastStepPreferenceKey(flow.getName());
	}

	@Nonnull
	private static String makeLastStepPreferenceKey(@Nonnull String flowName) {
		return FLOW + ":" + flowName;
	}

	@Nonnull
	static WizardFlow newDefaultWizardFlow() {
		final List<WizardStep> wizardSteps = new ArrayList<WizardStep>();
		for (WizardStep wizardStep : WizardStep.values()) {
			if (wizardStep != welcome && wizardStep != last && wizardStep.isVisible()) {
				wizardSteps.add(wizardStep);
			}
		}
		return new ListWizardFlow(DEFAULT_WIZARD_FLOW, wizardSteps);
	}

	@Nonnull
	static WizardFlow newFirstTimeWizardFlow() {
		final List<WizardStep> wizardSteps = new ArrayList<WizardStep>();
		for (WizardStep wizardStep : WizardStep.values()) {
			if (wizardStep.isVisible()) {
				wizardSteps.add(wizardStep);
			}
		}
		return new ListWizardFlow(FIRST_TIME_WIZARD, wizardSteps);
	}
}
