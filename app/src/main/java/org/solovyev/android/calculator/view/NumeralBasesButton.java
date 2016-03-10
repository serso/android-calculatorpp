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

import org.solovyev.android.calculator.R;
import org.solovyev.android.views.dragbutton.DirectionDragImageButton;
import org.solovyev.android.views.dragbutton.DirectionTextView;
import org.solovyev.android.views.dragbutton.DragDirection;

import javax.annotation.Nonnull;

import jscl.NumeralBase;

public class NumeralBasesButton extends DirectionDragImageButton {

    @Nonnull
    private NumeralBase numeralBase = NumeralBase.dec;

    public NumeralBasesButton(Context context, @Nonnull AttributeSet attrs) {
        super(context, attrs);
        updateDirectionColors();
    }

    boolean isCurrentNumberBase(@Nonnull String directionText) {
        return numeralBase.name().equals(directionText);
    }

    public void setNumeralBase(@Nonnull NumeralBase numeralBase) {
        if (this.numeralBase == numeralBase) {
            return;
        }
        this.numeralBase = numeralBase;
        updateDirectionColors();
    }

    private void updateDirectionColors() {
        for (DragDirection direction : DragDirection.values()) {
            final DirectionTextView.Text text = getText(direction);
            if (isCurrentNumberBase(text.getValue())) {
                text.setColor(ContextCompat.getColor(getContext(), R.color.cpp_selected_angle_unit_text), 1f);
            } else {
                text.setColor(ContextCompat.getColor(getContext(), R.color.cpp_text), DirectionTextView.DEF_ALPHA);
            }
        }
    }
}
