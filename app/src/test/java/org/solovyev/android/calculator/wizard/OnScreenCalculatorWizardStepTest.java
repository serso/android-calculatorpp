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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.solovyev.android.calculator.BuildConfig;
import org.solovyev.android.wizard.WizardUi;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;

@Config(sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)
public class OnScreenCalculatorWizardStepTest {

    @Nonnull
    private OnScreenCalculatorWizardStep fragment;

    @Nonnull
    private WizardActivity activity;

    @Nonnull
    private ActivityController<WizardActivity> controller;
    private Field uiField;

    @Before
    public void setUp() throws Exception {
        uiField = WizardActivity.class.getDeclaredField("wizardUi");
        uiField.setAccessible(true);

        createActivity();
        setFragment();
    }

    @Nonnull
    private WizardUi getWizardUi() throws IllegalAccessException {
        return (WizardUi) uiField.get(activity);
    }

    private void createActivity() {
        controller = Robolectric.buildActivity(WizardActivity.class).create().start().resume();
        activity = controller.get();
    }

    private void setFragment() throws IllegalAccessException {
        getWizardUi().setStep(CalculatorWizardStep.on_screen_calculator);
        activity.getSupportFragmentManager().executePendingTransactions();
        fragment = (OnScreenCalculatorWizardStep) activity.getSupportFragmentManager().findFragmentByTag(CalculatorWizardStep.on_screen_calculator.getFragmentTag());
    }

    @Test
    public void testShouldRestoreStateOnRestart() throws Exception {
        fragment.getCheckbox().setChecked(true);
        controller.restart();
        assertTrue(fragment.getCheckbox().isChecked());

        fragment.getCheckbox().setChecked(false);
        controller.restart();
        assertFalse(fragment.getCheckbox().isChecked());
    }
}
