package org.solovyev.android.calculator.plot;

import android.view.View;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.math.Point2d;

/**
 * User: serso
 * Date: 1/18/13
 * Time: 7:59 PM
 */
public class Graph2dDimensions {

    //                    |<--------------gWidth-------------->|
    //                   xMin                                xMax
    // -------------------|------------------------------------|--------------------
    //                    |<-------------vWidthPs------------->|
    //
    /*
    *
    *
    *                     0------------------------------------|--> xPxs
    *                     |
    *                     |
    *                     |                  y
    *                     |                  ^
    *                     |                  |
    *                     |                  |
    *                     |                  |
    *                     |------------------0-----------------|--> x
    *                     |                  |
    *                     |                  |
    *                     |                  |
    *                     |                  |
    *                     |                  |
    *                     v
    *                    yPxs
    *
    * */


    @NotNull
    private GraphView graphView;

    // view width and height in pixels
    private int vWidthPxs;
    private int vHeightPxs;

    // current position of camera in graph coordinates
    private float x0;
    private float y0;

    // graph width in function units (NOT screen pixels)
    private float gWidth = 20;

    public Graph2dDimensions(@NotNull GraphView graphView) {
        this.graphView = graphView;
    }

    /*
    **********************************************************************
    *
    *                           METHODS
    *
    **********************************************************************
    */

    @NotNull
    Point2d toGraphCoordinates(float xPxs, float yPxs) {
        return new Point2d( scalePxs(xPxs) + getXMin(), (getGraphHeight() - scalePxs(yPxs)) + getYMin() );
    }

    private float scalePxs(float pxs) {
        return pxs * getGraphToViewRatio();
    }

    // X

    public float getXMin() {
        return x0 - gWidth / 2;
    }

    float getXMax(float minX) {
        return minX + gWidth;
    }

    public float getXMax() {
        return getXMax(getXMin());
    }

    // Y

    public float getYMin() {
        return getYMin(getGraphHeight());
    }


    public float getYMin(float graphHeight) {
        return y0 - graphHeight / 2;
    }

    public float getYMax() {
        final float graphHeight = getGraphHeight();
        return getYMax(graphHeight, getYMin(graphHeight));
    }

    public float getYMax(float graphHeight, float yMin) {
        return yMin + graphHeight;
    }

    float getGraphHeight() {
        return gWidth * getAspectRatio();
    }

    float getGraphToViewRatio() {
        if (vWidthPxs != 0) {
            return gWidth / ((float)vWidthPxs);
        } else {
            return 0f;
        }
    }

    private float getAspectRatio() {
        if (vWidthPxs != 0) {
            return ((float) vHeightPxs) / vWidthPxs;
        } else {
            return 0f;
        }
    }

    public int getVWidthPxs() {
        return vWidthPxs;
    }

    public int getVHeightPxs() {
        return vHeightPxs;
    }

    public float getX0() {
        return x0;
    }

    public float getY0() {
        return y0;
    }

    public float getGWidth() {
        return gWidth;
    }

    /*
    **********************************************************************
    *
    *                           SETTERS
    *
    **********************************************************************
    */

    public void setXRange(float xMin, float xMax) {
        this.gWidth = xMax - xMin;
        this.x0 = xMin + gWidth / 2;
        this.y0 = 0;

        this.graphView.invalidateGraphs();
    }

    public void setViewDimensions(@NotNull View view) {
        this.vWidthPxs = view.getWidth();
        this.vHeightPxs = view.getHeight();

        this.graphView.invalidateGraphs();
    }


    public void setGWidth(float gWidth) {
        this.gWidth = gWidth;

        this.graphView.invalidateGraphs();
    }

    public void setViewDimensions(int vWidthPxs, int vHeightPxs) {
        this.vWidthPxs = vWidthPxs;
        this.vHeightPxs = vHeightPxs;

        this.graphView.invalidateGraphs();
    }

    void setXY(float x0, float y0) {
        this.x0 = x0;
        this.y0 = y0;
    }

    public void increaseXY(float dx, float dy) {
        this.x0 += dx;
        this.y0 += dy;
    }

    @NotNull
    public Graph2dDimensions copy() {
        final Graph2dDimensions copy = new Graph2dDimensions(this.graphView);

        copy.vWidthPxs = this.vWidthPxs;
        copy.vHeightPxs = this.vHeightPxs;
        copy.x0 = this.x0;
        copy.y0 = this.y0;
        copy.gWidth = this.gWidth;

        return copy;
    }
}
