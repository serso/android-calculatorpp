package org.solovyev.android.calculator.plot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorFragment;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.R;
import org.solovyev.android.fragments.FragmentUtils;

public class CalculatorPlotFunctionSettingsActivity extends SherlockFragmentActivity {

	private static final String INPUT_FUNCTION_ID = "plot-function-id";

	public static void startActivity(@NotNull Context context, @NotNull PlotFunction plotFunction) {
		final Intent intent = new Intent(context, CalculatorPlotFunctionSettingsActivity.class);
		intent.putExtra(INPUT_FUNCTION_ID, plotFunction.getXyFunction().getId());
		context.startActivity(intent);

	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.cpp_plot_function_settings_dialog);

		final Intent intent = getIntent();

		if (intent != null) {
			final String plotFunctionId = intent.getStringExtra(INPUT_FUNCTION_ID);

			if (plotFunctionId != null) {
				final Bundle parameters = new Bundle();
				parameters.putString(INPUT_FUNCTION_ID, plotFunctionId);
				FragmentUtils.createFragment(this, CalculatorPlotFunctionSettingsFragment.class, R.id.dialog_layout, "plot-function-settings", parameters);
			} else {
				finish();
			}
		} else {
			finish();
		}
	}

	public static class CalculatorPlotFunctionSettingsFragment extends CalculatorFragment {

		@Nullable
		private PlotFunction plotFunction;

		public CalculatorPlotFunctionSettingsFragment() {
			super(CalculatorFragmentType.plotter_function_settings);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			final String functionId = getArguments().getString(INPUT_FUNCTION_ID);
			if (functionId != null) {
				plotFunction = Locator.getInstance().getPlotter().getFunctionById(functionId);
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			return super.onCreateView(inflater, container, savedInstanceState);
		}

		@Override
		public void onViewCreated(View root, Bundle savedInstanceState) {
			super.onViewCreated(root, savedInstanceState);

			final CalculatorPlotter plotter = Locator.getInstance().getPlotter();

			final Spinner plotLineColorSpinner = (Spinner)root.findViewById(R.id.cpp_plot_function_line_color_spinner);
			final Spinner plotLineColorTypeSpinner = (Spinner)root.findViewById(R.id.cpp_plot_function_line_color_type_spinner);
			final Spinner plotLineStyleSpinner = (Spinner)root.findViewById(R.id.cpp_plot_function_line_style_spinner);
			final Button okButton = (Button)root.findViewById(R.id.cpp_ok_button);

			if (plotFunction != null) {

				plotLineColorSpinner.setSelection(PlotLineColor.valueOf(plotFunction.getPlotLineDef().getLineColor()).ordinal());
				plotLineColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						final PlotLineColor newPlotLineColor = PlotLineColor.values()[position];
						int newLineColor = newPlotLineColor.getColor();
						if ( newLineColor != plotFunction.getPlotLineDef().getLineColor() ) {
							final PlotFunction newPlotFunction = new PlotFunction(plotFunction.getXyFunction(), PlotLineDef.changeLineColor(plotFunction.getPlotLineDef(), newLineColor));
							if(plotter.updateFunction(newPlotFunction)) {
								plotFunction = newPlotFunction;
							}
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

				plotLineColorTypeSpinner.setSelection(plotFunction.getPlotLineDef().getLineColorType().ordinal());
				plotLineColorTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						final PlotLineColorType newPlotLineColorType = PlotLineColorType.values()[position];
						if ( newPlotLineColorType != plotFunction.getPlotLineDef().getLineColorType() ) {
							final PlotFunction newPlotFunction = new PlotFunction(plotFunction.getXyFunction(), PlotLineDef.changeLineColorType(plotFunction.getPlotLineDef(), newPlotLineColorType));
							if(plotter.updateFunction(newPlotFunction)) {
								plotFunction = newPlotFunction;
							}
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

				plotLineStyleSpinner.setSelection(plotFunction.getPlotLineDef().getLineStyle().ordinal());
				plotLineStyleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						final PlotLineStyle newPlotLineStyle = PlotLineStyle.values()[position];
						if ( newPlotLineStyle != plotFunction.getPlotLineDef().getLineStyle() ) {
							final PlotFunction newPlotFunction = new PlotFunction(plotFunction.getXyFunction(), PlotLineDef.changeLineStyle(plotFunction.getPlotLineDef(), newPlotLineStyle));
							if(plotter.updateFunction(newPlotFunction)) {
								plotFunction = newPlotFunction;
							}
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

				okButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						final Activity activity = getActivity();
						if ( activity != null ) {
							activity.finish();
						}
					}
				});

			} else {
				plotLineColorSpinner.setEnabled(false);
				plotLineColorTypeSpinner.setEnabled(false);
				plotLineStyleSpinner.setEnabled(false);
				okButton.setEnabled(false);
			}
		}
	}
}
