package org.solovyev.android.views; /**
 * DO WHAT YOU WANT TO PUBLIC LICENSE
 * Version 2, December 2004
 * <p/>
 * Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>
 * <p/>
 * Everyone is permitted to copy and distribute verbatim or modified
 * copies of this license document, and changing it is allowed as long
 * as the name is changed.
 * <p/>
 * DO WHAT YOU WANT TO PUBLIC LICENSE
 * TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 * <p/>
 * 0. You just DO WHAT YOU WANT TO.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TimingLogger;
import android.util.TypedValue;
import android.widget.TextView;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static android.util.TypedValue.applyDimension;

/**
 * Text view that auto adjusts text size to fit within the view.
 * If the text size equals the minimum text size and still does not
 * fit, append with an ellipsis.
 *
 * @author Chase Colburn
 * @since Apr 4, 2011
 */
@SuppressWarnings("unused")
public class AutoResizeTextView extends TextView {

    // Minimum text size for this text view
    public static final float MIN_TEXT_SIZE = 20;
    // Our ellipse string
    private static final String mEllipsis = "…";
    private static final String TAG = "AutoResizeTextView";
    // Registered resize listener
    private OnTextResizeListener mTextResizeListener;
    // Flag for text and/or size changes to force a resize
    private boolean mNeedsResize = false;
    // Text size that is set from code. This acts as a starting point for resizing
    private float mTextSize;
    // Temporary upper bounds on the starting text size
    private float mMaxTextSize = 0;
    // Lower bounds for text size
    private float mMinTextSize = MIN_TEXT_SIZE;
    // Text view line spacing multiplier
    private float mSpacingMult = 1.0f;
    // Text view additional line spacing
    private float mSpacingAdd = 0.0f;
    // Add ellipsis to text that overflows at the smallest text size
    private boolean mAddEllipsis = true;
    private final TextPaint tmpPaint = new TextPaint();
    private final float mStep;
    private final TimingLogger mTimer = new TimingLogger(TAG, "");

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
        mTextSize = getTextSize();
        mStep = Math.max(2, applyDimension(COMPLEX_UNIT_SP, 1, getResources().getDisplayMetrics()));

    }

    /**
     * When text changes, set the force resize flag to true and reset the text size.
     */
    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        mNeedsResize = true;
        // Since this view may be reused, it is good to reset the text size
        resetTextSize();

        final int height = getHeight();
        final int width = getWidth();
        if (height > 0 && width > 0) {
            resizeText();
        }
    }

    /**
     * If the text view size changed, set the force resize flag to true
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            mNeedsResize = true;
        }
    }

    /**
     * Register listener to receive resize notifications
     */
    public void setOnResizeListener(OnTextResizeListener listener) {
        mTextResizeListener = listener;
    }

    /**
     * Override the set text size to update our internal reference values
     */
    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        mTextSize = getTextSize();
    }

    /**
     * Override the set text size to update our internal reference values
     */
    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        mTextSize = getTextSize();
    }

    /**
     * Override the set line spacing to update our internal reference values
     */
    @Override
    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(add, mult);
        mSpacingMult = mult;
        mSpacingAdd = add;
    }

    /**
     * Return upper text size limit
     */
    public float getMaxTextSize() {
        return mMaxTextSize;
    }

    /**
     * Set the upper text size limit and invalidate the view
     */
    public void setMaxTextSize(float maxTextSize) {
        mMaxTextSize = maxTextSize;
        requestLayout();
        invalidate();
    }

    /**
     * Return lower text size limit
     */
    public float getMinTextSize() {
        return mMinTextSize;
    }

    /**
     * Set the lower text size limit and invalidate the view
     */
    public void setMinTextSize(float minTextSize) {
        mMinTextSize = minTextSize;
        requestLayout();
        invalidate();
    }

    /**
     * Return flag to add ellipsis to text that overflows at the smallest text size
     */
    public boolean getAddEllipsis() {
        return mAddEllipsis;
    }

    /**
     * Set flag to add ellipsis to text that overflows at the smallest text size
     */
    public void setAddEllipsis(boolean addEllipsis) {
        mAddEllipsis = addEllipsis;
    }

    /**
     * Reset the text to the original size
     */
    public void resetTextSize() {
        if (mTextSize > 0) {
            super.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            mMaxTextSize = mTextSize;
        }
    }

    /**
     * Resize text after measuring
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed || mNeedsResize) {
            int widthLimit = (right - left) - getCompoundPaddingLeft() - getCompoundPaddingRight();
            int heightLimit = (bottom - top) - getCompoundPaddingBottom() - getCompoundPaddingTop();
            resizeText(widthLimit, heightLimit);
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * Resize the text size with default width and height
     */
    public void resizeText() {

        int heightLimit = getHeight() - getPaddingBottom() - getPaddingTop();
        int widthLimit = getWidth() - getPaddingLeft() - getPaddingRight();
        resizeText(widthLimit, heightLimit);
    }

    /**
     * Resize the text size with specified width and height
     */
    @SuppressLint("SetTextI18n")
    public void resizeText(int width, int height) {
        mTimer.reset(TAG, "resizeText");
        CharSequence text = getText();
        // Do not resize if the view does not have dimensions or there is no text
        if (text == null || text.length() == 0 || height <= 0 || width <= 0 || mTextSize == 0) {
            return;
        }

        if (getTransformationMethod() != null) {
            text = getTransformationMethod().getTransformation(text, this);
        }

        // Get the text view's paint object
        TextPaint textPaint = getPaint();

        // Store the current text size
        float oldTextSize = textPaint.getTextSize();
        // If there is a max text size set, use the lesser of that and the default text size
        float targetTextSize = mMaxTextSize > 0 ? Math.min(mTextSize, mMaxTextSize) : mTextSize;

        // Get the required text height
        int textHeight = getTextHeight(text, textPaint, width, targetTextSize);

        mTimer.addSplit("beforeScaling");
        if (textHeight > height && targetTextSize > mMinTextSize) {
            // Until we either fit within our text view or we had reached our min text size, incrementally try smaller sizes
            while (textHeight > height && targetTextSize > mMinTextSize) {
                // to make search faster let's use "textHeight / height" factor for the step (it is always > 1)
                final float factor = textHeight / height;
                targetTextSize = Math.max((float)Math.floor(targetTextSize - mStep * factor), mMinTextSize);
                textHeight = getTextHeight(text, textPaint, width, targetTextSize);
            }
        } else if (textHeight < height) {
            // Try bigger sizes until we fill the view
            float newTargetTextSize = targetTextSize;
            int newTextHeight = textHeight;
            while (newTextHeight < height) {
                // use last values which don't exceed view dimensions
                targetTextSize = newTargetTextSize;
                textHeight = newTextHeight;

                // to make search faster let's use "height / newTextHeight" factor for the step (it is always > 1)
                final float factor = height / newTextHeight;
                newTargetTextSize = (float) Math.floor(newTargetTextSize + mStep * factor);
                newTextHeight = getTextHeight(text, textPaint, width, newTargetTextSize);
            }
        }
        mTimer.addSplit("scaling");

        // If we had reached our minimum text size and still don't fit, append an ellipsis
        if (mAddEllipsis && targetTextSize == mMinTextSize && textHeight > height) {
            // Draw using a static layout
            // modified: use a copy of TextPaint for measuring
            TextPaint paint = new TextPaint(textPaint);
            // Draw using a static layout
            StaticLayout layout = new StaticLayout(text, paint, width, Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false);
            // Check that we have a least one line of rendered text
            if (layout.getLineCount() > 0) {
                // Since the line at the specific vertical position would be cut off,
                // we must trim up to the previous line
                int lastLine = layout.getLineForVertical(height) - 1;
                // If the text would not even fit on a single line, clear it
                if (lastLine < 0) {
                    setText("");
                }
                // Otherwise, trim to the previous line and add an ellipsis
                else {
                    int start = layout.getLineStart(lastLine);
                    int end = layout.getLineEnd(lastLine);
                    float lineWidth = layout.getLineWidth(lastLine);
                    float ellipseWidth = textPaint.measureText(mEllipsis);

                    // Trim characters off until we have enough room to draw the ellipsis
                    while (width < lineWidth + ellipseWidth) {
                        lineWidth = textPaint.measureText(text.subSequence(start, --end + 1).toString());
                    }
                    setText(text.subSequence(0, end) + mEllipsis);
                }
            }
        }
        mTimer.addSplit("ellipsising");

        // Some devices try to auto adjust line spacing, so force default line spacing
        // and invalidate the layout as a side effect
        setTextSize(TypedValue.COMPLEX_UNIT_PX, targetTextSize);
        setLineSpacing(mSpacingAdd, mSpacingMult);

        // Notify the listener if registered
        if (mTextResizeListener != null) {
            mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
        }

        // Reset force resize flag
        mNeedsResize = false;
        mTimer.dumpToLog();
    }

    // Set the text size of the text paint object and use a static layout to render text off screen before measuring
    private int getTextHeight(CharSequence source, TextPaint paint, int width, float textSize) {
        // modified: make a copy of the original TextPaint object for measuring
        // (apparently the object gets modified while measuring, see also the
        // docs for TextView.getPaint() (which states to access it read-only)
        tmpPaint.set(paint);
        // Update the text paint object
        tmpPaint.setTextSize(textSize);
        // Measure using a static layout
        StaticLayout layout = new StaticLayout(source, tmpPaint, width, Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, true);
        return layout.getHeight();
    }

    // Interface for resize notifications
    public interface OnTextResizeListener {
        void onTextResize(TextView textView, float oldSize, float newSize);
    }

}