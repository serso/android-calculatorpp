package org.solovyev.android.calculator.plot;

import android.graphics.Bitmap;
import android.widget.ZoomButtonsController;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface GraphView extends ZoomButtonsController.OnZoomListener, TouchHandler.TouchHandlerListener {

    public void init(@NotNull PlotViewDef plotViewDef);

    public void setPlotFunctions(@NotNull List<PlotFunction> plotFunctions);

    @NotNull
    public List<PlotFunction> getPlotFunctions();

    public void onDestroy();
    public void onPause();
    public void onResume();

	@NotNull
    public Bitmap captureScreenshot();

	void setXRange(float xMin, float xMax);
	void setYRange(float yMin, float yMax);

    float getXMin();

    float getXMax();

    float getYMin();

    float getYMax();

    void invalidateGraphs();

/*	void increaseDensity();
	void decreaseDensity();*/

    /*
    **********************************************************************
    *
    *                           CUSTOMIZATION
    *
    **********************************************************************
    */

/*    void setBgColor(int color);
    void setAxisColor(int color);*/
}
