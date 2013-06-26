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

import javax.annotation.Nonnull;

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

	@Nonnull
	public static PlotBoundaries newInstance(float xMin, float xMax, float yMin, float yMax) {
		return new PlotBoundaries(xMin, xMax, yMin, yMax);
	}

	@Nonnull
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

	@Nonnull
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
