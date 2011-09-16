/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.view.MotionEvent;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.view.*;
import org.solovyev.common.utils.Point2d;

/**
 * User: serso
 * Date: 9/16/11
 * Time: 11:45 PM
 */
public class CursorDragProcessor implements SimpleOnDragListener.DragProcessor{

	@NotNull
	private final CursorControl cursorControl;

	public CursorDragProcessor(@NotNull CursorControl cursorControl) {
		this.cursorControl = cursorControl;
	}

	@Override
	public boolean processDragEvent(@NotNull DragDirection dragDirection, @NotNull DragButton dragButton, @NotNull Point2d startPoint2d, @NotNull MotionEvent motionEvent) {
		boolean result = false;

		if (dragButton instanceof DirectionDragButton) {
			String text = ((DirectionDragButton) dragButton).getText(dragDirection);
			if ("↞".equals(text)) {
				cursorControl.setCursorOnStart();
			} else if ("↠".equals(text)) {
				cursorControl.setCursorOnEnd();
			}
		}

		return result;
	}
}
