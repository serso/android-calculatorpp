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

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 12/30/12
 * Time: 4:43 PM
 */
public class CalculatorPlotFragment extends AbstractCalculatorPlotFragment {

    @Nullable
    private GraphView graphView;

    @Nullable
    @Override
    protected PlotBoundaries getPlotBoundaries() {
        if (graphView instanceof CalculatorGraph2dView) {
            return PlotBoundaries.newInstance(graphView.getXMin(), graphView.getXMax(), graphView.getYMin(), graphView.getYMax());
        } else {
            return null;
        }
    }

    @Override
    protected void createGraphicalView(@Nonnull View root, @Nonnull PlotData plotData) {

        // remove old
        final ViewGroup graphContainer = (ViewGroup) root.findViewById(R.id.main_fragment_layout);

        if (graphView instanceof View) {
            graphView.onDestroy();
            graphContainer.removeView((View) graphView);
        }

        final boolean d3 = plotData.isPlot3d();
        if (d3) {
            graphView = new CalculatorGraph3dView(getActivity());
        } else {
            graphView = new CalculatorGraph2dView(getActivity());
        }

        final int color = App.getTheme().getTextColorFor(getActivity()).normal;
        graphView.init(PlotViewDef.newInstance(color, color, Color.DKGRAY, getBgColor(d3)));

        final PlotBoundaries boundaries = plotData.getBoundaries();
        graphView.setXRange(boundaries.getXMin(), boundaries.getXMax());
        graphView.setYRange(boundaries.getYMin(), boundaries.getYMax());
        graphView.setAdjustYAxis(plotData.isAdjustYAxis());

        graphView.setPlotFunctions(plotData.getFunctions());

        if (graphView instanceof View) {
            graphContainer.addView((View) graphView);
        }
    }

    @Override
    protected void createChart(@Nonnull PlotData plotData) {
    }

    @Override
    protected boolean isScreenshotSupported() {
        return true;
    }

    @Nonnull
    @Override
    protected Bitmap getScreehshot() {
        if (this.graphView == null) throw new AssertionError();
        return this.graphView.captureScreenshot();
    }

    @Override
    protected boolean is3dPlotSupported() {
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (this.graphView != null) {
            this.graphView.onResume();
        }
    }

    @Override
    protected void onError() {
        final View root = getView();
        if (root != null && graphView instanceof View) {
            final ViewGroup graphContainer = (ViewGroup) root.findViewById(R.id.main_fragment_layout);
            graphContainer.removeView((View) graphView);
        }
        this.graphView = null;
    }

    @Override
    public void onPause() {
        if (this.graphView != null) {
            this.graphView.onPause();
        }

        super.onPause();
    }


    @Override
    public void onDestroyView() {
        if (this.graphView != null) {
            this.graphView.onDestroy();
        }

        super.onDestroyView();
    }

}
