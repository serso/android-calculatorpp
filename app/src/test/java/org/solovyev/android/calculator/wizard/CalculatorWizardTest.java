package org.solovyev.android.calculator.wizard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.solovyev.android.calculator.wizard.CalculatorWizardStep.choose_mode;
import static org.solovyev.android.calculator.wizard.CalculatorWizardStep.last;
import static org.solovyev.android.calculator.wizard.CalculatorWizardStep.welcome;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.solovyev.android.calculator.BuildConfig;
import org.solovyev.android.wizard.Wizard;
import org.solovyev.android.wizard.WizardFlow;
import org.solovyev.android.wizard.Wizards;

import javax.annotation.Nonnull;

@Config(manifest = Config.NONE)
@RunWith(value = RobolectricTestRunner.class)
public class CalculatorWizardTest {

    @Nonnull
    private Wizards wizards;

    @Nonnull
    private Wizard wizard;

    @Nonnull
    private Wizard defaultWizard;

    @Before
    public void setUp() throws Exception {
        wizards = new CalculatorWizards(RuntimeEnvironment.application);
        wizard = wizards.getWizard(null);
        defaultWizard = wizards.getWizard(CalculatorWizards.DEFAULT_WIZARD_FLOW);
    }

    @Test
    public void testDefaultFlowShouldNotContainWelcomeAndLastSteps() throws Exception {
        final WizardFlow flow = defaultWizard.getFlow();
        assertNull(flow.getStepByName(welcome.getName()));
        assertNull(flow.getStepByName(last.getName()));
    }

    @Test
    public void testFirstTimeFlowShouldContainWelcomeAndLastSteps() throws Exception {
        final WizardFlow flow = wizard.getFlow();
        assertNotNull(flow.getStepByName(welcome.getName()));
        assertNotNull(flow.getStepByName(last.getName()));
    }

    @Test
    public void testShouldThrowExceptionIfUnknownWizard() throws Exception {
        try {
            wizards.getWizard("testtesttesttesttest");
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    @Test
    public void testShouldReturnWizard() throws Exception {
        assertNotNull(wizards.getWizard(CalculatorWizards.FIRST_TIME_WIZARD));
        assertNotNull(wizards.getWizard(CalculatorWizards.DEFAULT_WIZARD_FLOW));
    }

    @Test
    public void testShouldSaveWizardIsFinishedWhenNotLastStepAndForce() throws Exception {
        assertFalse(wizard.isFinished());
        wizard.saveFinished(CalculatorWizardStep.drag_button, true);

        assertTrue(wizard.isFinished());
    }

    @Test
    public void testShouldNotSaveWizardIsFinishedWhenNotLastStepAndNotForce() throws Exception {
        assertFalse(wizard.isFinished());
        wizard.saveFinished(CalculatorWizardStep.drag_button, false);

        assertFalse(wizard.isFinished());
    }

    @Test
    public void testShouldSaveWizardIsFinishedWhenLastStep() throws Exception {
        assertFalse(wizard.isFinished());
        wizard.saveFinished(CalculatorWizardStep.last, false);

        assertTrue(wizard.isFinished());
    }

    @Test
    public void testShouldSaveLastWizardStep() throws Exception {
        assertFalse(wizard.isStarted());
        assertNull(wizard.getLastSavedStepName());

        wizard.saveLastStep(choose_mode);
        assertTrue(wizard.isStarted());
        assertEquals(choose_mode.name(), wizard.getLastSavedStepName());
    }
}
