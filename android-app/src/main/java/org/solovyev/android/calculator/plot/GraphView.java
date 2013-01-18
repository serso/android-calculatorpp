// Copyright (C) 2009-2010 Mihai Preda

package org.solovyev.android.calculator.plot;

import android.graphics.Bitmap;
import android.widget.ZoomButtonsController;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface GraphView extends ZoomButtonsController.OnZoomListener, TouchHandler.TouchHandlerListener {

    public void init(@NotNull FunctionViewDef functionViewDef);

    public void setPlotFunctions(@NotNull List<PlotFunction> plotFunctions);

    public void onPause();
    public void onResume();

	@NotNull
    public Bitmap captureScreenshot();

	void setXRange(float xMin, float xMax);

    float getXMin();

    float getXMax();

    float getYMin();

    float getYMax();

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
