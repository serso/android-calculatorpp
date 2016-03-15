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
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import jscl.AngleUnit;
import org.solovyev.android.calculator.R;
import org.solovyev.android.views.dragbutton.DirectionDragImageButton;
import org.solovyev.android.views.dragbutton.DirectionTextView;
import org.solovyev.android.views.dragbutton.DragDirection;

import javax.annotation.Nonnull;

public class AngleUnitsButton extends DirectionDragImageButton {

    @Nonnull
    private AngleUnit angleUnit = AngleUnit.deg;

    public AngleUnitsButton(Context context, @Nonnull AttributeSet attrs) {
        super(context, attrs);
        updateDirectionColors();
    }

    boolean isCurrentAngleUnits(@Nonnull String directionText) {
        return angleUnit.name().equals(directionText);
    }

    public void setAngleUnit(@Nonnull AngleUnit angleUnit) {
        if (this.angleUnit == angleUnit) {
            return;
        }
        this.angleUnit = angleUnit;
        updateDirectionColors();
    }

    private void updateDirectionColors() {
        for (DragDirection direction : DragDirection.values()) {
            final DirectionTextView.Text text = getText(direction);
            if (isCurrentAngleUnits(text.getValue())) {
                text.setColor(ContextCompat.getColor(getContext(), R.color.yellow_100), 1f);
            } else {
                text.setColor(ContextCompat.getColor(getContext(), R.color.cpp_text), DirectionTextView.DEF_ALPHA);
            }
        }
    }
}
