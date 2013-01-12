package org.solovyev.android.calculator.plot;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.CalculatorListFragment;
import org.solovyev.android.calculator.about.CalculatorFragmentType;
import org.solovyev.android.list.ListItemArrayAdapter;

import javax.annotation.Nullable;
import java.util.List;

public class CalculatorPlotFunctionsFragment extends CalculatorListFragment {

	@NotNull
	public static final String INPUT = "plot_input";

	public CalculatorPlotFunctionsFragment() {
		super(CalculatorFragmentType.plotter_functions);
	}

	@Override
	public void onResume() {
		super.onResume();

		final List<ParcelablePlotInputListItem> items = Lists.transform(CalculatorPlotFunctionsController.getInstance().getFunctions(), new Function<XyFunction, ParcelablePlotInputListItem>() {
			@Override
			public ParcelablePlotInputListItem apply(@Nullable XyFunction input) {
				return new ParcelablePlotInputListItem(input);
			}
		});

		ListItemArrayAdapter.createAndAttach(getListView(), this.getActivity(), items);
	}
}
