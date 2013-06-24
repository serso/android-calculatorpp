package org.solovyev.android.calculator.wizard;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Display;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.solovyev.android.calculator.CalculatorPreferences;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.solovyev.android.calculator.CalculatorPreferences.Gui.Layout.main_calculator;
import static org.solovyev.android.calculator.CalculatorPreferences.Gui.Layout.main_calculator_mobile;
import static org.solovyev.android.calculator.wizard.CalculatorLayout.big_buttons;
import static org.solovyev.android.calculator.wizard.CalculatorLayout.optimized;
import static org.solovyev.android.calculator.wizard.CalculatorMode.engineer;
import static org.solovyev.android.calculator.wizard.CalculatorMode.simple;

@RunWith(RobolectricTestRunner.class)
public class WizardStepTest {

	private FragmentActivity activity;

	@Before
	public void setUp() throws Exception {
		activity = Robolectric.buildActivity(FragmentActivity.class).create().get();
	}

	@Test
	public void testFragmentsShouldBeInstantiated() throws Exception {
		for (WizardStep wizardStep : WizardStep.values()) {
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
		WizardStep.choose_mode.onNext(modeFragment);
	}

	private void chooseLayout(CalculatorLayout layout) {
		final ChooseLayoutWizardStep layoutFragment = mock(ChooseLayoutWizardStep.class);
		when(layoutFragment.getSelectedLayout()).thenReturn(layout);
		when(layoutFragment.getActivity()).thenReturn(activity);
		WizardStep.choose_layout.onNext(layoutFragment);
	}

	@Config(qualifiers = "large")
	@Test
	public void testChooseLayoutShouldBeVisibleForTablet() throws Exception {
		assertTrue(WizardStep.choose_layout.isVisible());
	}

	@Config(qualifiers = "normal")
	@Test
	public void testChooseLayoutShouldNotBeVisibleForMobile() throws Exception {
		assertFalse(WizardStep.choose_layout.isVisible());
	}
}
