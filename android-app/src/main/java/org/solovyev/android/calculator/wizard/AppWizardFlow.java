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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static org.solovyev.android.calculator.wizard.WizardStep.last;
import static org.solovyev.android.calculator.wizard.Wizards.DEFAULT_WIZARD_FLOW;
import static org.solovyev.android.calculator.wizard.Wizards.FIRST_TIME_WIZARD;
import static org.solovyev.android.calculator.wizard.WizardStep.welcome;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:25 PM
 */
final class AppWizardFlow implements WizardFlow {

	@Nonnull
	private final ListWizardFlow listWizardFlow;

	private AppWizardFlow(@Nonnull String name, @Nonnull List<WizardStep> wizardSteps) {
		this.listWizardFlow = new ListWizardFlow(name, wizardSteps);
	}

	@Nonnull
	static AppWizardFlow newDefaultWizardFlow() {
		final List<WizardStep> wizardSteps = new ArrayList<WizardStep>();
		for (WizardStep wizardStep : WizardStep.values()) {
			if (wizardStep != welcome && wizardStep != last && wizardStep.isVisible()) {
				wizardSteps.add(wizardStep);
			}
		}
		return new AppWizardFlow(DEFAULT_WIZARD_FLOW, wizardSteps);
	}

	@Nonnull
	static AppWizardFlow newFirstTimeWizardFlow() {
		final List<WizardStep> wizardSteps = new ArrayList<WizardStep>();
		for (WizardStep wizardStep : WizardStep.values()) {
			if (wizardStep.isVisible()) {
				wizardSteps.add(wizardStep);
			}
		}
		return new AppWizardFlow(FIRST_TIME_WIZARD, wizardSteps);
	}


	@Nonnull
	@Override
	public String getName() {
		return listWizardFlow.getName();
	}

	@Nullable
	@Override
	public WizardStep getStep(@Nonnull String name) {
		return listWizardFlow.getStep(name);
	}

	@Nullable
	@Override
	public WizardStep getNextStep(@Nonnull WizardStep step) {
		return listWizardFlow.getNextStep(step);
	}

	@Nullable
	@Override
	public WizardStep getPrevStep(@Nonnull WizardStep step) {
		return listWizardFlow.getPrevStep(step);
	}

	@Nonnull
	@Override
	public WizardStep getFirstStep() {
		return listWizardFlow.getFirstStep();
	}
}
