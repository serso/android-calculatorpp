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

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Spinner;
import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static org.solovyev.android.calculator.CalculatorApplication.getPreferences;

public class ChooseThemeWizardStep extends WizardFragment implements AdapterView.OnItemSelectedListener {

	private FrameLayout preview;
	@Nonnull
	private final List<ThemeUi> themes = new ArrayList<>();
	private WizardArrayAdapter<ThemeUi> adapter;

	@Override
	protected int getViewResId() {
		return R.layout.cpp_wizard_step_choose_theme;
	}

	@Override
	public void onViewCreated(View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);

		final Preferences.Gui.Theme theme = Preferences.Gui.getTheme(getPreferences());
		final Spinner spinner = (Spinner) root.findViewById(R.id.wizard_theme_spinner);
		themes.clear();
		themes.add(new ThemeUi(Preferences.Gui.Theme.material_theme, R.string.p_material_theme));
		themes.add(new ThemeUi(Preferences.Gui.Theme.material_light_theme, R.string.p_material_light_theme));
		themes.add(new ThemeUi(Preferences.Gui.Theme.metro_blue_theme, R.string.p_metro_blue_theme));
		themes.add(new ThemeUi(Preferences.Gui.Theme.metro_green_theme, R.string.p_metro_green_theme));
		themes.add(new ThemeUi(Preferences.Gui.Theme.metro_purple_theme, R.string.p_metro_purple_theme));
		adapter = new WizardArrayAdapter<>(getActivity(), themes);
		spinner.setAdapter(adapter);
		spinner.setSelection(findPosition(theme));
		spinner.setOnItemSelectedListener(this);

		preview = (FrameLayout) root.findViewById(R.id.wizard_theme_preview);
		updateImage(theme);
	}

	private int findPosition(@Nonnull Preferences.Gui.Theme theme) {
		for (int i = 0; i < themes.size(); i++) {
			if (theme.equals(themes.get(i).theme)) {
				return i;
			}

		}
		return 0;
	}

	private void updateImage(@Nonnull Preferences.Gui.Theme theme) {
		preview.removeAllViews();
		final ContextThemeWrapper context = new ContextThemeWrapper(getActivity(), theme.getThemeId());
		LayoutInflater.from(context).inflate(R.layout.cpp_wizard_step_choose_theme_preview, preview);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		final ThemeUi theme = adapter.getItem(position);
		Preferences.Gui.theme.putPreference(getPreferences(), theme.theme);
		updateImage(theme.theme);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	private final class ThemeUi {
		@Nonnull
		final Preferences.Gui.Theme theme;
		@Nonnull
		final String name;

		public ThemeUi(@Nonnull Preferences.Gui.Theme theme, @StringRes int name) {
			this.theme = theme;
			this.name = getString(name);
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
