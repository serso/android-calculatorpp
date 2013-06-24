package org.solovyev.android.calculator.wizard;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.annotation.Nonnull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;
import org.solovyev.android.calculator.CalculatorPreferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class OnScreenCalculatorWizardStepTest {

	@Nonnull
	private OnScreenCalculatorWizardStep fragment;

	@Nonnull
	private CalculatorWizardActivity activity;

	@Nonnull
	private ActivityController<CalculatorWizardActivity> controller;

	@Before
	public void setUp() throws Exception {
		createActivity();
		setFragment();
	}

	private void createActivity() {
		controller = Robolectric.buildActivity(CalculatorWizardActivity.class).create().start().resume();
		activity = controller.get();
	}

	private void setFragment() {
		activity.setStep(WizardStep.on_screen_calculator);
		activity.getSupportFragmentManager().executePendingTransactions();
		fragment = (OnScreenCalculatorWizardStep) activity.getSupportFragmentManager().findFragmentByTag(WizardStep.on_screen_calculator.getFragmentTag());
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

	private void testShouldBeEqualsToIconState(boolean iconEnabled) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Robolectric.application);
		CalculatorPreferences.OnscreenCalculator.showAppIcon.putPreference(preferences, iconEnabled);
		createActivity();
		setFragment();
		assertEquals(iconEnabled, fragment.isOnscreenCalculatorEnabled());
	}
}
