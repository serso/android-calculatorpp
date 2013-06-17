package org.solovyev.android.calculator.wizard;

import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:17 PM
 */
enum WizardStep {

	welcome(WelcomeWizardStep.class) {
		@Override
		boolean onNext(@Nonnull Fragment fragment) {
			return true;
		}

		@Override
		boolean onPrev(@Nonnull Fragment fragment) {
			return true;
		}
	},

	choose_mode(ChooseModeWizardStep.class) {
		@Override
		boolean onNext(@Nonnull Fragment f) {
			final ChooseModeWizardStep fragment = (ChooseModeWizardStep) f;

			final CalculatorMode mode = fragment.getSelectedMode();
			mode.apply(PreferenceManager.getDefaultSharedPreferences(f.getActivity()));

			return true;
		}

		@Override
		boolean onPrev(@Nonnull Fragment fragment) {
			return true;
		}
	};

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

	abstract boolean onNext(@Nonnull Fragment fragment);
	abstract boolean onPrev(@Nonnull Fragment fragment);
}
