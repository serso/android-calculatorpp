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

/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.view.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.R;
import org.solovyev.android.view.FontSizeAdjuster;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.Point2d;
import org.solovyev.common.utils.StringUtils;

import java.util.Arrays;

/**
 * NOTE: copied from com.android.calculator2.ColorButton
 */

/**
 * Button with click-animation effect.
 */
public class ColorButton extends Button {

	private int CLICK_FEEDBACK_COLOR;
	private static final int CLICK_FEEDBACK_INTERVAL = 10;
	private static final int CLICK_FEEDBACK_DURATION = 350;

	@NotNull
	private Point2d textPosition;
	private long animationStart;
	private Paint feedbackPaint;

	@NotNull
	private final OnClickListenerVibrator onClickListener;

	private static final float H_TEXT_POSITION_DEFAULT_VALUE = 0.5f;

	// todo serso: check - currently not worked
	private float hTextPosition = H_TEXT_POSITION_DEFAULT_VALUE;

	public ColorButton(Context context, AttributeSet attrs) {
		this(context, attrs, true);
	}

	public ColorButton(Context context, AttributeSet attrs, boolean init) {
		super(context, attrs);

		if (init) {
			init(context);
		}

		this.onClickListener = new OnClickListenerVibrator((Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE), PreferenceManager.getDefaultSharedPreferences(context));
	}

	protected void init(Context context) {
		final Resources resources = getResources();

		CLICK_FEEDBACK_COLOR = resources.getColor(org.solovyev.android.calculator.R.color.magic_flame);
		feedbackPaint = new Paint();
		feedbackPaint.setStyle(Style.STROKE);
		feedbackPaint.setStrokeWidth(2);


		if (CollectionsUtils.contains(getText().toString(), Arrays.asList("+", "-", "/", "×", "*", "∙"))) {
			getPaint().setColor(resources.getColor(R.color.button_operator_text_color));
		} else if (getText().toString().equals("C"))  {
			getPaint().setColor(resources.getColor(R.color.button_ce_text_color));
		} else {
			getPaint().setColor(resources.getColor(R.color.button_text_color));
		}

		animationStart = -1;

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

		result.setX(hTextPosition * getWidth() - 0.5f * paint.measureText(text.toString()));

		float height = getHeight() - paint.ascent() - paint.descent();

		result.setY(height / 2);

		return result;
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int before, int after) {
		measureText();
	}

	public void drawMagicFlame(int duration, Canvas canvas) {
		int alpha = 255 - 255 * duration / CLICK_FEEDBACK_DURATION;
		int color = CLICK_FEEDBACK_COLOR | (alpha << 24);

		feedbackPaint.setColor(color);
		canvas.drawRect(1, 1, getWidth() - 1, getHeight() - 1, feedbackPaint);
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (animationStart != -1) {
			int animDuration = (int) (System.currentTimeMillis() - animationStart);

			if (animDuration >= CLICK_FEEDBACK_DURATION) {
				animationStart = -1;
			} else {
				drawMagicFlame(animDuration, canvas);
				postInvalidateDelayed(CLICK_FEEDBACK_INTERVAL);
			}
		}

		CharSequence text = getText();
		if (!StringUtils.isEmpty(text) && textPosition != null) {
			canvas.drawText(text, 0, text.length(), textPosition.getX(), textPosition.getY(), getPaint());
		} else {
			drawDrawables(canvas);
		}
	}

	private void drawDrawables(Canvas canvas) {
		final int compoundPaddingLeft = getCompoundPaddingLeft();
		final int compoundPaddingTop = getCompoundPaddingTop();
		final int compoundPaddingRight = getCompoundPaddingRight();
		final int compoundPaddingBottom = getCompoundPaddingBottom();

		final int scrollX = getScrollX();
		final int scrollY = getScrollY();

		final int right = getRight();
		final int left = getLeft();
		final int bottom = getBottom();
		final int top = getTop();

		final Drawable[] drawables = getCompoundDrawables();
		if (drawables != null) {

			int vspace = bottom - top - compoundPaddingBottom - compoundPaddingTop;
			int hspace = right - left - compoundPaddingRight - compoundPaddingLeft;

			Drawable topDr = drawables[1];
			// IMPORTANT: The coordinates computed are also used in invalidateDrawable()
			// Make sure to update invalidateDrawable() when changing this code.
			if (topDr != null) {
				canvas.save();
				canvas.translate(scrollX + compoundPaddingLeft + (hspace - topDr.getBounds().width()) / 2,
						scrollY + getPaddingTop() + vspace / 2);
				topDr.draw(canvas);
				canvas.restore();
			}
		}
	}

	public void animateClickFeedback() {
		animationStart = System.currentTimeMillis();
		invalidate();
	}

	@Override
	public boolean performClick() {
		vibrate();
		return super.performClick();
	}

	@Override
	public boolean performLongClick() {
		vibrate();
		return super.performLongClick();
	}

	private void vibrate() {
	 	this.onClickListener.onClick(this);
	}

	public float getHTextPosition() {
		return hTextPosition;
	}

	public void setHTextPosition(float hTextPosition) {
		this.hTextPosition = hTextPosition;
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
