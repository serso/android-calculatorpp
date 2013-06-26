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

import android.graphics.Bitmap;
import android.widget.ZoomButtonsController;

import javax.annotation.Nonnull;

import java.util.List;

public interface GraphView extends ZoomButtonsController.OnZoomListener, TouchHandler.TouchHandlerListener {

	public void init(@Nonnull PlotViewDef plotViewDef);

	public void setPlotFunctions(@Nonnull List<PlotFunction> plotFunctions);

	@Nonnull
	public List<PlotFunction> getPlotFunctions();

	public void onDestroy();

	public void onPause();

	public void onResume();

	@Nonnull
	public Bitmap captureScreenshot();

	void setXRange(float xMin, float xMax);

	void setYRange(float yMin, float yMax);

	float getXMin();

	float getXMax();

	float getYMin();

	float getYMax();

	void invalidateGraphs();

	void setAdjustYAxis(boolean adjustYAxis);
}
