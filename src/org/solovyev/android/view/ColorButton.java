/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.solovyev.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.Button;
import android.view.MotionEvent;
import android.content.res.Resources;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.R;
import org.solovyev.util.StringUtils;
import org.solovyev.util.math.Point2d;

/**
 * NOTE: copied from com.android.calculator2.ColorButton
 */

/**
 * Button with click-animation effect.
 */
public class ColorButton extends Button {
	int CLICK_FEEDBACK_COLOR;
	static final int CLICK_FEEDBACK_INTERVAL = 10;
	static final int CLICK_FEEDBACK_DURATION = 350;

	@NotNull
	private Point2d textPosition;
	private long mAnimStart;
	private Paint mFeedbackPaint;

	public ColorButton(Context context, AttributeSet attrs) {
		this(context, attrs, true);
	}

	public ColorButton(Context context, AttributeSet attrs, boolean init) {
		super(context, attrs);
		if (init) {
			init(context);
		}
	}

	protected void init(Context context) {
		Resources res = getResources();

		CLICK_FEEDBACK_COLOR = res.getColor(R.color.magic_flame);
		mFeedbackPaint = new Paint();
		mFeedbackPaint.setStyle(Style.STROKE);
		mFeedbackPaint.setStrokeWidth(2);
		getPaint().setColor(res.getColor(R.color.button_text));

		mAnimStart = -1;

		if (context instanceof FontSizeAdjuster) {
			((FontSizeAdjuster) context).adjustFontSize(this);
		}
	}


	@Override
	public void onSizeChanged(int w, int h, int oldW, int oldH) {
		measureText();
	}

	protected void measureText() {
		Paint paint = getPaint();

		if (getText() != null) {
			textPosition = getTextPosition(paint, getText());
		}
	}

	private Point2d getTextPosition(@NotNull Paint paint, @NotNull CharSequence text) {
		final Point2d result = new Point2d();

		result.setX((getWidth() - paint.measureText(text.toString())) / 2);

		float height = getHeight() - paint.ascent() - paint.descent();

		result.setY(height / 2);

		return result;
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int before, int after) {
		measureText();
	}

	private void drawMagicFlame(int duration, Canvas canvas) {
		int alpha = 255 - 255 * duration / CLICK_FEEDBACK_DURATION;
		int color = CLICK_FEEDBACK_COLOR | (alpha << 24);

		mFeedbackPaint.setColor(color);
		canvas.drawRect(1, 1, getWidth() - 1, getHeight() - 1, mFeedbackPaint);
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (mAnimStart != -1) {
			int animDuration = (int) (System.currentTimeMillis() - mAnimStart);

			if (animDuration >= CLICK_FEEDBACK_DURATION) {
				mAnimStart = -1;
			} else {
				drawMagicFlame(animDuration, canvas);
				postInvalidateDelayed(CLICK_FEEDBACK_INTERVAL);
			}
		} else if (isPressed()) {
			drawMagicFlame(0, canvas);
		}

		CharSequence text = getText();
		if (text != null && textPosition != null) {
			canvas.drawText(text, 0, text.length(), textPosition.getX(), textPosition.getY(), getPaint());
		}
	}

	public void animateClickFeedback() {
		mAnimStart = System.currentTimeMillis();
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean result = super.onTouchEvent(event);

		switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				animateClickFeedback();
				break;
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_CANCEL:
				invalidate();
				break;
		}

		return result;
	}
}
