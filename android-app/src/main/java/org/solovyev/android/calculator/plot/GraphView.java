// Copyright (C) 2009-2010 Mihai Preda

package org.solovyev.android.calculator.plot;

import android.widget.ZoomButtonsController;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface GraphView extends ZoomButtonsController.OnZoomListener, TouchHandler.TouchHandlerInterface {

    static final String SCREENSHOT_DIR = "/screenshots";

    public void init(@NotNull FunctionViewDef functionViewDef);

    public void setFunctionPlotDefs(@NotNull List<ArityPlotFunction> functionPlotDefs);

    public void onPause();
    public void onResume();

    public String captureScreenshot();

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
