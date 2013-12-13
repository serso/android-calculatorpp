package org.solovyev.android.calculator.wizard;

import android.app.Activity;
import android.content.Context;
import org.solovyev.android.wizard.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.solovyev.android.calculator.wizard.CalculatorWizardStep.last;
import static org.solovyev.android.calculator.wizard.CalculatorWizardStep.welcome;

public class CalculatorWizards implements Wizards {

	public static final String FIRST_TIME_WIZARD = "first-wizard";
	public static final String DEFAULT_WIZARD_FLOW = "app-wizard";

	@Nonnull
	private final Context context;

	public CalculatorWizards(@Nonnull Context context) {
		this.context = context;
	}

	@Nonnull
	@Override
	public Class<? extends Activity> getActivityClassName() {
		return CalculatorWizardActivity.class;
	}

	@Nonnull
	@Override
	public Wizard getWizard(@Nullable String name) {
		if (name == null) {
			return getWizard(FIRST_TIME_WIZARD);
		}

		if (FIRST_TIME_WIZARD.equals(name)) {
			return newBaseWizard(FIRST_TIME_WIZARD, newFirstTimeWizardFlow());
		} else if (DEFAULT_WIZARD_FLOW.equals(name)) {
			return newBaseWizard(DEFAULT_WIZARD_FLOW, newDefaultWizardFlow());
		} else {
			throw new IllegalArgumentException("Wizard flow " + name + " is not supported");
		}
	}

	@Nonnull
	private BaseWizard newBaseWizard(@Nonnull String name, @Nonnull WizardFlow flow) {
		return new BaseWizard(name, context, flow);
	}

	@Nonnull
	static WizardFlow newDefaultWizardFlow() {
		final List<WizardStep> wizardSteps = new ArrayList<WizardStep>();
		for (WizardStep wizardStep : CalculatorWizardStep.values()) {
			if (wizardStep != welcome && wizardStep != last && wizardStep.isVisible()) {
				wizardSteps.add(wizardStep);
			}
		}
		return new ListWizardFlow(wizardSteps);
	}

	@Nonnull
	static WizardFlow newFirstTimeWizardFlow() {
		final List<WizardStep> wizardSteps = new ArrayList<WizardStep>();
		for (WizardStep wizardStep : CalculatorWizardStep.values()) {
			if (wizardStep.isVisible()) {
				wizardSteps.add(wizardStep);
			}
		}
		return new ListWizardFlow(wizardSteps);
	}
}
