package org.solovyev.android.calculator.wizard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
		activity = controller.get();
	}

	@Test
	public void testCreate() throws Exception {
		controller.attach();
		controller.create();

		assertNotNull(activity.getFlow());
		assertEquals(FirstTimeWizardFlow.NAME, activity.getFlow().getName());
		assertNotNull(activity.getStep());
		assertEquals(activity.getFlow().getFirstStep(), activity.getStep());

		activity.setStep(WizardStep.choose_mode);

		final Bundle outState = new Bundle();
		controller.saveInstanceState(outState);

		controller = Robolectric.buildActivity(CalculatorWizardActivity.class);
		controller.create(outState);

		activity = controller.get();
		assertNotNull(activity.getFlow());
		assertEquals(FirstTimeWizardFlow.NAME, activity.getFlow().getName());
		assertNotNull(activity.getStep());
		assertEquals(WizardStep.choose_mode, activity.getStep());

		final Intent intent = new Intent();
		intent.setClass(activity, CalculatorWizardActivity.class);
		intent.putExtra(CalculatorWizardActivity.FLOW, AppWizardFlow.NAME);
		controller = Robolectric.buildActivity(CalculatorWizardActivity.class).withIntent(intent);
		activity = controller.get();
		controller.create(null);
		assertEquals(AppWizardFlow.NAME, activity.getFlow().getName());
		assertEquals(activity.getFlow().getFirstStep(), activity.getStep());

		final Bundle outState1 = new Bundle();
		controller.saveInstanceState(outState1);

		controller = Robolectric.buildActivity(CalculatorWizardActivity.class);
		activity = controller.get();
		controller.create(outState1);
		assertEquals(AppWizardFlow.NAME, activity.getFlow().getName());
		assertEquals(activity.getFlow().getFirstStep(), activity.getStep());
	}

	@Test
	public void testFragment() throws Exception {
		controller.create().start().resume();

		final FragmentManager fm = activity.getSupportFragmentManager();
		Fragment f = fm.findFragmentByTag(WizardStep.welcome.getFragmentTag());
		Assert.assertNotNull(f);
		Assert.assertTrue(f.isAdded());

		activity.setStep(WizardStep.choose_mode);

		f = fm.findFragmentByTag(WizardStep.choose_mode.getFragmentTag());
		Assert.assertNotNull(f);
		Assert.assertTrue(f.isAdded());
	}

	@Test
	public void testSetStep() throws Exception {
		controller.create();
		activity.setStep(WizardStep.choose_mode);
	}

	@Test
	public void testStartWizard() throws Exception {
		final ShadowActivity shadowActivity = Robolectric.shadowOf(controller.get());
		CalculatorWizardActivity.startWizard(AppWizardFlow.NAME, shadowActivity.getApplicationContext());
		Assert.assertNotNull(shadowActivity.getNextStartedActivity());
	}
}
