package org.solovyev.android.calculator.plot;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.R;

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
        if ( graphView != null ) {
            return PlotBoundaries.newInstance(graphView.getXMin(), graphView.getXMax(), graphView.getYMin(), graphView.getYMax());
        } else {
            return null;
        }
    }

    @Override
    protected void createGraphicalView(@NotNull View root, @NotNull PlotData plotData, @NotNull PlotBoundaries plotBoundaries) {

        // remove old
        final ViewGroup graphContainer = (ViewGroup) root.findViewById(R.id.main_fragment_layout);

        if (graphView instanceof View) {
            graphContainer.removeView((View) graphView);
        }

        if ( plotData.isPlot3d() ) {
            graphView = new Graph3dView(getActivity());
        } else {
            graphView = new CalculatorGraph2dView(getActivity());
        }

        graphView.init(FunctionViewDef.newInstance(Color.WHITE, Color.WHITE, Color.DKGRAY, getBgColor()));
        //graphView.setXRange((float)plotBoundaries.getXMin(), (float)plotBoundaries.getXMax());
        graphView.setPlotFunctions(plotData.getFunctions());

        graphContainer.addView((View) graphView);
    }

    @Override
    protected void createChart(@NotNull PlotData plotData, @NotNull PlotBoundaries plotBoundaries) {
    }

	@Override
	protected boolean isScreenshotSupported() {
		return true;
	}

	@NotNull
	@Override
	protected Bitmap getScreehshot() {
		assert this.graphView != null;
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
        super.onPause();
        if (this.graphView != null) {
            this.graphView.onPause();
        }
    }

    /*
    **********************************************************************
    *
    *                           STATIC
    *
    **********************************************************************
    */

}
