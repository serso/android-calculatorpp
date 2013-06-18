package org.solovyev.android.calculator.wizard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.solovyev.android.calculator.wizard.WizardStep.choose_mode;
import static org.solovyev.android.calculator.wizard.WizardStep.welcome;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:25 PM
 */
final class FirstTimeWizardFlow implements WizardFlow {

	public static final String NAME = "first-wizard";

	@Nonnull
	private final ListWizardFlow listWizardFlow;

	FirstTimeWizardFlow() {
		final List<WizardStep> wizardSteps = new ArrayList<WizardStep>();
		for (WizardStep wizardStep : WizardStep.values()) {
			wizardSteps.add(wizardStep);
		}
		this.listWizardFlow = new ListWizardFlow(NAME, wizardSteps);
	}

	@Nonnull
	@Override
	public String getName() {
		return listWizardFlow.getName();
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
