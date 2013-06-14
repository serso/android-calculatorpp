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
		if (graphView instanceof CalculatorGraph2dView) {
			return PlotBoundaries.newInstance(graphView.getXMin(), graphView.getXMax(), graphView.getYMin(), graphView.getYMax());
		} else {
			return null;
		}
	}

	@Override
	protected void createGraphicalView(@NotNull View root, @NotNull PlotData plotData) {

		// remove old
		final ViewGroup graphContainer = (ViewGroup) root.findViewById(R.id.main_fragment_layout);

		if (graphView instanceof View) {
			graphView.onDestroy();
			graphContainer.removeView((View) graphView);
		}

		if (plotData.isPlot3d()) {
			graphView = new CalculatorGraph3dView(getActivity());
		} else {
			graphView = new CalculatorGraph2dView(getActivity());
		}

		graphView.init(PlotViewDef.newInstance(Color.WHITE, Color.WHITE, Color.DKGRAY, getBgColor()));

		final PlotBoundaries boundaries = plotData.getBoundaries();
		graphView.setXRange(boundaries.getXMin(), boundaries.getXMax());
		graphView.setYRange(boundaries.getYMin(), boundaries.getYMax());

		graphView.setPlotFunctions(plotData.getFunctions());

		if (graphView instanceof View) {
			graphContainer.addView((View) graphView);
		}
	}

	@Override
	protected void createChart(@NotNull PlotData plotData) {
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
