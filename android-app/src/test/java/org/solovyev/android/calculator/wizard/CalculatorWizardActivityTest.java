package org.solovyev.android.calculator.wizard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.solovyev.android.calculator.wizard.CalculatorWizardActivity.startWizard;
import static org.solovyev.android.calculator.wizard.WizardStep.choose_mode;

/**
 * User: serso
 * Date: 6/17/13
 * Time: 9:57 PM
 */
@RunWith(value = RobolectricTestRunner.class)
public class CalculatorWizardActivityTest {

	private ActivityController<CalculatorWizardActivity> controller;
	private CalculatorWizardActivity activity;

	@Before
	public void setUp() throws Exception {
		controller = Robolectric.buildActivity(CalculatorWizardActivity.class);
		controller.attach();
		controller.create();
		activity = controller.get();
	}

	@Test
	public void testShouldBeFirstTimeWizardByDefault() throws Exception {
		assertEquals(Wizards.FIRST_TIME_WIZARD, activity.getFlow().getName());
	}

	@Test
	public void testShouldBeFirstStep() throws Exception {
		assertNotNull(activity.getStep());
		assertEquals(activity.getFlow().getFirstStep(), activity.getStep());
	}

	@Test
	public void testShouldSaveState() throws Exception {
		activity.setStep(choose_mode);

		final Bundle outState = new Bundle();
		controller.saveInstanceState(outState);

		controller = Robolectric.buildActivity(CalculatorWizardActivity.class);
		controller.create(outState);

		activity = controller.get();
		assertNotNull(activity.getFlow());
		assertEquals(Wizards.FIRST_TIME_WIZARD, activity.getFlow().getName());
		assertNotNull(activity.getStep());
		assertEquals(choose_mode, activity.getStep());
	}

	@Test
	public void testCreate() throws Exception {
		final Intent intent = new Intent();
		intent.setClass(activity, CalculatorWizardActivity.class);
		intent.putExtra(Wizards.FLOW, Wizards.DEFAULT_WIZARD_FLOW);
		controller = Robolectric.buildActivity(CalculatorWizardActivity.class).withIntent(intent);
		controller.create();
		activity = controller.get();
		assertEquals(Wizards.DEFAULT_WIZARD_FLOW, activity.getFlow().getName());
		assertEquals(activity.getFlow().getFirstStep(), activity.getStep());

		final Bundle outState1 = new Bundle();
		controller.saveInstanceState(outState1);

		controller = Robolectric.buildActivity(CalculatorWizardActivity.class);
		activity = controller.get();
		controller.create(outState1);
		assertEquals(Wizards.DEFAULT_WIZARD_FLOW, activity.getFlow().getName());
		assertEquals(activity.getFlow().getFirstStep(), activity.getStep());
	}

	@Test
	public void testShouldAddFirstFragment() throws Exception {
		controller.start().resume();

		final FragmentManager fm = activity.getSupportFragmentManager();
		final Fragment f = fm.findFragmentByTag(WizardStep.welcome.getFragmentTag());
		assertNotNull(f);
		assertTrue(f.isAdded());
	}

	@Test
	public void testShouldAddStepFragment() throws Exception {
		controller.start().resume();

		final FragmentManager fm = activity.getSupportFragmentManager();

		activity.setStep(choose_mode);

		final Fragment f = fm.findFragmentByTag(choose_mode.getFragmentTag());
		assertNotNull(f);
		assertTrue(f.isAdded());
	}

	@Test
	public void testSetStep() throws Exception {
		activity.setStep(choose_mode);
		assertEquals(choose_mode, activity.getStep());
	}

	@Test
	public void testShouldStartWizardActivityAfterStart() throws Exception {
		final ShadowActivity shadowActivity = Robolectric.shadowOf(controller.get());
		startWizard(Wizards.DEFAULT_WIZARD_FLOW, shadowActivity.getApplicationContext());
		assertNotNull(shadowActivity.getNextStartedActivity());
	}

	@Test
	public void testTitleShouldBeSet() throws Exception {
		activity.setStep(choose_mode);
		assertEquals(activity.getString(choose_mode.getTitleResId()), activity.getTitle().toString());
	}

	@Test
	public void testNextButtonShouldNotBeShownAtTheEnd() throws Exception {
		setLastStep();
		assertEquals(VISIBLE, activity.getPrevButton().getVisibility());
		assertEquals(GONE, activity.getNextButton().getVisibility());
		assertEquals(VISIBLE, activity.getFinishButton().getVisibility());
	}

	private void setLastStep() {
		activity.setStep(WizardStep.values()[WizardStep.values().length - 1]);
	}

	@Test
	public void testPrevButtonShouldNotBeShownAtTheStart() throws Exception {
		setFirstStep();
		assertEquals(VISIBLE, activity.getNextButton().getVisibility());
		assertEquals(GONE, activity.getFinishButton().getVisibility());
		assertEquals(GONE, activity.getPrevButton().getVisibility());
	}

	private void setFirstStep() {
		activity.setStep(WizardStep.values()[0]);
	}

	@Test
	public void testShouldSaveLastWizardStateOnPause() throws Exception {
		assertNull(Wizards.getLastSavedWizardStepName(activity.getFlow().getName()));
		activity.setStep(WizardStep.drag_button_step);
		activity.onPause();
		assertEquals(WizardStep.drag_button_step.getName(), Wizards.getLastSavedWizardStepName(activity.getFlow().getName()));
	}

	@Test
	public void testShouldSaveFinishedIfLastStep() throws Exception {
		assertFalse(Wizards.isWizardFinished(activity.getFlow().getName()));
		setLastStep();
		activity.finishFlow();
		assertTrue(Wizards.isWizardFinished(activity.getFlow().getName()));
	}

	@Test
	public void testShouldNotSaveFinishedIfNotLastStep() throws Exception {
		assertFalse(Wizards.isWizardFinished(activity.getFlow().getName()));
		setFirstStep();
		activity.finishFlow();
		assertFalse(Wizards.isWizardFinished(activity.getFlow().getName()));
	}
}
