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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;
import org.solovyev.android.CalculatorTestRunner;
import org.solovyev.android.calculator.CalculatorPreferences;
import org.solovyev.android.wizard.BaseWizardActivity;
import org.solovyev.android.wizard.WizardUi;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

@RunWith(CalculatorTestRunner.class)
public class OnScreenCalculatorWizardStepTest {

	@Nonnull
	private OnScreenCalculatorWizardStep fragment;

	@Nonnull
	private CalculatorWizardActivity activity;

	@Nonnull
	private ActivityController<CalculatorWizardActivity> controller;
	private Field uiField;

	@Before
	public void setUp() throws Exception {
		uiField = BaseWizardActivity.class.getDeclaredField("ui");
		uiField.setAccessible(true);

		createActivity();
		setFragment();
	}

	@Nonnull
	private WizardUi getWizardUi() throws IllegalAccessException {
		return (WizardUi) uiField.get(activity);
	}

	private void createActivity() {
		controller = Robolectric.buildActivity(CalculatorWizardActivity.class).create().start().resume();
		activity = controller.get();
	}

	private void setFragment() throws IllegalAccessException {
		getWizardUi().setStep(CalculatorWizardStep.on_screen_calculator);
		activity.getSupportFragmentManager().executePendingTransactions();
		fragment = (OnScreenCalculatorWizardStep) activity.getSupportFragmentManager().findFragmentByTag(CalculatorWizardStep.on_screen_calculator.getFragmentTag());
	}

	@Test
	public void testShouldRestoreStateOnRestart() throws Exception {
		fragment.getOnscreenCalculatorCheckbox().setChecked(true);
		controller.restart();
		assertTrue(fragment.getOnscreenCalculatorCheckbox().isChecked());

		fragment.getOnscreenCalculatorCheckbox().setChecked(false);
		controller.restart();
		assertFalse(fragment.getOnscreenCalculatorCheckbox().isChecked());
	}

	@Test
	public void testShouldBeEnabledIfIconIsShown() throws Exception {
		testShouldBeEqualsToIconState(true);
	}

	@Test
	public void testShouldBeDisabledIfIconIsNotShown() throws Exception {
		testShouldBeEqualsToIconState(false);
	}

	private void testShouldBeEqualsToIconState(boolean iconEnabled) throws IllegalAccessException {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Robolectric.application);
		CalculatorPreferences.OnscreenCalculator.showAppIcon.putPreference(preferences, iconEnabled);
		createActivity();
		setFragment();
		assertEquals(iconEnabled, fragment.isOnscreenCalculatorEnabled());
	}
}
