/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.view.MotionEvent;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.view.drag.DirectionDragButton;
import org.solovyev.android.view.drag.DragButton;
import org.solovyev.android.view.drag.DragDirection;
import org.solovyev.android.view.drag.SimpleOnDragListener;
import org.solovyev.common.math.Point2d;

/**
 * User: serso
 * Date: 9/16/11
 * Time: 11:48 PM
 */
public class DigitButtonDragProcessor implements SimpleOnDragListener.DragProcessor {

	@NotNull
	private CalculatorKeyboard calculatorKeyboard;

	public DigitButtonDragProcessor(@NotNull CalculatorKeyboard calculatorKeyboard) {
		this.calculatorKeyboard = calculatorKeyboard;
	}

	@Override
	public boolean processDragEvent(@NotNull DragDirection dragDirection, @NotNull DragButton dragButton, @NotNull Point2d startPoint2d, @NotNull MotionEvent motionEvent) {
		assert dragButton instanceof DirectionDragButton;
		calculatorKeyboard.buttonPressed(((DirectionDragButton) dragButton).getText(dragDirection));
		return true;
	}

}
