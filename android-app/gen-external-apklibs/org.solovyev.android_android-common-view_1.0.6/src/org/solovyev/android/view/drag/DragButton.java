/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.view.drag;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.view.AndroidViewUtils;
import org.solovyev.common.math.Point2d;
import org.solovyev.common.text.StringUtils;

public class DragButton extends Button {

    @Nullable
    private Point2d startPoint = null;

    @Nullable
    private org.solovyev.android.view.drag.OnDragListener onDragListener;

    @NotNull
    private DragButton.OnTouchListenerImpl onTouchListener;

    private boolean showText = true;

    @NotNull
    private final Handler uiHandler = new Handler();

    @Nullable
    private CharSequence textBackup;

    public DragButton(@NotNull Context context, @NotNull AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(new OnTouchListenerImpl());
    }

    public DragButton(@NotNull Context context, @NotNull DragButtonDef dragButtonDef) {
        super(context);

        setOnTouchListener(new OnTouchListenerImpl());

        setText(dragButtonDef.getText());
    }

    public void setOnDragListener(@Nullable org.solovyev.android.view.drag.OnDragListener onDragListener) {
        this.onDragListener = onDragListener;
    }

    @Nullable
    public org.solovyev.android.view.drag.OnDragListener getOnDragListener() {
        return onDragListener;
    }

    public void applyDef(@NotNull DragButtonDef buttonDef) {
        AndroidViewUtils.applyButtonDef(this, buttonDef);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        if (l instanceof OnTouchListenerImpl) {
            this.onTouchListener = (OnTouchListenerImpl) l;
            super.setOnTouchListener(l);
        } else {
            this.onTouchListener.nestedOnTouchListener = l;
        }
    }

    /**
     * OnTouchListener implementation that fires onDrag()
     *
     * @author serso
     */
    private final class OnTouchListenerImpl implements OnTouchListener {

        @Nullable
        private OnTouchListener nestedOnTouchListener;

        @Override
        public boolean onTouch(@NotNull View v, @NotNull MotionEvent event) {
            // processing on touch event

            boolean consumed = false;

            // in order to avoid possible NPEs
            final Point2d localStartPoint = startPoint;
            final org.solovyev.android.view.drag.OnDragListener localOnDragListener = onDragListener;

            if (localOnDragListener != null) {
                // only if onDrag() listener specified

                Log.d(String.valueOf(getId()), "onTouch() for: " + getId() + " . Motion event: " + event);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // start tracking: set start point
                        startPoint = new Point2d(event.getX(), event.getY());
                        break;

                    case MotionEvent.ACTION_UP:
                        // stop tracking

                        if (localStartPoint != null) {
                            consumed = localOnDragListener.onDrag(DragButton.this, new DragEvent(localStartPoint, event));

                            if (consumed) {
                                if (localOnDragListener.isSuppressOnClickEvent()) {
                                    // prevent on click action
                                    v.setPressed(false);
                                }
                            }
                        }

                        startPoint = null;
                        break;
                }
            }

            if (nestedOnTouchListener != null && !consumed) {
                return nestedOnTouchListener.onTouch(v, event);
            } else {
                return consumed;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        CharSequence text = getText();
        if (!StringUtils.isEmpty(text)) {
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
            if ( showText ) {
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
