/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
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
 * Date: 10/24/11
 * Time: 9:52 PM
 */
public class EqualsDragProcessor implements SimpleOnDragListener.DragProcessor {

	public EqualsDragProcessor() {
	}

	@Override
	public boolean processDragEvent(@NotNull DragDirection dragDirection, @NotNull DragButton dragButton, @NotNull Point2d startPoint2d, @NotNull MotionEvent motionEvent) {
		boolean result = false;

		if (dragButton instanceof DirectionDragButton) {
            if (dragDirection == DragDirection.down) {
                CalculatorActivityLauncher.tryPlot();
                result = true;
            } else {
                final String text = ((DirectionDragButton) dragButton).getText(dragDirection);
                if ("≡".equals(text)) {
                    Locator.getInstance().getCalculator().simplify();
                    result = true;
                }
            }
        }

		return result;
	}
}
