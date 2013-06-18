package org.solovyev.android.calculator.wizard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import org.solovyev.android.calculator.CalculatorApplication;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.solovyev.android.calculator.wizard.ChooseModeWizardStep.MODE;
import static org.solovyev.android.calculator.wizard.Wizard.Preferences;

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

		@Nullable
		@Override
		Bundle getFragmentArgs() {
			return null;
		}
	},

	choose_mode(ChooseModeWizardStep.class) {
		@Override
		boolean onNext(@Nonnull Fragment f) {
			final ChooseModeWizardStep fragment = (ChooseModeWizardStep) f;

			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(f.getActivity());

			final CalculatorMode mode = fragment.getSelectedMode();
			mode.apply(preferences);
			Preferences.mode.putPreference(preferences, mode);

			return true;
		}

		@Override
		boolean onPrev(@Nonnull Fragment fragment) {
			return true;
		}

		@Nullable
		@Override
		Bundle getFragmentArgs() {
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CalculatorApplication.getInstance());

			final Bundle bundle = new Bundle();
			bundle.putSerializable(MODE, Preferences.mode.getPreference(preferences));
			return bundle;
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

	@Nullable
	abstract Bundle getFragmentArgs();
}
