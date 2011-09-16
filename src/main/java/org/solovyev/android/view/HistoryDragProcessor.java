/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.view;

import android.util.Log;
import android.view.MotionEvent;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.view.*;
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

		assert dragButton instanceof DirectionDragButton;
		String actionText = ((DirectionDragButton) dragButton).getDirectionText(dragDirection);
		if (!StringUtils.isEmpty(actionText)) {
			try {
				result = true;

				final HistoryAction historyAction = HistoryAction.valueOf(actionText);
				historyControl.doHistoryAction(historyAction);
			} catch (IllegalArgumentException e) {
				Log.e(String.valueOf(dragButton.getId()), "Unsupported history action: " + actionText);
			}
		}

		return result;
	}
}
