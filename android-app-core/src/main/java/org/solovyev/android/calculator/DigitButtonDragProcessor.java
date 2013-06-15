/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.view.MotionEvent;
import javax.annotation.Nonnull;
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

	@Nonnull
	private CalculatorKeyboard calculatorKeyboard;

	public DigitButtonDragProcessor(@Nonnull CalculatorKeyboard calculatorKeyboard) {
		this.calculatorKeyboard = calculatorKeyboard;
	}

	@Override
	public boolean processDragEvent(@Nonnull DragDirection dragDirection, @Nonnull DragButton dragButton, @Nonnull Point2d startPoint2d, @Nonnull MotionEvent motionEvent) {
		assert dragButton instanceof DirectionDragButton;
		calculatorKeyboard.buttonPressed(((DirectionDragButton) dragButton).getText(dragDirection));
		return true;
	}

}
