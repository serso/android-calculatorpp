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

import android.graphics.Color;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 1/5/13
 * Time: 9:11 PM
 */
public class PlotViewDef {

	/*
	**********************************************************************
	*
	*                           CONSTANTS
	*
	**********************************************************************
	*/

	private static final int DEFAULT_AXIS_COLOR = 0xff00a000;
	private static final int DEFAULT_GRID_COLOR = 0xff004000;
	private static final int DEFAULT_BACKGROUND_COLOR = Color.BLACK;

	/*
	**********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/

	private int axisColor = DEFAULT_AXIS_COLOR;

	private int axisLabelsColor = DEFAULT_AXIS_COLOR;

	private int gridColor = DEFAULT_GRID_COLOR;

	private int backgroundColor = DEFAULT_BACKGROUND_COLOR;

	private PlotViewDef() {
	}

	private PlotViewDef(int axisColor, int axisLabelColor, int gridColor, int backgroundColor) {
		this.axisColor = axisColor;
		this.axisLabelsColor = axisLabelColor;
		this.gridColor = gridColor;
		this.backgroundColor = backgroundColor;
	}

	@Nonnull
	public static PlotViewDef newDefaultInstance() {
		return new PlotViewDef();
	}

	@Nonnull
	public static PlotViewDef newInstance(int axisColor, int axisLabelColor, int gridColor, int backgroundColor) {
		return new PlotViewDef(axisColor, axisLabelColor, gridColor, backgroundColor);
	}

	public int getAxisColor() {
		return axisColor;
	}

	public int getAxisLabelsColor() {
		return axisLabelsColor;
	}

	public int getGridColor() {
		return gridColor;
	}

	public int getBackgroundColor() {
		return backgroundColor;
	}
}
