package org.solovyev.android.calculator.wizard;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;
import static org.solovyev.android.calculator.wizard.WizardStep.choose_mode;
import static org.solovyev.android.calculator.wizard.WizardStep.last;
import static org.solovyev.android.calculator.wizard.WizardStep.welcome;
import static org.solovyev.android.calculator.wizard.Wizards.*;

/**
 * User: serso
 * Date: 7/11/13
 * Time: 10:29 AM
 */
@RunWith(value = RobolectricTestRunner.class)
public class WizardsTest {

	@Test
	public void testDefaultFlowShouldNotContainWelcomeAndLastSteps() throws Exception {
		final WizardFlow flow = Wizards.newDefaultWizardFlow();
		assertNull(flow.getStep(welcome.getName()));
		assertNull(flow.getStep(last.getName()));
	}

	@Test
	public void testFirstTimeFlowShouldContainWelcomeAndLastSteps() throws Exception {
		final WizardFlow flow = Wizards.newFirstTimeWizardFlow();
		assertNotNull(flow.getStep(welcome.getName()));
		assertNotNull(flow.getStep(last.getName()));
	}

	@Test
	public void testShouldThrowExceptionIfUnknownFlow() throws Exception {
		try {
			getWizardFlow("testtesttesttesttest");
			fail();
		} catch (IllegalArgumentException e) {
			// ok
		}
	}

	@Test
	public void testShouldReturnFlow() throws Exception {
		assertNotNull(getWizardFlow(FIRST_TIME_WIZARD));
		assertNotNull(getWizardFlow(DEFAULT_WIZARD_FLOW));
	}

	@Test
	public void testShouldSaveWizardIsFinishedWhenNotLastStepAndForce() throws Exception {
		assertFalse(isWizardFinished(FIRST_TIME_WIZARD));
		final WizardFlow flow = getWizardFlow(FIRST_TIME_WIZARD);
		saveWizardFinished(flow, WizardStep.drag_button, true);

		assertTrue(isWizardFinished(FIRST_TIME_WIZARD));
	}

	@Test
	public void testShouldNotSaveWizardIsFinishedWhenNotLastStepAndNotForce() throws Exception {
		assertFalse(isWizardFinished(FIRST_TIME_WIZARD));
		final WizardFlow flow = getWizardFlow(FIRST_TIME_WIZARD);
		saveWizardFinished(flow, WizardStep.drag_button, false);

		assertFalse(isWizardFinished(FIRST_TIME_WIZARD));
	}

	@Test
	public void testShouldSaveWizardIsFinishedWhenLastStep() throws Exception {
		assertFalse(isWizardFinished(FIRST_TIME_WIZARD));
		final WizardFlow flow = getWizardFlow(FIRST_TIME_WIZARD);
		saveWizardFinished(flow, WizardStep.last, false);

		assertTrue(isWizardFinished(FIRST_TIME_WIZARD));
	}

	@Test
	public void testShouldSaveLastWizardStep() throws Exception {
		assertFalse(isWizardStarted(FIRST_TIME_WIZARD));
		assertNull(getLastSavedWizardStepName(FIRST_TIME_WIZARD));

		final WizardFlow flow = getWizardFlow(FIRST_TIME_WIZARD);
		saveLastWizardStep(flow, choose_mode);
		assertTrue(isWizardStarted(FIRST_TIME_WIZARD));
		assertEquals(choose_mode.name(), getLastSavedWizardStepName(FIRST_TIME_WIZARD));
	}
}
