package org.solovyev.android.calculator.wizard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
			if (wizardStep != welcome && wizardStep.isVisible()) {
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
