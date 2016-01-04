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

package org.solovyev.android.calculator.drag;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import org.solovyev.android.view.AndroidViewUtils;
import org.solovyev.common.math.Point2d;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DragButton extends Button {

    @Nullable
    private Point2d startPoint = null;

    @Nullable
    private DragListener onDragListener;

    private boolean showText = true;

    @Nullable
    private CharSequence textBackup;

    public DragButton(@Nonnull Context context, @Nonnull AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnDragListener(@Nullable DragListener onDragListener) {
        this.onDragListener = onDragListener;
    }

    @Override
    public boolean onTouchEvent(@Nonnull MotionEvent event) {
        boolean consumed = false;

        // in order to avoid possible NPEs
        final Point2d localStartPoint = startPoint;
        final DragListener localOnDragListener = onDragListener;

        if (localOnDragListener != null) {
            // only if onDrag() listener specified

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // start tracking: set start point
                    startPoint = new Point2d(event.getX(), event.getY());
                    break;

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    // stop tracking

                    startPoint = null;
                    if (localStartPoint != null) {
                        consumed = localOnDragListener.onDrag(DragButton.this, new DragEvent(localStartPoint, event));
                        if (consumed && localOnDragListener.isSuppressOnClickEvent()) {
                            final MotionEvent newEvent = MotionEvent.obtain(event);
                            newEvent.setAction(MotionEvent.ACTION_CANCEL);
                            super.onTouchEvent(newEvent);
                            newEvent.recycle();
                            return true;
                        }
                    }
                    break;
            }
        }

        return super.onTouchEvent(event) || consumed;
    }

    @Override
    public boolean dispatchTouchEvent(@Nonnull MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        CharSequence text = getText();
        if (!Strings.isEmpty(text)) {
            super.onDraw(canvas);
        } else {
            if (!AndroidViewUtils.drawDrawables(canvas, this)) {
                super.onDraw(canvas);
            }
        }
    }


    public boolean isShowText() {
        return showText;
    }

    public void setShowText(boolean showText) {
        if (this.showText != showText) {
            if (showText) {
                setText(textBackup);
                textBackup = null;
            } else {
                textBackup = this.getText();
                setText(null);
            }
            this.showText = showText;
        }
    }
}
