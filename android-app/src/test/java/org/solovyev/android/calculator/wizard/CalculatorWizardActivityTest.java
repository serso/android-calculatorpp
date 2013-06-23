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

import static org.junit.Assert.*;
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
		assertEquals(Wizard.FIRST_TIME_WIZARD, activity.getFlow().getName());
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
		assertEquals(Wizard.FIRST_TIME_WIZARD, activity.getFlow().getName());
		assertNotNull(activity.getStep());
		assertEquals(choose_mode, activity.getStep());
	}

	@Test
	public void testCreate() throws Exception {
		final Intent intent = new Intent();
		intent.setClass(activity, CalculatorWizardActivity.class);
		intent.putExtra(CalculatorWizardActivity.FLOW, Wizard.DEFAULT_WIZARD_FLOW);
		controller = Robolectric.buildActivity(CalculatorWizardActivity.class).withIntent(intent);
		activity = controller.get();
		controller.create(null);
		assertEquals(Wizard.DEFAULT_WIZARD_FLOW, activity.getFlow().getName());
		assertEquals(activity.getFlow().getFirstStep(), activity.getStep());

		final Bundle outState1 = new Bundle();
		controller.saveInstanceState(outState1);

		controller = Robolectric.buildActivity(CalculatorWizardActivity.class);
		activity = controller.get();
		controller.create(outState1);
		assertEquals(Wizard.DEFAULT_WIZARD_FLOW, activity.getFlow().getName());
		assertEquals(activity.getFlow().getFirstStep(), activity.getStep());
	}

	@Test
	public void testShouldAddFirstFragment() throws Exception {
		controller.create().start().resume();

		final FragmentManager fm = activity.getSupportFragmentManager();
		final Fragment f = fm.findFragmentByTag(WizardStep.welcome.getFragmentTag());
		assertNotNull(f);
		assertTrue(f.isAdded());
	}

	@Test
	public void testShouldAddStepFragment() throws Exception {
		controller.create().start().resume();

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
		startWizard(Wizard.DEFAULT_WIZARD_FLOW, shadowActivity.getApplicationContext());
		assertNotNull(shadowActivity.getNextStartedActivity());
	}
}
