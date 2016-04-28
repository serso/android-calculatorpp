package org.solovyev.android.views;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.SeekBar;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.R;

/**
 * SeekBar for discrete values with a label displayed underneath the active tick
 */
public class DiscreteSeekBar extends SeekBar {
    // Duration of how quick the SeekBar thumb should snap to its destination value
    private static final int THUMB_SNAP_DURATION_TIME = 100;
    private final Paint mPaint = new Paint();
    private ObjectAnimator mObjectAnimator;
    private OnChangeListener mOnChangeListener;
    private int mCurrentTick = 0;
    private CharSequence[] mTickLabels;
    private ColorStateList mLabelColor;

    public DiscreteSeekBar(Context context) {
        super(context);
        init(context, null, 0);
    }

    public DiscreteSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public DiscreteSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.DiscreteSeekBar, defStyle, 0);
        mTickLabels = a.getTextArray(R.styleable.DiscreteSeekBar_values);
        final int labelsSize = a.getDimensionPixelSize(R.styleable.DiscreteSeekBar_labelsSize, 0);
        final ColorStateList labelsColor = a.getColorStateList(R.styleable.DiscreteSeekBar_labelsColor);
        a.recycle();

        Check.isNotNull(mTickLabels);
        Check.isTrue(mTickLabels.length > 0);
        Check.isTrue(labelsSize > 0);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(labelsSize);

        if (labelsColor != null) {
            setLabelColor(labelsColor);
        } else {
            mPaint.setColor(Color.BLACK);
        }

        // Extend the bottom padding to include tick label height (including descent in order to not
        // clip glyphs that extends below the baseline).
        Paint.FontMetricsInt fi = mPaint.getFontMetricsInt();
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(),
                getPaddingBottom() + labelsSize + fi.descent);

        super.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    cancelAnimator();
                }
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            private void cancelAnimator() {
                if (mObjectAnimator != null) {
                    mObjectAnimator.cancel();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mCurrentTick = getClosestTick(seekBar.getProgress());
                final int endProgress = getProgressForTick(mCurrentTick);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    startAnimator(seekBar, endProgress);
                } else {
                    seekBar.setProgress(endProgress);
                }
                if (mOnChangeListener != null) {
                    mOnChangeListener.onValueChanged(mCurrentTick);
                }
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            private void startAnimator(SeekBar seekBar, int endProgress) {
                mObjectAnimator = ObjectAnimator.ofInt(
                        seekBar, "progress", seekBar.getProgress(), endProgress);
                mObjectAnimator.setInterpolator(new DecelerateInterpolator());
                mObjectAnimator.setDuration(THUMB_SNAP_DURATION_TIME);
                mObjectAnimator.start();
            }
        });
    }

    private int getClosestTick(int progress) {
        float normalizedValue = (float) progress / getMax();
        return Math.round(normalizedValue * getMaxTick());
    }

    private int getProgressForTick(int tick) {
        return (getMax() / getMaxTick()) * tick;
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener seekBarChangeListener) {
        // It doesn't make sense to expose the interface for listening to intermediate changes.
        Check.isTrue(false);
    }

    /**
     * Get the largest tick value the SeekBar can represent
     *
     * @return maximum tick value
     */
    public int getMaxTick() {
        return mTickLabels.length - 1;
    }

    /**
     * Set listener for observing value changes
     *
     * @param onChangeListener listener that should receive updates
     */
    public void setOnChangeListener(OnChangeListener onChangeListener) {
        mOnChangeListener = onChangeListener;
    }

    /**
     * Set tick value
     *
     * @param tickValue tick value in range [0, maxTick]
     */
    public void setTickValue(int tickValue) {
        Check.isTrue(tickValue >= 0);
        Check.isTrue(tickValue <= getMaxTick());
        mCurrentTick = tickValue;
        setProgress(getProgressForTick(mCurrentTick));
    }

    public void setLabelColor(int color) {
        mLabelColor = ColorStateList.valueOf(color);
        updateLabelColor();
    }

    public void setLabelColor(ColorStateList colors) {
        mLabelColor = colors;
        updateLabelColor();
    }

    private void updateLabelColor() {
        int color = mLabelColor.getColorForState(getDrawableState(), Color.BLACK);
        if (color != mPaint.getColor()) {
            mPaint.setColor(color);
            invalidate();
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateLabelColor();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final float sliderWidth = getWidth() - getPaddingRight() - getPaddingLeft();
        final float sliderStepSize = sliderWidth / getMaxTick();
        int closestTick = getClosestTick(getProgress());
        String text = mTickLabels[closestTick].toString();
        final float startOffset = getPaddingLeft();
        final float tickLabelWidth = mPaint.measureText(text);
        final float tickPos = sliderStepSize * closestTick;
        final float labelOffset;
        // First step description text should be anchored with its left edge just
        // below the slider start tick. The last step description should be anchored
        // to the right just under the end tick. Tick labels in between are centered below
        // each tick.
        if (closestTick == 0) {
            labelOffset = startOffset;
        } else if (closestTick == getMaxTick()) {
            labelOffset = startOffset + sliderWidth - tickLabelWidth;
        } else {
            labelOffset = startOffset + tickPos - tickLabelWidth / 2;
        }
        // Text position is drawn from bottom left, with bottom at the font baseline. We need to
        // offset by the descent to cover e.g 'g' that extends below the baseline.
        final Paint.FontMetricsInt m = mPaint.getFontMetricsInt();
        final int lowestPosForFullGlyphCoverage = getHeight() - m.descent;
        canvas.drawText(text, labelOffset, lowestPosForFullGlyphCoverage, mPaint);
    }

    /**
     * Listener for observing tick changes
     */
    public interface OnChangeListener {
        void onValueChanged(int selectedTick);
    }
}
