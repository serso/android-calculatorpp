package org.solovyev.android.views.dragbutton;

import android.support.annotation.StyleableRes;

import org.solovyev.android.calculator.R;

public enum DragDirection {

    up(180f - 45f, 180f - 0f, R.styleable.DirectionText_directionTextUp, R.styleable.DirectionText_directionTextScaleUp),
    down(0f, 45f, R.styleable.DirectionText_directionTextDown, R.styleable.DirectionText_directionTextScaleDown),
    left(90f - 45f, 90f + 45f, R.styleable.DirectionText_directionTextLeft, R.styleable.DirectionText_directionTextScaleLeft),
    right(90f - 45f, 90f + 45f, R.styleable.DirectionText_directionTextRight, R.styleable.DirectionText_directionTextScaleRight);

    final float angleFrom;
    final float angleTo;
    @StyleableRes
    final int textAttr;
    @StyleableRes
    final int scaleAttr;

    DragDirection(float angleFrom, float angleTo, int textAttr, int scaleAttr) {
        this.angleFrom = angleFrom;
        this.angleTo = angleTo;
        this.textAttr = textAttr;
        this.scaleAttr = scaleAttr;
    }
}
