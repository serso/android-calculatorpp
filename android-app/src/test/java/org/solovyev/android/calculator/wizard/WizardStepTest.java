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

import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.solovyev.android.calculator.CalculatorPreferences;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.solovyev.android.calculator.CalculatorApplication.getPreferences;
import static org.solovyev.android.calculator.CalculatorPreferences.Gui.Layout.main_calculator;
import static org.solovyev.android.calculator.CalculatorPreferences.Gui.Layout.main_calculator_mobile;
import static org.solovyev.android.calculator.CalculatorPreferences.OnscreenCalculator.showAppIcon;
import static org.solovyev.android.calculator.wizard.CalculatorLayout.big_buttons;
import static org.solovyev.android.calculator.wizard.CalculatorLayout.optimized;
import static org.solovyev.android.calculator.wizard.CalculatorMode.engineer;
import static org.solovyev.android.calculator.wizard.CalculatorMode.simple;
import static org.solovyev.android.calculator.wizard.ChooseLayoutWizardStep.LAYOUT;
import static org.solovyev.android.calculator.wizard.ChooseModeWizardStep.MODE;
import static org.solovyev.android.calculator.wizard.OnScreenCalculatorWizardStep.ONSCREEN_CALCULATOR_ENABLED;
import static org.solovyev.android.calculator.wizard.CalculatorWizardStep.choose_layout;
import static org.solovyev.android.calculator.wizard.CalculatorWizardStep.choose_mode;
import static org.solovyev.android.calculator.wizard.CalculatorWizardStep.on_screen_calculator;

@RunWith(RobolectricTestRunner.class)
public class WizardStepTest {

	private FragmentActivity activity;

	@Before
	public void setUp() throws Exception {
		activity = Robolectric.buildActivity(FragmentActivity.class).create().get();
	}

	@Test
	public void testFragmentsShouldBeInstantiated() throws Exception {
		for (CalculatorWizardStep wizardStep : CalculatorWizardStep.values()) {
			Fragment.instantiate(Robolectric.application, wizardStep.getFragmentClass().getName());
		}
	}

	@Test
	public void testShouldBeMainMobileLayout() throws Exception {
		chooseLayout(big_buttons);
		chooseMode(engineer);

		assertUiLayoutEquals(main_calculator_mobile);
	}

	@Test
	public void testShouldBeMainLayout() throws Exception {
		chooseLayout(optimized);
		chooseMode(engineer);

		assertUiLayoutEquals(main_calculator);
	}

	@Test
	public void testShouldBeSimpleLayout() throws Exception {
		chooseLayout(optimized);
		chooseMode(simple);

		assertUiLayoutEquals(CalculatorPreferences.Gui.Layout.simple);
	}

	@Test
	public void testShouldBeSimpleMobileLayout() throws Exception {
		chooseLayout(big_buttons);
		chooseMode(simple);

		assertUiLayoutEquals(CalculatorPreferences.Gui.Layout.simple_mobile);
	}

	private void assertUiLayoutEquals(CalculatorPreferences.Gui.Layout uiLayout) {
		Assert.assertEquals(uiLayout, CalculatorPreferences.Gui.layout.getPreference(PreferenceManager.getDefaultSharedPreferences(Robolectric.application)));
	}

	private void chooseMode(CalculatorMode mode) {
		final ChooseModeWizardStep modeFragment = mock(ChooseModeWizardStep.class);
		when(modeFragment.getSelectedMode()).thenReturn(mode);
		when(modeFragment.getActivity()).thenReturn(activity);
		CalculatorWizardStep.choose_mode.onNext(modeFragment);
	}

	private void chooseLayout(CalculatorLayout layout) {
		final ChooseLayoutWizardStep layoutFragment = mock(ChooseLayoutWizardStep.class);
		when(layoutFragment.getSelectedLayout()).thenReturn(layout);
		when(layoutFragment.getActivity()).thenReturn(activity);
		choose_layout.onNext(layoutFragment);
	}

/*	@Config(qualifiers = "large")
	@Test
	public void testChooseLayoutShouldBeVisibleForTablet() throws Exception {
		assertTrue(CalculatorWizardStep.choose_layout.isVisible());
	}*/

	@Config(qualifiers = "normal")
	@Test
	public void testChooseLayoutShouldNotBeVisibleForMobile() throws Exception {
		assertFalse(choose_layout.isVisible());
	}

	@Test
	public void testOnscreenCalculatorShouldNotBeShown() throws Exception {
		doOnscreenStep(false);
		assertFalse(showAppIcon.getPreference(getPreferences()));
	}

	@Test
	public void testOnscreenCalculatorShouldBeShown() throws Exception {
		doOnscreenStep(true);
		assertTrue(showAppIcon.getPreference(getPreferences()));
	}


	private void doOnscreenStep(boolean onscreenCalculatorEnabled) {
		final OnScreenCalculatorWizardStep f = mock(OnScreenCalculatorWizardStep.class);
		when(f.isOnscreenCalculatorEnabled()).thenReturn(onscreenCalculatorEnabled);
		when(f.getActivity()).thenReturn(activity);
		CalculatorWizardStep.on_screen_calculator.onNext(f);
	}

	@SuppressWarnings("ConstantConditions")
	@Test
	public void testChooseLayoutFragmentArgs() throws Exception {
		CalculatorPreferences.Gui.layout.putPreference(getPreferences(), CalculatorPreferences.Gui.Layout.simple);
		assertEquals(CalculatorLayout.optimized, choose_layout.getFragmentArgs().getSerializable(LAYOUT));

		CalculatorPreferences.Gui.layout.putPreference(getPreferences(), CalculatorPreferences.Gui.Layout.simple_mobile);
		assertEquals(CalculatorLayout.big_buttons, choose_layout.getFragmentArgs().getSerializable(LAYOUT));
	}

	@SuppressWarnings("ConstantConditions")
	@Test
	public void testChooseModeFragmentArgs() throws Exception {
		CalculatorPreferences.Gui.layout.putPreference(getPreferences(), CalculatorPreferences.Gui.Layout.main_calculator);
		assertEquals(CalculatorMode.engineer, choose_mode.getFragmentArgs().getSerializable(MODE));

		CalculatorPreferences.Gui.layout.putPreference(getPreferences(), CalculatorPreferences.Gui.Layout.simple_mobile);
		assertEquals(CalculatorMode.simple, choose_mode.getFragmentArgs().getSerializable(MODE));
	}

	@SuppressWarnings("ConstantConditions")
	@Test
	public void testOnscreenFragmentArgs() throws Exception {
		CalculatorPreferences.OnscreenCalculator.showAppIcon.putPreference(getPreferences(), true);
		assertTrue(on_screen_calculator.getFragmentArgs().getBoolean(ONSCREEN_CALCULATOR_ENABLED));

		CalculatorPreferences.OnscreenCalculator.showAppIcon.putPreference(getPreferences(), false);
		assertFalse(on_screen_calculator.getFragmentArgs().getBoolean(ONSCREEN_CALCULATOR_ENABLED));
	}
}
