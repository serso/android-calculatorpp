package org.solovyev.android.calculator.wizard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import org.solovyev.android.calculator.R;
import org.solovyev.android.list.ListItem;
import org.solovyev.android.list.ListItemAdapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.solovyev.android.calculator.wizard.CalculatorLayout.getDefaultMode;

/**
 * User: serso
 * Date: 6/19/13
 * Time: 12:33 AM
 */
final class TabletWizardStep extends SherlockFragment {

	/*
	**********************************************************************
	*
	*                           CONSTANTS
	*
	**********************************************************************
	*/

	static final String LAYOUT = "layout";

	/*
	**********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/

	@Nullable
	private Spinner layoutSpinner;

	private CalculatorLayout layout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(savedInstanceState != null) {
			layout = (CalculatorLayout) savedInstanceState.getSerializable(LAYOUT);
		}

		if (layout == null) {
			layout = (CalculatorLayout) getArguments().getSerializable(LAYOUT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.cpp_wizard_step_tablet, null);
	}

	@Override
	public void onViewCreated(View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);

		layoutSpinner = (Spinner) root.findViewById(R.id.wizard_layout_spinner);

		final List<LayoutListItem> listItems = new ArrayList<LayoutListItem>();
		for (CalculatorLayout layout : CalculatorLayout.values()) {
			listItems.add(new LayoutListItem(layout));
		}
		layoutSpinner.setAdapter(ListItemAdapter.newInstance(getActivity(), listItems));

		final int position = Arrays.binarySearch(CalculatorMode.values(), layout);
		if (position >= 0) {
			layoutSpinner.setSelection(position);
		}
	}

	@Nonnull
	CalculatorLayout getSelectedLayout() {
		CalculatorLayout layout = getDefaultMode();

		if (layoutSpinner != null) {
			final int position = layoutSpinner.getSelectedItemPosition();

			if (position >= 0 && position < CalculatorLayout.values().length) {
				layout = CalculatorLayout.values()[position];
			}
		}

		return layout;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putSerializable(LAYOUT, layout);
	}

	/*
	**********************************************************************
	*
	*                           STATIC/INNER
	*
	**********************************************************************
	*/

	private static final class LayoutListItem implements ListItem {

		@Nonnull
		private final CalculatorLayout layout;

		private LayoutListItem(@Nonnull CalculatorLayout layout) {
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
