// Copyright (C) 2009-2010 Mihai Preda

package arity.calculator;

import org.javia.arity.Function;

public interface GraphView {

    static final String SCREENSHOT_DIR = "/screenshots";

    public void setFunction(Function f);

    public void onPause();
    public void onResume();

    public String captureScreenshot();

    void setId(int id);

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
