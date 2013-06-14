package org.solovyev.android.calculator.plot;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.CalculatorListFragment;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.R;
import org.solovyev.android.fragments.FragmentUtils;
import org.solovyev.android.list.ListItemAdapter;

import java.util.List;

/**
 * User: serso
 * Date: 1/13/13
 * Time: 5:05 PM
 */
public class CalculatorPlotFunctionsActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.cpp_dialog);

		FragmentUtils.createFragment(this, CalculatorPlotFunctionsFragment.class, R.id.dialog_layout, "plot-functions");
	}

	public static class CalculatorPlotFunctionsFragment extends CalculatorListFragment {

		public CalculatorPlotFunctionsFragment() {
			super(CalculatorFragmentType.plotter_functions);
		}

		@Override
		public void onResume() {
			super.onResume();

			final List<PlotFunctionListItem> items = Lists.transform(Locator.getInstance().getPlotter().getFunctions(), new Function<PlotFunction, PlotFunctionListItem>() {
				@Override
				public PlotFunctionListItem apply(@javax.annotation.Nullable PlotFunction input) {
					assert input != null;
					return new PlotFunctionListItem(input);
				}
			});

			ListItemAdapter.createAndAttach(this, items);
		}
	}
}
