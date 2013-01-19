package org.solovyev.android.calculator.plot;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.CalculatorListFragment;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.R;
import org.solovyev.android.fragments.FragmentUtils;
import org.solovyev.common.msg.MessageType;

/**
 * User: serso
 * Date: 1/19/13
 * Time: 5:14 PM
 */
public class CalculatorPlotRangeActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cpp_dialog);

        FragmentUtils.createFragment(this, CalculatorPlotRangeFragment.class, R.id.dialog_layout, "plot-range");
    }

    public static class CalculatorPlotRangeFragment extends CalculatorListFragment {

        public CalculatorPlotRangeFragment() {
            super(CalculatorFragmentType.plotter_range);
        }

        @Override
        public void onViewCreated(@NotNull View root, Bundle savedInstanceState) {
            super.onViewCreated(root, savedInstanceState);

            final CalculatorPlotter plotter = Locator.getInstance().getPlotter();

            final EditText xMinEditText = (EditText) root.findViewById(R.id.cpp_plot_range_x_min_editext);
            final EditText xMaxEditText = (EditText) root.findViewById(R.id.cpp_plot_range_x_max_editext);

            final PlotData plotData = plotter.getPlotData();
            final PlotBoundaries boundaries = plotData.getBoundaries();

            xMinEditText.setText(String.valueOf(boundaries.getXMin()));
            xMaxEditText.setText(String.valueOf(boundaries.getXMax()));

            xMinEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        final Float newXMin = Float.valueOf(s.toString());
                        plotter.setPlotBoundaries(PlotBoundaries.newInstance(newXMin, boundaries.getXMax()));
                    } catch (NumberFormatException e) {
                        Locator.getInstance().getNotifier().showMessage(R.string.cpp_invalid_number, MessageType.error);
                        xMinEditText.setText(String.valueOf(boundaries.getXMin()));
                    }

                }
            });

            xMaxEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        final Float newXMax = Float.valueOf(s.toString());
                        plotter.setPlotBoundaries(PlotBoundaries.newInstance(boundaries.getXMin(), newXMax));
                    } catch (NumberFormatException e) {
                        Locator.getInstance().getNotifier().showMessage(R.string.cpp_invalid_number, MessageType.error);
                        xMaxEditText.setText(String.valueOf(boundaries.getXMax()));
                    }
                }
            });

            root.findViewById(R.id.cpp_ok_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CalculatorPlotRangeFragment.this.getActivity().finish();
                }
            });
        }
    }
}

