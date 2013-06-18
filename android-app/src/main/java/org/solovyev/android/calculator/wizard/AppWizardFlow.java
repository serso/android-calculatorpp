package org.solovyev.android.calculator.wizard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.solovyev.android.calculator.wizard.WizardStep.welcome;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:25 PM
 */
final class AppWizardFlow implements WizardFlow {

	public static final String NAME = "app-wizard";

	@Nonnull
	private final ListWizardFlow listWizardFlow;

	AppWizardFlow() {
		final List<WizardStep> wizardSteps = new ArrayList<WizardStep>();
		for (WizardStep wizardStep : WizardStep.values()) {
			if (wizardStep != welcome) {
				wizardSteps.add(wizardStep);
			}
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
