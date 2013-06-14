package org.solovyev.android.calculator.plot;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * User: serso
 * Date: 1/19/13
 * Time: 4:51 PM
 */
public final class PlotBoundaries implements Serializable {

	public static final float DEFAULT_MIN_NUMBER = -10f;

	public static final float DEFAULT_MAX_NUMBER = 10f;


	private float xMin;
	private float xMax;
	private float yMin;
	private float yMax;

	public PlotBoundaries() {
	}

	PlotBoundaries(float xMin, float xMax, float yMin, float yMax) {
		this.xMin = Math.min(xMin, xMax);
		this.xMax = Math.max(xMin, xMax);
		this.yMin = Math.min(yMin, yMax);
		this.yMax = Math.max(yMin, yMax);
	}

	@NotNull
	public static PlotBoundaries newInstance(float xMin, float xMax, float yMin, float yMax) {
		return new PlotBoundaries(xMin, xMax, yMin, yMax);
	}

	@NotNull
	public static PlotBoundaries newInstance(float xMin, float xMax) {
		return newInstance(xMin, xMax, DEFAULT_MIN_NUMBER, DEFAULT_MAX_NUMBER);
	}

	public float getXMin() {
		return xMin;
	}

	public float getXMax() {
		return xMax;
	}

	public float getYMin() {
		return yMin;
	}

	public float getYMax() {
		return yMax;
	}

	@Override
	public String toString() {
		return "PlotBoundaries{" +
				"yMax=" + yMax +
				", yMin=" + yMin +
				", xMax=" + xMax +
				", xMin=" + xMin +
				'}';
	}

	@NotNull
	public static PlotBoundaries newDefaultInstance() {
		PlotBoundaries plotBoundaries = new PlotBoundaries();
		plotBoundaries.xMin = DEFAULT_MIN_NUMBER;
		plotBoundaries.yMin = DEFAULT_MIN_NUMBER;
		plotBoundaries.xMax = DEFAULT_MAX_NUMBER;
		plotBoundaries.yMax = DEFAULT_MAX_NUMBER;
		return plotBoundaries;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PlotBoundaries)) return false;

		PlotBoundaries that = (PlotBoundaries) o;

		if (Float.compare(that.xMax, xMax) != 0) return false;
		if (Float.compare(that.xMin, xMin) != 0) return false;
		if (Float.compare(that.yMax, yMax) != 0) return false;
		if (Float.compare(that.yMin, yMin) != 0) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (xMin != +0.0f ? Float.floatToIntBits(xMin) : 0);
		result = 31 * result + (xMax != +0.0f ? Float.floatToIntBits(xMax) : 0);
		result = 31 * result + (yMin != +0.0f ? Float.floatToIntBits(yMin) : 0);
		result = 31 * result + (yMax != +0.0f ? Float.floatToIntBits(yMax) : 0);
		return result;
	}
}
