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
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import jscl.NumeralBase;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.R;
import org.solovyev.android.view.drag.DirectionDragButton;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 12/8/11
 * Time: 2:22 AM
 */
public class NumeralBasesButton extends DirectionDragButton {

	@Nonnull
	private NumeralBase numeralBase;

	public NumeralBasesButton(Context context, @Nonnull AttributeSet attrs) {
		super(context, attrs);
		this.numeralBase = Locator.getInstance().getEngine().getNumeralBase();
	}

	@Override
	protected void initDirectionTextPaint(@Nonnull Paint basePaint, @Nonnull DirectionTextData textData) {
		super.initDirectionTextPaint(basePaint, textData);

		final String text = textData.getText();
		final TextPaint paint = textData.getPaint();

		final int color = getDirectionTextColor(text);
		paint.setColor(color);
		if (!isCurrentNumberBase(text)) {
			paint.setAlpha(directionTextAlpha);
		}
	}

	int getDirectionTextColor(@Nonnull String directionText) {
		final int color;
		if (isCurrentNumberBase(directionText)) {
			color = getResources().getColor(R.color.cpp_selected_angle_unit_text_color);
		} else {
			color = getResources().getColor(R.color.cpp_default_text_color);
		}
		return color;
	}

	boolean isCurrentNumberBase(@Nonnull String directionText) {
		return this.numeralBase.name().equals(directionText);
	}

	public void setNumeralBase(@Nonnull NumeralBase numeralBase) {
		if (this.numeralBase != numeralBase) {
			this.numeralBase = numeralBase;
			invalidate();
		}
	}
}
