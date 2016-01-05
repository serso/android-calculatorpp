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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;
import org.solovyev.android.CalculatorTestRunner;
import org.solovyev.android.wizard.Wizard;
import org.solovyev.android.wizard.WizardUi;
import org.solovyev.android.wizard.Wizards;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.solovyev.android.calculator.wizard.CalculatorWizardStep.choose_mode;

@RunWith(value = CalculatorTestRunner.class)
public class CalculatorWizardActivityTest {

    private ActivityController<WizardActivity> controller;
    private WizardActivity activity;
    private Wizards wizards;
    private Field uiField;

    @Before
    public void setUp() throws Exception {
        controller = Robolectric.buildActivity(WizardActivity.class);
        activity = controller.get();
        wizards = new CalculatorWizards(RuntimeEnvironment.application);
        activity.setWizards(wizards);
        controller.attach();
        controller.create();

        uiField = WizardActivity.class.getDeclaredField("wizardUi");
        uiField.setAccessible(true);
    }

    @Test
    public void testShouldBeFirstTimeWizardByDefault() throws Exception {
        assertEquals(CalculatorWizards.FIRST_TIME_WIZARD, getWizardUi().getWizard().getName());
    }

    @Nonnull
    private WizardUi getWizardUi() throws IllegalAccessException {
        return (WizardUi) uiField.get(activity);
    }

    @Test
    public void testShouldBeFirstStep() throws Exception {
        assertNotNull(getWizardUi().getStep());
        assertEquals(getWizardUi().getFlow().getFirstStep(), getWizardUi().getStep());
    }

    @Test
    public void testShouldSaveState() throws Exception {
        getWizardUi().setStep(choose_mode);

        final Bundle outState = new Bundle();
        controller.saveInstanceState(outState);

        controller = Robolectric.buildActivity(WizardActivity.class);
        controller.create(outState);

        activity = controller.get();
        assertNotNull(getWizardUi().getFlow());
        assertEquals(CalculatorWizards.FIRST_TIME_WIZARD, getWizardUi().getWizard().getName());
        assertNotNull(getWizardUi().getStep());
        assertEquals(choose_mode, getWizardUi().getStep());
    }

    @Test
    public void testCreate() throws Exception {
        final Intent intent = new Intent();
        intent.setClass(activity, WizardActivity.class);
        intent.putExtra("flow", CalculatorWizards.DEFAULT_WIZARD_FLOW);
        controller = Robolectric.buildActivity(WizardActivity.class).withIntent(intent);
        controller.create();
        activity = controller.get();
        assertEquals(CalculatorWizards.DEFAULT_WIZARD_FLOW, getWizardUi().getWizard().getName());
        assertEquals(getWizardUi().getFlow().getFirstStep(), getWizardUi().getStep());

        final Bundle outState1 = new Bundle();
        controller.saveInstanceState(outState1);

        controller = Robolectric.buildActivity(WizardActivity.class);
        activity = controller.get();
        controller.create(outState1);
        assertEquals(CalculatorWizards.DEFAULT_WIZARD_FLOW, getWizardUi().getWizard().getName());
        assertEquals(getWizardUi().getFlow().getFirstStep(), getWizardUi().getStep());
    }

    @Test
    public void testShouldAddFirstFragment() throws Exception {
        controller.start().resume();

        final FragmentManager fm = activity.getSupportFragmentManager();
        final Fragment f = fm.findFragmentByTag(CalculatorWizardStep.welcome.getFragmentTag());
        assertNotNull(f);
        assertTrue(f.isAdded());
    }

    @Test
    public void testShouldAddStepFragment() throws Exception {
        controller.start().resume();

        final FragmentManager fm = activity.getSupportFragmentManager();

        getWizardUi().setStep(choose_mode);

        final Fragment f = fm.findFragmentByTag(choose_mode.getFragmentTag());
        assertNotNull(f);
        assertTrue(f.isAdded());
    }

    @Test
    public void testSetStep() throws Exception {
        getWizardUi().setStep(choose_mode);
        assertEquals(choose_mode, getWizardUi().getStep());
    }

    @Test
    public void testShouldStartWizardActivityAfterStart() throws Exception {
        final ShadowActivity shadowActivity = Shadows.shadowOf(controller.get());
        WizardUi.startWizard(activity.getWizards(), CalculatorWizards.DEFAULT_WIZARD_FLOW, shadowActivity.getApplicationContext());
        assertNotNull(shadowActivity.getNextStartedActivity());
    }

    @Test
    public void testTitleShouldBeSet() throws Exception {
        getWizardUi().setStep(choose_mode);
        assertEquals(activity.getString(choose_mode.getTitleResId()), activity.getTitle().toString());
    }

    private void setLastStep() throws IllegalAccessException {
        getWizardUi().setStep(CalculatorWizardStep.values()[CalculatorWizardStep.values().length - 1]);
    }

    private void setFirstStep() throws IllegalAccessException {
        getWizardUi().setStep(CalculatorWizardStep.values()[0]);
    }

    @Test
    public void testShouldSaveLastWizardStateOnPause() throws Exception {
        final Wizard wizard = wizards.getWizard(getWizardUi().getWizard().getName());
        assertNull(wizard.getLastSavedStepName());
        getWizardUi().setStep(CalculatorWizardStep.drag_button);
        activity.onPause();
        assertEquals(CalculatorWizardStep.drag_button.getName(), wizard.getLastSavedStepName());
    }

    @Test
    public void testShouldSaveFinishedIfLastStep() throws Exception {
        final Wizard wizard = wizards.getWizard(getWizardUi().getWizard().getName());
        assertFalse(wizard.isFinished());
        setLastStep();
        getWizardUi().finishWizard();
        assertTrue(wizard.isFinished());
    }

    @Test
    public void testShouldNotSaveFinishedIfNotLastStep() throws Exception {
        final Wizard wizard = wizards.getWizard(getWizardUi().getWizard().getName());
        assertFalse(wizard.isFinished());
        setFirstStep();
        getWizardUi().finishWizard();
        assertFalse(wizard.isFinished());
    }
}
