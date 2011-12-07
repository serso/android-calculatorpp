/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.view.MotionEvent;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.view.widgets.DirectionDragButton;
import org.solovyev.android.view.widgets.DragButton;
import org.solovyev.android.view.widgets.DragDirection;
import org.solovyev.android.view.widgets.SimpleOnDragListener;
import org.solovyev.common.utils.Point2d;

/**
 * User: serso
 * Date: 9/16/11
 * Time: 11:48 PM
 */
public class DigitButtonDragProcessor implements SimpleOnDragListener.DragProcessor {

	@NotNull
	private final CalculatorModel calculatorModel;

	public DigitButtonDragProcessor(@NotNull CalculatorModel calculatorModel) {
		this.calculatorModel = calculatorModel;
	}

	@Override
	public boolean processDragEvent(@NotNull DragDirection dragDirection, @NotNull DragButton dragButton, @NotNull Point2d startPoint2d, @NotNull MotionEvent motionEvent) {
		assert dragButton instanceof DirectionDragButton;
		calculatorModel.processDigitButtonAction(((DirectionDragButton) dragButton).getText(dragDirection));
		return true;
	}

}
