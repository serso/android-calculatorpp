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
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import jscl.AngleUnit;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.R;
import org.solovyev.android.views.dragbutton.DirectionDragButton;

import javax.annotation.Nonnull;

public class AngleUnitsButton extends DirectionDragButton {

    @Nonnull
    private AngleUnit angleUnit;

    public AngleUnitsButton(Context context, @Nonnull AttributeSet attrs) {
        super(context, attrs);
        this.angleUnit = Locator.getInstance().getEngine().getMathEngine().getAngleUnits();
    }

    @Override
    protected void initDirectionTextPaint(@Nonnull Paint basePaint, @Nonnull DirectionTextData textData) {
        super.initDirectionTextPaint(basePaint, textData);

        final String text = textData.getText();
        final TextPaint paint = textData.getPaint();

        final int color = getDirectionTextColor(text);
        paint.setColor(color);
        if (!isCurrentAngleUnits(text)) {
            paint.setAlpha(directionTextAlpha);
        }
    }

    int getDirectionTextColor(@Nonnull String directionText) {
        if (isCurrentAngleUnits(directionText)) {
            return ContextCompat.getColor(getContext(), R.color.cpp_selected_angle_unit_text);
        }
        return ContextCompat.getColor(getContext(), R.color.cpp_text);
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
