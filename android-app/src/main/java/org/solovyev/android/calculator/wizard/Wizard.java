package org.solovyev.android.calculator.wizard;

import org.solovyev.android.prefs.Preference;

import javax.annotation.Nonnull;

import static org.solovyev.android.calculator.wizard.AppWizardFlow.newDefaultWizardFlow;
import static org.solovyev.android.calculator.wizard.AppWizardFlow.newFirstTimeWizardFlow;
import static org.solovyev.android.prefs.StringPreference.ofEnum;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:23 PM
 */
public final class Wizard {

	public static final String FIRST_TIME_WIZARD = "first-wizard";
	public static final String DEFAULT_WIZARD_FLOW = "app-wizard";

	private Wizard() {
		throw new AssertionError();
	}

	@Nonnull
	public static WizardFlow getWizardFlow(@Nonnull String name) {
		if(FIRST_TIME_WIZARD.equals(name)) {
			return newFirstTimeWizardFlow();
		} else if(DEFAULT_WIZARD_FLOW.equals(name)) {
			return newDefaultWizardFlow();
		} else {
			throw new IllegalArgumentException("Wizard flow " + name + " is not supported");
		}
	}
}
