/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import jscl.AngleUnit;

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

	@Nonnull
	private AngleUnit angleUnit;

	public AngleUnitsButton(Context context, @Nonnull AttributeSet attrs) {
		super(context, attrs);
		this.angleUnit = Locator.getInstance().getEngine().getAngleUnits();
	}

	@Override
	protected void initDirectionTextPaint(@Nonnull Paint basePaint,
										  @Nonnull DirectionTextData directionTextData,
										  @Nonnull Resources resources) {
		super.initDirectionTextPaint(basePaint, directionTextData, resources);

		final TextPaint directionTextPaint = directionTextData.getPaint();
		final int color = getDirectionTextColor(directionTextData.getText());
		directionTextPaint.setColor(color);
		if (!isCurrentAngleUnits(directionTextData.getText())) {
			directionTextPaint.setAlpha(getDirectionTextAlpha());
		}
	}

	int getDirectionTextColor(@Nonnull String directionText) {
		final int color;
		final Resources resources = getResources();
		if (isCurrentAngleUnits(directionText)) {
			color = resources.getColor(R.color.cpp_selected_angle_unit_text_color);
		} else {
			color = resources.getColor(R.color.cpp_default_text_color);
		}
		return color;
	}

	boolean isCurrentAngleUnits(@Nonnull String directionText) {
		return this.angleUnit.name().equals(directionText);
	}

	public void setAngleUnit(@Nonnull AngleUnit angleUnit) {
		if (this.angleUnit != angleUnit) {
			this.angleUnit = angleUnit;
			invalidate();
		}
	}
}
