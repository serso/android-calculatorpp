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
	//                    |<-------------vWidthPxs------------>|
	//
	/*
	*
	*
	*        yMax   ------0------------------------------------|--> xPxs
	*               ^     |
	*               |     |
	*               v     |                  y
	*               H     |                  ^
	*               e     |                  |
	*               i     |                  |
	*               g     |                  |
	*               h     |------------------0-----------------|--> x
	*               t     |                  |
	*               |     |                  |
	*               |     |                  |
	*               v     |                  |
	*        yMin   -------                  -
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

	// graph width and height in function units (NOT screen pixels)
	private float gWidth = 20;
	private float gHeight = 20;

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
		return new Point2d(scaleXPxs(xPxs) + getXMin(), (getGHeight() - scaleYPxs(yPxs)) + getYMin());
	}

	private float scaleXPxs(float pxs) {
		return pxs * getXGraphToViewScale();
	}

	private float scaleYPxs(float pxs) {
		return pxs * getYGraphToViewScale();
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
		return y0 - gHeight / 2;
	}

	public float getYMax() {
		return getYMax(getYMin());
	}

	public float getYMax(float yMin) {
		return yMin + gHeight;
	}

	float getXGraphToViewScale() {
		if (vWidthPxs != 0) {
			return gWidth / ((float) vWidthPxs);
		} else {
			return 0f;
		}
	}

	float getYGraphToViewScale() {
		if (vHeightPxs != 0) {
			return gHeight / ((float) vHeightPxs);
		} else {
			return 0f;
		}
	}

	private float getViewAspectRatio() {
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

	float getGHeight() {
		return gHeight;
	}

	/*
	**********************************************************************
	*
	*                           SETTERS
	*
	**********************************************************************
	*/

	public void setXRange(float xMin, float xMax) {
		setXRange0(xMin, xMax);

		this.graphView.invalidateGraphs();
	}

	private void setXRange0(float xMin, float xMax) {
		this.gWidth = xMax - xMin;
		this.x0 = xMin + gWidth / 2;
	}

	public void setYRange(float yMin, float yMax) {
		setYRange0(yMin, yMax);

		this.graphView.invalidateGraphs();
	}

	private void setYRange0(float yMin, float yMax) {
		this.gHeight = yMax - yMin;
		this.y0 = yMin + gHeight / 2;
	}

	public void setRanges(float xMin, float xMax, float yMin, float yMax) {
		setXRange0(xMin, xMax);
		setYRange0(yMin, yMax);

		this.graphView.invalidateGraphs();
	}

	public void setViewDimensions(@NotNull View view) {
		this.vWidthPxs = view.getWidth();
		this.vHeightPxs = view.getHeight();

		this.graphView.invalidateGraphs();
	}


	public void setGraphDimensions(float gWidth, float gHeight) {
		this.gWidth = gWidth;
		this.gHeight = gHeight;

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
		copy.gHeight = this.gHeight;

		return copy;
	}
}
