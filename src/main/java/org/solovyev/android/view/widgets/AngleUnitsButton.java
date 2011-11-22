package org.solovyev.android.view.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.model.CalculatorEngine;

/**
 * User: serso
 * Date: 11/22/11
 * Time: 2:42 PM
 */
public class AngleUnitsButton extends DirectionDragButton {

	public AngleUnitsButton(Context context, @NotNull AttributeSet attrs) {
		super(context, attrs);
	}

	@Nullable
	@Override
	protected TextPaint initUpDownTextPaint(@Nullable Paint paint, @NotNull DragDirection direction) {
		final TextPaint result = super.initUpDownTextPaint(paint, direction);

		if (result != null) {
			final Resources resources = getResources();
			if ( CalculatorEngine.instance.getEngine().getDefaultAngleUnits().name().equals(getDirectionText(direction)) ) {
				result.setColor(resources.getColor(R.color.selected_angle_unit_text_color));
			} else {
				result.setColor(resources.getColor(R.color.default_text_color));
			}
		}

		return result;
	}
}
