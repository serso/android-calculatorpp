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

import static org.solovyev.android.calculator.wizard.CalculatorMode.getDefaultMode;

/**
 * User: serso
 * Date: 6/16/13
 * Time: 9:59 PM
 */
public class ChooseModeWizardStep extends SherlockFragment {

	/*
	**********************************************************************
	*
	*                           CONSTANTS
	*
	**********************************************************************
	*/

	static final String MODE = "mode";

	/*
	**********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/

	@Nullable
	private Spinner modeSpinner;

	private CalculatorMode mode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(savedInstanceState != null) {
			mode = (CalculatorMode) savedInstanceState.getSerializable(MODE);
		}

		if (mode == null) {
			mode = (CalculatorMode) getArguments().getSerializable(MODE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.cpp_wizard_step_choose_mode, null);
	}

	@Override
	public void onViewCreated(View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);

		modeSpinner = (Spinner) root.findViewById(R.id.wizard_mode_spinner);

		final List<ModeListItem> listItems = new ArrayList<ModeListItem>();
		for (CalculatorMode mode : CalculatorMode.values()) {
			listItems.add(new ModeListItem(mode));
		}
		modeSpinner.setAdapter(ListItemAdapter.newInstance(getActivity(), listItems));

		final int position = Arrays.binarySearch(CalculatorMode.values(), mode);
		if (position >= 0) {
			modeSpinner.setSelection(position);
		}
	}

	@Nonnull
	CalculatorMode getSelectedMode() {
		CalculatorMode mode = getDefaultMode();

		if (modeSpinner != null) {
			final int position = modeSpinner.getSelectedItemPosition();

			if (position >= 0 && position < CalculatorMode.values().length) {
				mode = CalculatorMode.values()[position];
			}
		}

		return mode;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putSerializable(MODE, mode);
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
		private final CalculatorMode mode;

		private ModeListItem(@Nonnull CalculatorMode mode) {
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

}
