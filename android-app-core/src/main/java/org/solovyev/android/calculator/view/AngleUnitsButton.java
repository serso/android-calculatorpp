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
import javax.annotation.Nonnull;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.core.R;
import org.solovyev.android.view.drag.DirectionDragButton;

/**
 * User: serso
 * Date: 11/22/11
 * Time: 2:42 PM
 */
public class AngleUnitsButton extends DirectionDragButton {

	public AngleUnitsButton(Context context, @Nonnull AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void initDirectionTextPaint(@Nonnull Paint basePaint,
										  @Nonnull DirectionTextData directionTextData,
										  @Nonnull Resources resources) {
		super.initDirectionTextPaint(basePaint, directionTextData, resources);

		final TextPaint directionTextPaint = directionTextData.getPaint();
		if (Locator.getInstance().getEngine().getAngleUnits().name().equals(directionTextData.getText())) {
			directionTextPaint.setColor(resources.getColor(R.color.cpp_selected_angle_unit_text_color));
		} else {
			directionTextPaint.setColor(resources.getColor(R.color.cpp_default_text_color));
			directionTextPaint.setAlpha(getDirectionTextAlpha());
		}
	}
}
