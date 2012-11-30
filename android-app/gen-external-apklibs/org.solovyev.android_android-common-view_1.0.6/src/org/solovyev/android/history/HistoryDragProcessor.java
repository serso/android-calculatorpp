/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.history;

import android.util.Log;
import android.view.MotionEvent;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.view.drag.DragButton;
import org.solovyev.android.view.drag.DragDirection;
import org.solovyev.android.view.drag.SimpleOnDragListener;
import org.solovyev.common.history.HistoryAction;
import org.solovyev.common.history.HistoryControl;
import org.solovyev.common.math.Point2d;

/**
* User: serso
* Date: 9/16/11
* Time: 11:36 PM
*/
public class HistoryDragProcessor<T> implements SimpleOnDragListener.DragProcessor {

	@NotNull
	private final HistoryControl<T> historyControl;

	public HistoryDragProcessor(@NotNull HistoryControl<T> historyControl) {
		this.historyControl = historyControl;
	}

	@Override
	public boolean processDragEvent(@NotNull DragDirection dragDirection, @NotNull DragButton dragButton, @NotNull Point2d startPoint2d, @NotNull MotionEvent motionEvent) {
		boolean result = false;

		Log.d(String.valueOf(dragButton.getId()), "History on drag event start: " + dragDirection);

		final HistoryAction historyAction;
		if ( dragDirection == DragDirection.up ) {
			historyAction = HistoryAction.undo;
		} else if ( dragDirection == DragDirection.down ) {
			historyAction = HistoryAction.redo;
		} else {
			historyAction = null;
		}

		if (historyAction != null) {
			result = true;
			historyControl.doHistoryAction(historyAction);
		}

		return result;
	}
}
