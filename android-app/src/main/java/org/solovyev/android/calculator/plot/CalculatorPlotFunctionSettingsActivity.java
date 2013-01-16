package org.solovyev.android.calculator.plot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
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

		/*
		**********************************************************************
		*
		*                           STATIC
		*
		**********************************************************************
		*/

		public static final int LINE_WIDTH_DELAY = 1000;
		public static final int MESSAGE_ID = 1;

		@Nullable
		private PlotFunction plotFunction;

		@NotNull
		private final CalculatorPlotter plotter = Locator.getInstance().getPlotter();

		@NotNull
		private final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (plotFunction != null) {
					switch (msg.what) {
						case MESSAGE_ID:
							final PlotLineDef newPlotLineDef = PlotLineDef.changeLineWidth(plotFunction.getPlotLineDef(), msg.arg1);
							final PlotFunction newPlotFunction = PlotFunction.changePlotLineDef(plotFunction, newPlotLineDef);
							if(plotter.updateFunction(newPlotFunction)) {
								plotFunction = newPlotFunction;
							}
							break;
					}
				}
			}
		};

		public CalculatorPlotFunctionSettingsFragment() {
			super(CalculatorFragmentType.plotter_function_settings);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			final String functionId = getArguments().getString(INPUT_FUNCTION_ID);
			if (functionId != null) {
				plotFunction = Locator.getInstance().getPlotter().getFunctionById(functionId);
				if ( plotFunction != null ) {
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

			final Spinner plotLineColorSpinner = (Spinner)root.findViewById(R.id.cpp_plot_function_line_color_spinner);
			final Spinner plotLineColorTypeSpinner = (Spinner)root.findViewById(R.id.cpp_plot_function_line_color_type_spinner);
			final Spinner plotLineStyleSpinner = (Spinner)root.findViewById(R.id.cpp_plot_function_line_style_spinner);
			final SeekBar plotLineWidthSeekBar = (SeekBar)root.findViewById(R.id.cpp_plot_functions_line_width_seekbar);
			final Button okButton = (Button)root.findViewById(R.id.cpp_ok_button);

			plotLineWidthSeekBar.setMax(10);

			if (plotFunction != null) {
				plotLineWidthSeekBar.setProgress((int)plotFunction.getPlotLineDef().getLineWidth());

				plotLineColorSpinner.setSelection(PlotLineColor.valueOf(plotFunction.getPlotLineDef().getLineColor()).ordinal());
				plotLineColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						final PlotLineColor newPlotLineColor = PlotLineColor.values()[position];
						int newLineColor = newPlotLineColor.getColor();
						if ( newLineColor != plotFunction.getPlotLineDef().getLineColor() ) {
							final PlotLineDef newPlotLineDef = PlotLineDef.changeLineColor(plotFunction.getPlotLineDef(), newLineColor);
							final PlotFunction newPlotFunction = PlotFunction.changePlotLineDef(plotFunction, newPlotLineDef);
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
							final PlotLineDef newPlotLineDef = PlotLineDef.changeLineColorType(plotFunction.getPlotLineDef(), newPlotLineColorType);
							final PlotFunction newPlotFunction = PlotFunction.changePlotLineDef(plotFunction, newPlotLineDef);
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

							final PlotLineDef newPlotLineDef = PlotLineDef.changeLineStyle(plotFunction.getPlotLineDef(), newPlotLineStyle);
							final PlotFunction newPlotFunction = PlotFunction.changePlotLineDef(plotFunction, newPlotLineDef);

							if(plotter.updateFunction(newPlotFunction)) {
								plotFunction = newPlotFunction;
							}
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

				plotLineWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						// remove old messages
						handler.removeMessages(MESSAGE_ID);

						// send new message
						handler.sendMessageDelayed(Message.obtain(handler, MESSAGE_ID, progress, 0), LINE_WIDTH_DELAY);
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
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
				plotLineWidthSeekBar.setEnabled(false);
				plotLineColorSpinner.setEnabled(false);
				plotLineColorTypeSpinner.setEnabled(false);
				plotLineStyleSpinner.setEnabled(false);
				okButton.setEnabled(false);
			}
		}
	}
}
