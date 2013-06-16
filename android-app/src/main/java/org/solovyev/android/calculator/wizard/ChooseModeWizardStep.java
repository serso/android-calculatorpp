package org.solovyev.android.calculator.wizard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import org.solovyev.android.calculator.CalculatorPreferences;
import org.solovyev.android.calculator.R;
import org.solovyev.android.list.ListItem;
import org.solovyev.android.list.ListItemAdapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.solovyev.android.calculator.CalculatorPreferences.Gui.Layout.main_calculator;
import static org.solovyev.android.calculator.CalculatorPreferences.Gui.Layout.main_calculator_mobile;
import static org.solovyev.android.calculator.CalculatorPreferences.Gui.Layout.simple;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:59 PM
 */
public final class ChooseModeWizardStep extends SherlockFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.cpp_wizard_step_choose_mode, null);
	}

	@Override
	public void onViewCreated(View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);

		final Spinner layoutSpinner = (Spinner) root.findViewById(R.id.wizard_mode_spinner);
		final List<ModeListItem> listItems = new ArrayList<ModeListItem>();
		listItems.add(new ModeListItem(main_calculator));
		listItems.add(new ModeListItem(main_calculator_mobile));
		listItems.add(new ModeListItem(simple));
		final ListItemAdapter<ModeListItem> adapter = ListItemAdapter.newInstance(getActivity(), listItems);
		layoutSpinner.setAdapter(adapter);
		layoutSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
				final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

				final ModeListItem item = adapter.getItem(position);
				CalculatorPreferences.Gui.layout.putPreference(preferences, item.layout);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
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
		private final CalculatorPreferences.Gui.Layout layout;

		private ModeListItem(@Nonnull CalculatorPreferences.Gui.Layout layout) {
			this.layout = layout;
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
			textView.setText(layout.getNameResId());
			return textView;
		}
	}
}
