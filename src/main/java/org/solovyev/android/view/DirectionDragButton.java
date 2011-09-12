package org.solovyev.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.R;
import org.solovyev.util.StringUtils;
import org.solovyev.util.math.Point2d;

/**
 * User: serso
 * Date: 7/17/11
 * Time: 10:25 PM
 */
public class DirectionDragButton extends DragButton {

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

	public DirectionDragButton(Context context, @NotNull AttributeSet attrs) {
		super(context, attrs, false);
		init(context, attrs);
	}


	private void init(@NotNull Context context, @NotNull AttributeSet attrs) {

		TypedArray a = context.obtainStyledAttributes(attrs, org.solovyev.android.calculator.R.styleable.DragButton);

		final int N = a.getIndexCount();
		for (int i = 0; i < N; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
				case org.solovyev.android.calculator.R.styleable.DragButton_textUp:
					this.textUp = a.getString(attr);
					break;
				case org.solovyev.android.calculator.R.styleable.DragButton_textDown:
					this.textDown = a.getString(attr);
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
			textUpPosition = getTextPosition(upDownTextPaint, basePaint, textUp, 1);
		}

		if (textDown != null) {
			textDownPosition = getTextPosition(upDownTextPaint, basePaint, textDown, -1);
		}

		if ( textDownPosition != null && textUpPosition != null ) {
			if ( textDownPosition.getX() > textUpPosition.getX() ) {
				textDownPosition.setX(textUpPosition.getX());
			} else {
				textUpPosition.setX(textDownPosition.getX());
			}
		}

	}

	private Point2d getTextPosition(@NotNull Paint paint, @NotNull Paint basePaint, @NotNull CharSequence text, float direction) {
		final Point2d result = new Point2d();

		float width = paint.measureText(text.toString() + " ");
		result.setX(getWidth() - width);

		float selfHeight = paint.ascent() + paint.descent();

		basePaint.measureText(StringUtils.getNotEmpty(getText(), "|"));

		float height = getHeight() - basePaint.ascent() - basePaint.descent();
		if (direction < 0) {
			result.setY(height / 2 - direction * height / 3 + selfHeight);
		} else {
			result.setY(height / 2 - direction * height / 3);
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
		upDownTextPaint.setTextSize(paint.getTextSize() / 2);
	}

	private String getStyledUpDownText(@Nullable String text) {
		return StringUtils.getNotEmpty(text, "&nbsp;");
	}

	public void setTextUp(@Nullable String textUp) {
		this.textUp = textUp;
	}

	@Nullable
	public String getTextUp() {
		return textUp;
	}

	public void setTextDown(@Nullable String textDown) {
		this.textDown = textDown;
	}

	@Nullable
	public String getTextDown() {
		return textDown;
	}

	public void setTextMiddle(@Nullable String textMiddle) {
		this.textMiddle = textMiddle;
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
		} else if ( direction == DragDirection.down ) {
			result = getTextDown();
		} else {
			result = null;
		}

		return result;
	}
}
