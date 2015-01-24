/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.wizard;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import org.solovyev.android.calculator.App;
import org.solovyev.android.Views;
import org.solovyev.android.calculator.CalculatorPreferences;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.solovyev.android.calculator.CalculatorApplication.getPreferences;
import static org.solovyev.android.calculator.wizard.ChooseLayoutWizardStep.LAYOUT;
import static org.solovyev.android.calculator.wizard.ChooseModeWizardStep.MODE;
import static org.solovyev.android.calculator.wizard.OnScreenCalculatorWizardStep.ONSCREEN_CALCULATOR_ENABLED;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:17 PM
 */
enum CalculatorWizardStep implements org.solovyev.android.wizard.WizardStep {

	welcome(WelcomeWizardStep.class, R.string.cpp_wizard_welcome_title, R.string.cpp_wizard_start),

	choose_layout(ChooseLayoutWizardStep.class, R.string.cpp_wizard_layout_title) {
		@Override
		public boolean onNext(@Nonnull Fragment f) {
			final ChooseLayoutWizardStep fragment = (ChooseLayoutWizardStep) f;

			final CalculatorLayout layout = fragment.getSelectedLayout();
			layout.apply(getPreferences());

			return true;
		}

		@Nullable
		@Override
		public Bundle getFragmentArgs() {
			final Bundle bundle = new Bundle();
			bundle.putSerializable(LAYOUT, CalculatorLayout.fromGuiLayout(CalculatorPreferences.Gui.layout.getPreference(getPreferences())));
			return bundle;
		}

		@Override
		public boolean isVisible() {
			return Views.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_LARGE, App.getApplication().getResources().getConfiguration());
		}
	},

	choose_mode(ChooseModeWizardStep.class, R.string.cpp_wizard_mode_title) {
		@Override
		public boolean onNext(@Nonnull Fragment f) {
			final ChooseModeWizardStep fragment = (ChooseModeWizardStep) f;

			final CalculatorMode mode = fragment.getSelectedMode();
			mode.apply(getPreferences());

			return true;
		}

		@Nullable
		@Override
		public Bundle getFragmentArgs() {
			final Bundle bundle = new Bundle();
			bundle.putSerializable(MODE, CalculatorMode.fromGuiLayout(CalculatorPreferences.Gui.layout.getPreference(getPreferences())));
			return bundle;
		}
	},

	on_screen_calculator(OnScreenCalculatorWizardStep.class, R.string.cpp_wizard_onscreen_calculator_title) {
		@Override
		public boolean onNext(@Nonnull Fragment f) {
			final OnScreenCalculatorWizardStep fragment = (OnScreenCalculatorWizardStep) f;

			CalculatorPreferences.OnscreenCalculator.showAppIcon.putPreference(getPreferences(), fragment.isOnscreenCalculatorEnabled());

			return true;
		}

		@Nullable
		@Override
		public Bundle getFragmentArgs() {
			final Bundle bundle = new Bundle();
			bundle.putSerializable(ONSCREEN_CALCULATOR_ENABLED, CalculatorPreferences.OnscreenCalculator.showAppIcon.getPreference(getPreferences()));
			return bundle;
		}
	},

	drag_button(DragButtonWizardStep.class, R.string.cpp_wizard_dragbutton_title),

	last(FinalWizardStep.class, R.string.cpp_wizard_final_title);

	@Nonnull
	private final Class<? extends Fragment> fragmentClass;

	private final int titleResId;
	private final int nextButtonTitleResId;

	CalculatorWizardStep(@Nonnull Class<? extends Fragment> fragmentClass, int titleResId) {
		this(fragmentClass, titleResId, R.string.cpp_wizard_next);
	}
	CalculatorWizardStep(@Nonnull Class<? extends Fragment> fragmentClass, int titleResId, int nextButtonTitleResId) {
		this.fragmentClass = fragmentClass;
		this.titleResId = titleResId;
		this.nextButtonTitleResId = nextButtonTitleResId;
	}

	@Nonnull
	@Override
	public String getFragmentTag() {
		return name();
	}

	@Override
	@Nonnull
	public Class<? extends Fragment> getFragmentClass() {
		return fragmentClass;
	}

	@Override
	public int getTitleResId() {
		return titleResId;
	}

	@Override
	public int getNextButtonTitleResId() {
		return nextButtonTitleResId;
	}

	@Override
	public boolean onNext(@Nonnull Fragment fragment) {
		return true;
	}

	@Override
	public boolean onPrev(@Nonnull Fragment fragment) {
		return true;
	}

	@Override
	@Nullable
	public Bundle getFragmentArgs() {
		return null;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	@Nonnull
	public String getName() {
		return name();
	}
}
