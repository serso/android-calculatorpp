package org.solovyev.android.views.dragbutton;


import androidx.annotation.StyleableRes;

public enum DragDirection {

    up(180f - 45f, 180f - 0f, R.styleable.DirectionText_directionTextUp, R.styleable.DirectionText_directionTextScaleUp, R.styleable.DirectionText_directionTextPaddingUp),
    down(0f, 45f, R.styleable.DirectionText_directionTextDown, R.styleable.DirectionText_directionTextScaleDown, R.styleable.DirectionText_directionTextPaddingDown),
    left(90f - 45f, 90f + 45f, R.styleable.DirectionText_directionTextLeft, R.styleable.DirectionText_directionTextScaleLeft, R.styleable.DirectionText_directionTextPaddingLeft),
    right(90f - 45f, 90f + 45f, R.styleable.DirectionText_directionTextRight, R.styleable.DirectionText_directionTextScaleRight, R.styleable.DirectionText_directionTextPaddingRight);

    final float angleFrom;
    final float angleTo;
    @StyleableRes
    final int textAttr;
    @StyleableRes
    final int scaleAttr;
    @StyleableRes
    final int paddingAttr;

    DragDirection(float angleFrom, float angleTo, @StyleableRes int textAttr, @StyleableRes int scaleAttr, @StyleableRes int paddingAttr) {
        this.angleFrom = angleFrom;
        this.angleTo = angleTo;
        this.textAttr = textAttr;
        this.scaleAttr = scaleAttr;
        this.paddingAttr = paddingAttr;
    }
}
