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

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;

import org.solovyev.android.calculator.CalculatorFragment;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.R;
import org.solovyev.android.fragments.FragmentUtils;
import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 1/19/13
 * Time: 5:14 PM
 */
public class CalculatorPlotRangeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cpp_dialog);

        FragmentUtils.createFragment(this, CalculatorPlotRangeFragment.class, R.id.dialog_layout, "plot-range");
    }

    public static class CalculatorPlotRangeFragment extends CalculatorFragment {

        public CalculatorPlotRangeFragment() {
            super(CalculatorFragmentType.plotter_range);
        }

        @Override
        public void onViewCreated(@Nonnull View root, Bundle savedInstanceState) {
            super.onViewCreated(root, savedInstanceState);

            final CalculatorPlotter plotter = Locator.getInstance().getPlotter();

            final EditText xMinEditText = (EditText) root.findViewById(R.id.cpp_plot_range_x_min_editext);
            final EditText xMaxEditText = (EditText) root.findViewById(R.id.cpp_plot_range_x_max_editext);
            final EditText yMinEditText = (EditText) root.findViewById(R.id.cpp_plot_range_y_min_editext);
            final EditText yMaxEditText = (EditText) root.findViewById(R.id.cpp_plot_range_y_max_editext);

            final PlotData plotData = plotter.getPlotData();
            final PlotBoundaries boundaries = plotData.getBoundaries();

            xMinEditText.setText(String.valueOf(boundaries.getXMin()));
            xMaxEditText.setText(String.valueOf(boundaries.getXMax()));
            yMinEditText.setText(String.valueOf(boundaries.getYMin()));
            yMaxEditText.setText(String.valueOf(boundaries.getYMax()));

            root.findViewById(R.id.cpp_apply_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        final Float xMin = Float.valueOf(xMinEditText.getText().toString());
                        final Float xMax = Float.valueOf(xMaxEditText.getText().toString());
                        final Float yMin = Float.valueOf(yMinEditText.getText().toString());
                        final Float yMax = Float.valueOf(yMaxEditText.getText().toString());

                        if (xMin.equals(xMax)) {
                            throw new IllegalArgumentException();
                        }

                        if (yMin.equals(yMax)) {
                            throw new IllegalArgumentException();
                        }

                        plotter.setPlotBoundaries(PlotBoundaries.newInstance(xMin, xMax, yMin, yMax));

                        CalculatorPlotRangeFragment.this.getActivity().finish();

                    } catch (IllegalArgumentException e) {
                        if (e instanceof NumberFormatException) {
                            Locator.getInstance().getNotifier().showMessage(R.string.cpp_invalid_number, MessageType.error);
                        } else {
                            Locator.getInstance().getNotifier().showMessage(R.string.cpp_plot_boundaries_should_differ, MessageType.error);
                        }
                    }
                }
            });
        }
    }
}

