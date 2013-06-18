package org.solovyev.android.calculator.wizard;

import org.solovyev.android.prefs.Preference;

import javax.annotation.Nonnull;

import static org.solovyev.android.calculator.wizard.CalculatorMode.getDefaultMode;
import static org.solovyev.android.prefs.StringPreference.ofEnum;

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
		} else if(AppWizardFlow.NAME.equals(name)) {
			return new AppWizardFlow();
		} else {
			throw new IllegalArgumentException("Wizard flow " + name + " is not supported");
		}
	}

	/*
	**********************************************************************
	*
	*                           STATIC/INNER
	*
	**********************************************************************
	*/

	static final class Preferences {
		static final Preference<CalculatorMode> mode = ofEnum("mode", getDefaultMode(), CalculatorMode.class);
	}
}
