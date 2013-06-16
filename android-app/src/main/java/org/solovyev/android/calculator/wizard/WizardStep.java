package org.solovyev.android.calculator.wizard;

import android.support.v4.app.Fragment;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:17 PM
 */
enum WizardStep {

	welcome(WelcomeWizardStep.class),
	choose_mode(ChooseModeWizardStep.class);

	@Nonnull
	private final Class<? extends Fragment> fragmentClass;

	WizardStep(@Nonnull Class<? extends Fragment> fragmentClass) {
		this.fragmentClass = fragmentClass;
	}

	public String getFragmentTag() {
		return name();
	}

	@Nonnull
	Class<? extends Fragment> getFragmentClass() {
		return fragmentClass;
	}
}
