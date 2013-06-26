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

/**
 * User: serso
 * Date: 10/4/12
 * Time: 10:08 PM
 */
public enum PlotLineColor {

	// Color.WHITE
	white(0xFFFFFFFF),

	blue(0xFF10648C),

	// Color.RED
	red(0xFFFF0000),

	// Color.GREEN
	green(0xFF00FF00),

	// Color.GRAY
	grey(0xFF888888);

	private final int color;

	private PlotLineColor(int color) {
		this.color = color;
	}


	public int getColor() {
		return this.color;
	}

	@Nonnull
	public static PlotLineColor valueOf(int color) {
		for (PlotLineColor plotLineColor : PlotLineColor.values()) {
			if (plotLineColor.color == color) {
				return plotLineColor;
			}
		}

		return PlotLineColor.white;
	}
}
