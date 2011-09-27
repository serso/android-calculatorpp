/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.view.widgets;

import android.util.Log;
import android.view.MotionEvent;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.view.HistoryControl;
import org.solovyev.common.utils.Point2d;
import org.solovyev.common.utils.StringUtils;
import org.solovyev.common.utils.history.HistoryAction;

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
