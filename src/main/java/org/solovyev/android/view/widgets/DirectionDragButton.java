/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.view.widgets;

import android.content.Context;
import android.content.res.Resources;
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
	private String textMiddle;

	private static class DirectionTextData {

		@NotNull
		private String text;

		@NotNull
		private Point2d position;

		@NotNull
		private TextPaint paint;

		private DirectionTextData(@NotNull String text) {
			this.text = text;
		}

		@NotNull
		public String getText() {
			return text;
		}

		public void setText(@NotNull String text) {
			this.text = text;
		}

		@NotNull
		public Point2d getPosition() {
			return position;
		}

		public void setPosition(@NotNull Point2d position) {
			this.position = position;
		}

		@NotNull
		public TextPaint getPaint() {
			return paint;
		}

		public void setPaint(@NotNull TextPaint paint) {
			this.paint = paint;
		}
	}

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

		if (textUp != null) {
			initUpDownTextPaint(basePaint, DragDirection.up);
			textUpPosition = getTextPosition(upTextPaint, basePaint, textUp, getText(), 1, getWidth(), getHeight(), getDirectionTextScale());
		}

		if (textDown != null) {
			initUpDownTextPaint(basePaint, DragDirection.down);
			textDownPosition = getTextPosition(downTextPaint, basePaint, textDown, getText(), -1, getWidth(), getHeight(), getDirectionTextScale());
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


		if (textUp != null && textUpPosition != null) {
			initUpDownTextPaint(null, DragDirection.up);
			canvas.drawText(textUp, 0, textUp.length(), textUpPosition.getX(), textUpPosition.getY(), upTextPaint);
		}

		if (textDown != null && textDownPosition != null) {
			initUpDownTextPaint(null, DragDirection.down);
			canvas.drawText(textDown, 0, textDown.length(), textDownPosition.getX(), textDownPosition.getY(), downTextPaint);
		}
	}

	@Nullable
	protected TextPaint initUpDownTextPaint(@Nullable Paint paint, @NotNull DragDirection direction) {
		if (paint == null) {
			paint = getPaint();
		}

		final Resources resources = getResources();
		if (direction == DragDirection.up) {
			upTextPaint = getUpDownTextPaint(paint, resources, getDirectionTextScale());
			return upTextPaint;
		} else if (direction == DragDirection.down) {
			downTextPaint = getUpDownTextPaint(paint, resources, getDirectionTextScale());
			return downTextPaint;
		}

		return null;
	}

	@NotNull
	private static TextPaint getUpDownTextPaint(@NotNull Paint basePaint, @NotNull Resources resources, @NotNull Float directionTextScale) {
		final TextPaint result = new TextPaint(basePaint);
		result.setColor(resources.getColor(R.color.button_text_color));
		result.setAlpha(getDefaultDirectionTextAlpha());
		result.setTextSize(basePaint.getTextSize() * directionTextScale);

		return result;
	}

	protected static int getDefaultDirectionTextAlpha() {
		return 150;
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
