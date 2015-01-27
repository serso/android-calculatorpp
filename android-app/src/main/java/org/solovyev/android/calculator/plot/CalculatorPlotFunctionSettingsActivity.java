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

package org.solovyev.android.calculator.plot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;

import android.support.v7.app.ActionBarActivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.solovyev.android.calculator.CalculatorFragment;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.R;
import org.solovyev.android.fragments.FragmentUtils;

public class CalculatorPlotFunctionSettingsActivity extends ActionBarActivity {

	private static final String INPUT_FUNCTION_ID = "plot-function-id";

	public static void startActivity(@Nonnull Context context, @Nonnull PlotFunction plotFunction) {
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

		/*
		**********************************************************************
		*
		*                           STATIC
		*
		**********************************************************************
		*/

		@Nullable
		private PlotFunction plotFunction;

		@Nonnull
		private final CalculatorPlotter plotter = Locator.getInstance().getPlotter();

		public CalculatorPlotFunctionSettingsFragment() {
			super(CalculatorFragmentType.plotter_function_settings);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			final String functionId = getArguments().getString(INPUT_FUNCTION_ID);
			if (functionId != null) {
				plotFunction = Locator.getInstance().getPlotter().getFunctionById(functionId);
				if (plotFunction != null) {
					getActivity().setTitle(plotFunction.getXyFunction().getExpressionString());
				}
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			return super.onCreateView(inflater, container, savedInstanceState);
		}

		@Override
		public void onViewCreated(View root, Bundle savedInstanceState) {
			super.onViewCreated(root, savedInstanceState);

			final Spinner plotLineColorSpinner = (Spinner) root.findViewById(R.id.cpp_plot_function_line_color_spinner);
			final Spinner plotLineColorTypeSpinner = (Spinner) root.findViewById(R.id.cpp_plot_function_line_color_type_spinner);
			final Spinner plotLineStyleSpinner = (Spinner) root.findViewById(R.id.cpp_plot_function_line_style_spinner);
			final SeekBar plotLineWidthSeekBar = (SeekBar) root.findViewById(R.id.cpp_plot_functions_line_width_seekbar);
			final Button okButton = (Button) root.findViewById(R.id.cpp_apply_button);

			plotLineWidthSeekBar.setMax(10);

			if (plotFunction != null) {
				plotLineWidthSeekBar.setProgress((int) plotFunction.getPlotLineDef().getLineWidth());
				plotLineColorSpinner.setSelection(PlotLineColor.valueOf(plotFunction.getPlotLineDef().getLineColor()).ordinal());
				plotLineColorTypeSpinner.setSelection(plotFunction.getPlotLineDef().getLineColorType().ordinal());
				plotLineStyleSpinner.setSelection(plotFunction.getPlotLineDef().getLineStyle().ordinal());


				okButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						PlotFunction plotFunction = CalculatorPlotFunctionSettingsFragment.this.plotFunction;

						// color
						final PlotLineColor newPlotLineColor = PlotLineColor.values()[plotLineColorSpinner.getSelectedItemPosition()];
						int newLineColor = newPlotLineColor.getColor();
						if (newLineColor != plotFunction.getPlotLineDef().getLineColor()) {
							final PlotLineDef newPlotLineDef = PlotLineDef.changeLineColor(plotFunction.getPlotLineDef(), newLineColor);
							plotFunction = PlotFunction.changePlotLineDef(plotFunction, newPlotLineDef);
						}

						// color type
						final PlotLineColorType newPlotLineColorType = PlotLineColorType.values()[plotLineColorTypeSpinner.getSelectedItemPosition()];
						if (newPlotLineColorType != CalculatorPlotFunctionSettingsFragment.this.plotFunction.getPlotLineDef().getLineColorType()) {
							final PlotLineDef newPlotLineDef = PlotLineDef.changeLineColorType(plotFunction.getPlotLineDef(), newPlotLineColorType);
							plotFunction = PlotFunction.changePlotLineDef(plotFunction, newPlotLineDef);
						}

						// line style
						final PlotLineStyle newPlotLineStyle = PlotLineStyle.values()[plotLineStyleSpinner.getSelectedItemPosition()];
						if (newPlotLineStyle != plotFunction.getPlotLineDef().getLineStyle()) {
							final PlotLineDef newPlotLineDef = PlotLineDef.changeLineStyle(plotFunction.getPlotLineDef(), newPlotLineStyle);
							plotFunction = PlotFunction.changePlotLineDef(plotFunction, newPlotLineDef);
						}

						// width

						final int newPlotLineWidth = plotLineWidthSeekBar.getProgress();
						if (((float) newPlotLineWidth) != plotFunction.getPlotLineDef().getLineWidth()) {
							final PlotLineDef newPlotLineDef = PlotLineDef.changeLineWidth(plotFunction.getPlotLineDef(), newPlotLineWidth);
							plotFunction = PlotFunction.changePlotLineDef(plotFunction, newPlotLineDef);
						}

						if (plotFunction != CalculatorPlotFunctionSettingsFragment.this.plotFunction) {
							// if has been changed

							if (plotter.updateFunction(plotFunction)) {
								CalculatorPlotFunctionSettingsFragment.this.plotFunction = plotFunction;
							}
						}

						final Activity activity = getActivity();
						if (activity != null) {
							activity.finish();
						}
					}
				});

			} else {
				plotLineWidthSeekBar.setEnabled(false);
				plotLineColorSpinner.setEnabled(false);
				plotLineColorTypeSpinner.setEnabled(false);
				plotLineStyleSpinner.setEnabled(false);
				okButton.setEnabled(false);
			}
		}
	}
}
