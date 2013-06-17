package org.solovyev.android.calculator.wizard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import jscl.AngleUnit;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.solovyev.android.calculator.CalculatorPreferences;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;
import org.solovyev.android.list.ListItem;
import org.solovyev.android.list.ListItemAdapter;

import com.actionbarsherlock.app.SherlockFragment;

import static org.solovyev.android.calculator.CalculatorPreferences.Gui.Layout.main_calculator;
import static org.solovyev.android.calculator.CalculatorPreferences.Gui.Layout.simple;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:59 PM
 */
public final class ChooseModeWizardStep extends SherlockFragment implements WizardStepFragment {

	@Nullable
	private Spinner layoutSpinner;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.cpp_wizard_step_choose_mode, null);
	}

	@Override
	public void onViewCreated(View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);

		layoutSpinner = (Spinner) root.findViewById(R.id.wizard_mode_spinner);
		final List<ModeListItem> listItems = new ArrayList<ModeListItem>();
		for (Mode mode : Mode.values()) {
			listItems.add(new ModeListItem(mode));
		}
		layoutSpinner.setAdapter(ListItemAdapter.newInstance(getActivity(), listItems));
	}

	@Override
	public boolean onNext() {
		if (layoutSpinner != null) {
			final int position = layoutSpinner.getSelectedItemPosition();

			final Mode mode;
			if (position >= 0 && position < Mode.values().length) {
				mode = Mode.values()[position];
			} else {
				mode = Mode.Simple;
			}

			mode.apply(PreferenceManager.getDefaultSharedPreferences(getActivity()));
		}

		return true;
	}

	@Override
	public boolean onPrev() {
		return true;
	}

	/*
	**********************************************************************
	*
	*                           STATIC/INNER
	*
	**********************************************************************
	*/

	private static final class ModeListItem implements ListItem {

		@Nonnull
		private final Mode mode;

		private ModeListItem(@Nonnull Mode mode) {
			this.mode = mode;
		}

		@Nullable
		@Override
		public OnClickAction getOnClickAction() {
			return null;
		}

		@Nullable
		@Override
		public OnClickAction getOnLongClickAction() {
			return null;
		}

		@Nonnull
		@Override
		public View updateView(@Nonnull Context context, @Nonnull View view) {
			return build(context);
		}

		@Nonnull
		@Override
		public View build(@Nonnull Context context) {
			final TextView textView = new TextView(context);
			textView.setText(mode.getNameResId());
			return textView;
		}
	}

	private static enum Mode {
		Simple(R.string.cpp_wizard_mode_simple) {
			@Override
			protected void apply(@Nonnull SharedPreferences preferences) {
				CalculatorPreferences.Gui.layout.putPreference(preferences, simple);
				CalculatorPreferences.Calculations.preferredAngleUnits.putPreference(preferences, AngleUnit.deg);
				AndroidCalculatorEngine.Preferences.scienceNotation.putPreference(preferences, false);
				AndroidCalculatorEngine.Preferences.roundResult.putPreference(preferences, true);
			}
		},

		Engineer(R.string.cpp_wizard_mode_engineer) {
			@Override
			protected void apply(@Nonnull SharedPreferences preferences) {
				CalculatorPreferences.Gui.layout.putPreference(preferences, main_calculator);
				CalculatorPreferences.Calculations.preferredAngleUnits.putPreference(preferences, AngleUnit.rad);
				AndroidCalculatorEngine.Preferences.scienceNotation.putPreference(preferences, true);
				AndroidCalculatorEngine.Preferences.roundResult.putPreference(preferences, false);
			}
		};

		private final int nameResId;

		Mode(int nameResId) {
			this.nameResId = nameResId;
		}

		private int getNameResId() {
			return nameResId;
		}

		protected abstract void apply(@Nonnull SharedPreferences preferences);
	}
}
