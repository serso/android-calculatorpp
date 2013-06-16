package org.solovyev.android.calculator.wizard;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:23 PM
 */
public final class Wizard {

	private Wizard() {
		throw new AssertionError();
	}

	@Nonnull
	public static WizardFlow getWizardFlow(@Nonnull String name) {
		if(FirstTimeWizardFlow.NAME.equals(name)) {
			return new FirstTimeWizardFlow();
		} else {
			throw new IllegalArgumentException("Wizard flow " + name + " is not supported");
		}
	}
}
