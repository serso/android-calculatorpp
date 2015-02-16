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

package org.solovyev.android.calculator.history;

import android.util.Log;
import android.view.MotionEvent;

import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.drag.DragButton;
import org.solovyev.android.calculator.drag.DragDirection;
import org.solovyev.android.calculator.drag.SimpleDragListener;
import org.solovyev.common.history.HistoryAction;
import org.solovyev.common.history.HistoryControl;
import org.solovyev.common.math.Point2d;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 9/16/11
 * Time: 11:36 PM
 */
public class HistoryDragProcessor<T> implements SimpleDragListener.DragProcessor {

	@Nonnull
	private final HistoryControl<T> historyControl;

	public HistoryDragProcessor(@Nonnull HistoryControl<T> historyControl) {
		this.historyControl = historyControl;
	}

	@Override
	public boolean processDragEvent(@Nonnull DragDirection dragDirection, @Nonnull DragButton dragButton, @Nonnull Point2d startPoint2d, @Nonnull MotionEvent motionEvent) {
		boolean result = false;

		Log.d(String.valueOf(dragButton.getId()), "History on drag event start: " + dragDirection);

		final HistoryAction historyAction;
		if (dragDirection == DragDirection.up) {
			historyAction = HistoryAction.undo;
		} else if (dragDirection == DragDirection.down) {
			historyAction = HistoryAction.redo;
		} else {
			historyAction = null;
		}

		if (historyAction != null) {
			App.getVibrator().vibrate();
			result = true;
			historyControl.doHistoryAction(historyAction);
		}

		return result;
	}
}
