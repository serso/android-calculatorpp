/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.view.widgets.DirectionDragButton;

/**
 * User: serso
 * Date: 11/22/11
 * Time: 2:42 PM
 */
public class AngleUnitsButton extends DirectionDragButton {

	public AngleUnitsButton(Context context, @NotNull AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void initDirectionTextPaint(@NotNull Paint basePaint,
										  @NotNull DirectionTextData directionTextData,
										  @NotNull Resources resources) {
		super.initDirectionTextPaint(basePaint, directionTextData, resources);

		final TextPaint directionTextPaint = directionTextData.getPaint();
		if (CalculatorEngine.instance.getEngine().getAngleUnits().name().equals(directionTextData.getText())) {
			directionTextPaint.setColor(resources.getColor(R.color.selected_angle_unit_text_color));
		} else {
			directionTextPaint.setColor(resources.getColor(R.color.default_text_color));
			directionTextPaint.setAlpha(getDirectionTextAlpha());
		}
	}
}
