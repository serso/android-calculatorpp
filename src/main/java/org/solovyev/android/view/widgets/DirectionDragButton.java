/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.view.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.R;
import org.solovyev.common.utils.Point2d;
import org.solovyev.common.utils.StringUtils;

/**
 * User: serso
 * Date: 7/17/11
 * Time: 10:25 PM
 */
public class DirectionDragButton extends DragButton {

	@NotNull
	private final static Float DEFAULT_DIRECTION_TEXT_SCALE = 0.33f;

	@Nullable
	private String textUp;

	@Nullable
	private String textDown;

	@Nullable
	private String textMiddle;

	@NotNull
	private Point2d textUpPosition;

	@NotNull
	private Point2d textDownPosition;

	@NotNull
	private TextPaint upDownTextPaint;

	@Nullable
	private Float directionTextScale = DEFAULT_DIRECTION_TEXT_SCALE;

	public DirectionDragButton(Context context, @NotNull AttributeSet attrs) {
		super(context, attrs, false);
		init(context, attrs);
	}

	@Nullable
	public String getDirectionText(@NotNull DragDirection direction) {
		final String result;

		switch (direction) {
			case up:
				result = this.getTextUp();
				break;

			case down:
				result = this.getTextDown();
				break;

			default:
				result = null;
				break;
		}

		return result;
	}


	private void init(@NotNull Context context, @NotNull AttributeSet attrs) {

		TypedArray a = context.obtainStyledAttributes(attrs, org.solovyev.android.calculator.R.styleable.DragButton);

		final int N = a.getIndexCount();
		for (int i = 0; i < N; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
				case R.styleable.DragButton_textUp:
					this.textUp = a.getString(attr);
					break;
				case R.styleable.DragButton_textDown:
					this.textDown = a.getString(attr);
					break;
				case R.styleable.DragButton_directionTextScale:
					this.directionTextScale = Float.valueOf(a.getString(attr));
					break;
			}
		}

		// backup text
		this.textMiddle = String.valueOf(getText());

		super.init(context);
	}

	@Override
	protected void measureText() {
		super.measureText();

		final Paint basePaint = getPaint();
		initUpDownTextPaint(basePaint);

		if (textUp != null) {
			textUpPosition = getTextPosition(upDownTextPaint, basePaint, textUp, getText(), 1, getWidth(), getHeight(), getDirectionTextScale());
		}

		if (textDown != null) {
			textDownPosition = getTextPosition(upDownTextPaint, basePaint, textDown, getText(), -1, getWidth(), getHeight(), getDirectionTextScale());
		}

		/*if (textDownPosition != null && textUpPosition != null) {
			if (textDownPosition.getX() > textUpPosition.getX()) {
				textDownPosition.setX(textUpPosition.getX());
			} else {
				textUpPosition.setX(textDownPosition.getX());
			}
		}*/

	}

	public static Point2d getTextPosition(@NotNull Paint paint, @NotNull Paint basePaint, @NotNull CharSequence text, CharSequence baseText, float direction, int w, int h, float scale) {
		final Point2d result = new Point2d();

		float width = paint.measureText(text.toString() + " ");
		result.setX(w - width);

		float selfHeight = paint.ascent() + paint.descent();

		basePaint.measureText(StringUtils.getNotEmpty(baseText, "|"));

		if (direction < 0) {
			result.setY(h / 2 + h / 3 - selfHeight / 2);
		} else {
			result.setY(h / 2 - h / 3 - selfHeight / 2);
		}

		return result;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		initUpDownTextPaint(null);

		if (textUp != null && textUpPosition != null) {
			canvas.drawText(textUp, 0, textUp.length(), textUpPosition.getX(), textUpPosition.getY(), upDownTextPaint);
		}

		if (textDown != null && textDownPosition != null) {
			canvas.drawText(textDown, 0, textDown.length(), textDownPosition.getX(), textDownPosition.getY(), upDownTextPaint);
		}
	}

	private void initUpDownTextPaint(@Nullable Paint paint) {
		if (paint == null) {
			paint = getPaint();
		}

		upDownTextPaint = new TextPaint(paint);
		upDownTextPaint.setAlpha(150);
		upDownTextPaint.setTextSize(paint.getTextSize() * getDirectionTextScale());
	}

	@Nullable
	public String getTextUp() {
		return textUp;
	}

	@Nullable
	public String getTextDown() {
		return textDown;
	}

	@Nullable
	public String getTextMiddle() {
		return textMiddle;
	}

	@Nullable
	public String getText(@NotNull DragDirection direction) {
		final String result;

		if (direction == DragDirection.up) {
			result = getTextUp();
		} else if (direction == DragDirection.down) {
			result = getTextDown();
		} else {
			result = null;
		}

		return result;
	}

	@NotNull
	public Float getDirectionTextScale() {
		return directionTextScale == null ? DEFAULT_DIRECTION_TEXT_SCALE : directionTextScale;
	}

}
