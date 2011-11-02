/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Text view that auto adjusts text size to fit within the view.
 * If the text size equals the minimum text size and still does not
 * fit, append with an ellipsis.
 *
 * @author Chase Colburn
 * @since Apr 4, 2011
 */
public class AutoResizeTextView extends TextView {

	// Minimum text size for this text view
	public static final float MIN_TEXT_SIZE = 20;

	// Interface for resize notifications
	public interface OnTextResizeListener {
		public void onTextResize(TextView textView, float oldSize, float newSize);
	}

	// Off screen canvas for text size rendering
	private static final Canvas textResizeCanvas = new Canvas();

	// Our ellipse string
	private static final String ellipsis = "...";

	// Registered resize listener
	private OnTextResizeListener textResizeListener;

	// Flag for text and/or size changes to force a resize
	private boolean needsResize = false;

	// Lower bounds for text size
	private float minTextSize = MIN_TEXT_SIZE;

	// Text view line spacing multiplier
	private float spacingMult = 1.0f;

	// Text view additional line spacing
	private float spacingAdd = 0.0f;

	// Add ellipsis to text that overflows at the smallest text size
	private boolean addEllipsis = true;

	// Default constructor override
	public AutoResizeTextView(Context context) {
		this(context, null);
	}

	// Default constructor when inflating from XML file
	public AutoResizeTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	// Default constructor override
	public AutoResizeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * When text changes, set the force resize flag to true and resetInterpreter the text size.
	 */
	@Override
	protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
		needsResize = true;
		// Since this view may be reused, it is good to resetInterpreter the text size
	}

	/**
	 * If the text view size changed, set the force resize flag to true
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (w != oldw || h != oldh) {
			needsResize = true;
		}
	}

	/**
	 * Register listener to receive resize notifications
	 *
	 * @param listener
	 */
	public void setOnResizeListener(OnTextResizeListener listener) {
		textResizeListener = listener;
	}

	/**
	 * Override the set text size to update our internal reference values
	 */
	@Override
	public void setTextSize(int unit, float size) {
		super.setTextSize(unit, size);
	}

	/**
	 * Override the set line spacing to update our internal reference values
	 */
	@Override
	public void setLineSpacing(float add, float mult) {
		super.setLineSpacing(add, mult);
		spacingMult = mult;
		spacingAdd = add;
	}

	/**
	 * Set the lower text size limit and invalidate the view
	 *
	 * @param minTextSize
	 */
	public void setMinTextSize(float minTextSize) {
		this.minTextSize = minTextSize;
		requestLayout();
		invalidate();
	}

	/**
	 * Return lower text size limit
	 *
	 * @return
	 */
	public float getMinTextSize() {
		return minTextSize;
	}

	/**
	 * Set flag to add ellipsis to text that overflows at the smallest text size
	 *
	 * @param addEllipsis
	 */
	public void setAddEllipsis(boolean addEllipsis) {
		this.addEllipsis = addEllipsis;
	}

	/**
	 * Return flag to add ellipsis to text that overflows at the smallest text size
	 *
	 * @return
	 */
	public boolean getAddEllipsis() {
		return addEllipsis;
	}


	/**
	 * Resize text after measuring
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		if (changed || needsResize) {
			resizeText(right - left, bottom - top, getText());
		}
		super.onLayout(changed, left, top, right, bottom);
	}

	/**
	 * Resize the text size with default width and height
	 */
	public void resizeText() {
		resizeText(getText());
	}

	public void resizeText(final CharSequence text) {
		int heightLimit = getHeight() - getPaddingBottom() - getPaddingTop();
		int widthLimit = getWidth() - getPaddingLeft() - getPaddingRight();
		resizeText(widthLimit, heightLimit, text);
	}

	/**
	 * Resize the text size with specified width and height
	 *
	 * @param width
	 * @param height
	 * @param text
	 */
	public void resizeText(int width, int height, final CharSequence text) {

		// Do not resize if the view does not have dimensions or there is no text
		if (text == null || text.length() == 0 || height <= 0 || width <= 0) {
			return;
		}

		// Get the text view's paint object
		TextPaint textPaint = getPaint();

		// Store the current text size
		float oldTextSize = textPaint.getTextSize();

		// If there is a max text size set, use the lesser of that and the default text size
		float newTextSize = 100;

		// Get the required text height
		int newTextHeight = getTextRect(text, textPaint, width, newTextSize);

		if (newTextHeight > height) {
			// Until we either fit within our text view or we had reached our min text size, incrementally try smaller sizes
			while (newTextHeight > height) {
				if (newTextSize <= minTextSize) {
					break;
				}
				newTextSize = Math.max(newTextSize - 2, minTextSize);
				newTextHeight = getTextRect(text, textPaint, width, newTextSize);
			}
		} else {
			while (newTextHeight < height) {
				if (newTextSize <= minTextSize) {
					break;
				}
				newTextSize = Math.max(newTextSize + 2, minTextSize);
				newTextHeight = getTextRect(text, textPaint, width, newTextSize);
			}
		}

		// If we had reached our minimum text size and still don't fit, append an ellipsis
		if (addEllipsis && newTextSize == minTextSize && newTextHeight > height) {
			// Draw using a static layout
			StaticLayout layout = new StaticLayout(text, textPaint, width, Alignment.ALIGN_NORMAL, spacingMult, spacingAdd, false);
			layout.draw(textResizeCanvas);
			int lastLine = layout.getLineForVertical(height) - 1;
			int start = layout.getLineStart(lastLine);
			int end = layout.getLineEnd(lastLine);
			float lineWidth = layout.getLineWidth(lastLine);
			float ellipseWidth = textPaint.measureText(ellipsis);

			// Trim characters off until we have enough room to draw the ellipsis
			while (width < lineWidth + ellipseWidth) {
				lineWidth = textPaint.measureText(text.subSequence(start, --end + 1).toString());
			}
			setText(text.subSequence(0, end) + ellipsis);

		}

		// Some devices try to auto adjust line spacing, so force default line spacing
		// and invalidate the layout as a side effect
		textPaint.setTextSize(newTextSize);
		setLineSpacing(spacingAdd, spacingMult);

		// Notify the listener if registered
		if (textResizeListener != null) {
			textResizeListener.onTextResize(this, oldTextSize, newTextSize);
		}

		// Reset force resize flag
		needsResize = false;
	}

	// Set the text size of the text paint object and use a static layout to render text off screen before measuring
	private int getTextRect(CharSequence source, TextPaint paint, int width, float textSize) {
		// Update the text paint object
		paint.setTextSize(textSize);
		// Draw using a static layout
		StaticLayout layout = new StaticLayout(source, paint, width, Alignment.ALIGN_NORMAL, spacingMult, spacingAdd, false);
		layout.draw(textResizeCanvas);
		return layout.getHeight();
	}
}
