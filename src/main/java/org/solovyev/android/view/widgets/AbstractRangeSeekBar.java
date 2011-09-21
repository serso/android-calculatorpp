package org.solovyev.android.view.widgets;

/**
 * User: serso
 * Date: 9/19/11
 * Time: 3:30 PM
 */

import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.widget.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.R;
import org.solovyev.common.math.DiscreteNormalizer;
import org.solovyev.common.math.LinearNormalizer;
import org.solovyev.common.math.Normalizer;
import org.solovyev.common.utils.Converter;

/**
 * Widget that lets users select a minimum and maximum value on a given numerical range.
 * The range value types can be one of Long, Double, Integer, Float, Short, Byte or BigDecimal.
 *
 * @param <T> The Number type of the range values. One of Long, Double, Integer, Float, Short, Byte or BigDecimal.
 * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
 */
public abstract class AbstractRangeSeekBar<T> extends ImageView {

	@NotNull
	private final Paint paint = new Paint();

	@NotNull
	private final ThumbContainer tc;

	@NotNull
	private final Converter<T, Double> toDoubleConverter;

	@NotNull
	private final Converter<Double, T> toTConverter;

	@NotNull
	private final T minValue, maxValue;

	@NotNull
	private final Normalizer normalizer;

	private double normalizedMinValue = 0d;

	private double normalizedMaxValue = 1d;

	private Thumb pressedThumb = null;

	private boolean notifyWhileDragging = false;


	@Nullable
	private OnRangeSeekBarChangeListener<T> listener;

	/**
	 * Creates a new RangeSeekBar.
	 *
	 * @param minValue The minimum value of the selectable range.
	 * @param maxValue The maximum value of the selectable range.
	 * @param context
	 * @throws IllegalArgumentException Will be thrown if min/max value types are not one of Long, Double, Integer, Float, Short, Byte or BigDecimal.
	 */
	public AbstractRangeSeekBar(@NotNull T minValue, @NotNull T maxValue, @Nullable Integer steps, Context context) throws IllegalArgumentException {
		super(context);

		this.minValue = minValue;
		this.maxValue = maxValue;

		this.toDoubleConverter = getToDoubleConverter();
		this.toTConverter = getToTConverter();

		if (steps == null) {
			normalizer = new LinearNormalizer(toDoubleConverter.convert(minValue), toDoubleConverter.convert(maxValue));
		} else {
			normalizer = new DiscreteNormalizer(toDoubleConverter.convert(minValue), toDoubleConverter.convert(maxValue), steps);
		}

		tc = new ThumbContainer();
	}

	@NotNull
	protected abstract Converter<Double,T> getToTConverter();

	@NotNull
	protected abstract Converter<T,Double> getToDoubleConverter();

	public boolean isNotifyWhileDragging() {
		return notifyWhileDragging;
	}

	/**
	 * Should the widget notify the listener callback while the user is still dragging a thumb? Default is false.
	 *
	 * @param flag
	 */
	public void setNotifyWhileDragging(boolean flag) {
		this.notifyWhileDragging = flag;
	}

	/**
	 * Returns the absolute minimum value of the range that has been set at construction time.
	 *
	 * @return The absolute minimum value of the range.
	 */
	@NotNull
	public T getMinValue() {
		return minValue;
	}

	/**
	 * Returns the absolute maximum value of the range that has been set at construction time.
	 *
	 * @return The absolute maximum value of the range.
	 */
	@NotNull
	public T getMaxValue() {
		return maxValue;
	}

	/**
	 * Returns the currently selected min value.
	 *
	 * @return The currently selected min value.
	 */
	public T getSelectedMinValue() {
		return denormalizeValue(normalizedMinValue);
	}

	/**
	 * Sets the currently selected minimum value. The widget will be invalidated and redrawn.
	 *
	 * @param value The Number value to set the minimum value to. Will be clamped to given absolute minimum/maximum range.
	 */
	public void setSelectedMinValue(@NotNull T value) {
		setNormalizedMinValue(normalizeValue(value));
	}

	/**
	 * Returns the currently selected max value.
	 *
	 * @return The currently selected max value.
	 */
	public T getSelectedMaxValue() {
		return denormalizeValue(normalizedMaxValue);
	}

	/**
	 * Sets the currently selected maximum value. The widget will be invalidated and redrawn.
	 *
	 * @param value The Number value to set the maximum value to. Will be clamped to given absolute minimum/maximum range.
	 */
	public void setSelectedMaxValue(@NotNull T value) {
		setNormalizedMaxValue(normalizeValue(value));
	}

	/**
	 * Registers given listener callback to notify about changed selected values.
	 *
	 * @param listener The listener to notify about changed selected values.
	 */
	public void setOnRangeSeekBarChangeListener(OnRangeSeekBarChangeListener<T> listener) {
		this.listener = listener;
	}

	/**
	 * Handles thumb selection and movement. Notifies listener callback on certain events.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				pressedThumb = evalPressedThumb(event.getX());
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				if (pressedThumb != null) {
					if (Thumb.MIN.equals(pressedThumb)) {
						setNormalizedMinValue(convertToNormalizedValue(event.getX()));
					} else if (Thumb.MAX.equals(pressedThumb)) {
						setNormalizedMaxValue(convertToNormalizedValue(event.getX()));
					}
					if (notifyWhileDragging && listener != null) {
						listener.rangeSeekBarValuesChanged(getSelectedMinValue(), getSelectedMaxValue(), false);
					}
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				pressedThumb = null;
				invalidate();
				if (listener != null) {
					listener.rangeSeekBarValuesChanged(getSelectedMinValue(), getSelectedMaxValue(), true);
				}
				break;
		}
		return true;
	}

	/**
	 * Ensures correct size of the widget.
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = 200;
		if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
			width = MeasureSpec.getSize(widthMeasureSpec);
		}

		int height = tc.thumbImage.getHeight();
		if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
			height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
		}
		setMeasuredDimension(width, height);
	}

	/**
	 * Draws the widget on the given canvas.
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// draw seek bar background line
		final RectF rect = tc.getRect();
		paint.setStyle(Style.FILL);
		paint.setColor(Color.GRAY);
		canvas.drawRect(rect, paint);
		// draw seek bar active range line
		rect.left = convertToScreenValue(normalizedMinValue);
		rect.right = convertToScreenValue(normalizedMaxValue);
		// orange color
		paint.setColor(Color.rgb(255, 165, 0));
		canvas.drawRect(rect, paint);

		// draw minimum thumb
		drawThumb(convertToScreenValue(normalizedMinValue), Thumb.MIN == pressedThumb, canvas);

		// draw maximum thumb
		drawThumb(convertToScreenValue(normalizedMaxValue), Thumb.MAX == pressedThumb, canvas);
	}

	/**
	 * Draws the "normal" resp. "pressed" thumb image on specified x-coordinate.
	 *
	 * @param normalizedToScreenValue The x-coordinate in screen space where to draw the image.
	 * @param pressed	 Is the thumb currently in "pressed" state?
	 * @param canvas	  The canvas to draw upon.
	 */
	private void drawThumb(float normalizedToScreenValue, boolean pressed, Canvas canvas) {
		canvas.drawBitmap(tc.getImage(pressed), normalizedToScreenValue - tc.thumbHalfWidth, (float) ((0.5f * getHeight()) - tc.thumbHalfHeight), paint);
	}

	/**
	 * Decides which (if any) thumb is touched by the given x-coordinate.
	 *
	 * @param touchX The x-coordinate of a touch event in screen space.
	 * @return The pressed thumb or null if none has been touched.
	 */
	private Thumb evalPressedThumb(float touchX) {
		Thumb result = null;
		boolean minThumbPressed = isInThumbRange(touchX, normalizedMinValue);
		boolean maxThumbPressed = isInThumbRange(touchX, normalizedMaxValue);
		if (minThumbPressed && maxThumbPressed) {
			// if both thumbs are pressed (they lie on top of each other), choose the one with more room to drag. this avoids "stalling" the thumbs in a corner, not being able to drag them apart anymore.
			result = (touchX / getWidth() > 0.5f) ? Thumb.MIN : Thumb.MAX;
		} else if (minThumbPressed) {
			result = Thumb.MIN;
		} else if (maxThumbPressed) {
			result = Thumb.MAX;
		}
		return result;
	}

	/**
	 * Decides if given x-coordinate in screen space needs to be interpreted as "within" the normalized thumb x-coordinate.
	 *
	 * @param touchX			   The x-coordinate in screen space to check.
	 * @param normalizedThumbValue The normalized x-coordinate of the thumb to check.
	 * @return true if x-coordinate is in thumb range, false otherwise.
	 */
	private boolean isInThumbRange(float touchX, double normalizedThumbValue) {
		return Math.abs(touchX - convertToScreenValue(normalizedThumbValue)) <= tc.thumbHalfWidth;
	}

	/**
	 * Sets normalized min value to value so that 0 <= value <= normalized max value <= 1.
	 * The View will get invalidated when calling this method.
	 *
	 * @param value The new normalized min value to set.
	 */
	private void setNormalizedMinValue(double value) {
		normalizedMinValue = Math.max(0d, Math.min(1d, Math.min(value, normalizedMaxValue)));
		invalidate();
	}

	/**
	 * Sets normalized max value to value so that 0 <= normalized min value <= value <= 1.
	 * The View will get invalidated when calling this method.
	 *
	 * @param value The new normalized max value to set.
	 */
	private void setNormalizedMaxValue(double value) {
		normalizedMaxValue = Math.max(0d, Math.min(1d, Math.max(value, normalizedMinValue)));
		invalidate();
	}

	/**
	 * Converts a normalized value to a Number object in the value space between absolute minimum and maximum.
	 *
	 * @param normalized
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private T denormalizeValue(double normalized) {
		return toTConverter.convert(normalizer.denormalize(normalized));
	}

	/**
	 * Converts the given Number value to a normalized double.
	 *
	 * @param value The Number value to normalize.
	 * @return The normalized double.
	 */
	private double normalizeValue(T value) {
		return normalizer.normalize(toDoubleConverter.convert(value));
	}

	/**
	 * Converts a normalized value into screen space.
	 *
	 * @param normalizedValue The normalized value to convert.
	 * @return The converted value in screen space.
	 */
	private float convertToScreenValue(double normalizedValue) {
		return (float) (tc.padding + normalizedValue * (getWidth() - 2 * tc.padding));
	}

	/**
	 * Converts screen space x-coordinates into normalized values.
	 *
	 * @param screenValue The x-coordinate in screen space to convert.
	 * @return The normalized value.
	 */
	private double convertToNormalizedValue(float screenValue) {
		int width = getWidth();
		if (width <= 2 * tc.padding) {
			// prevent division by zero, simply return 0.
			return 0d;
		} else {
			double result = (screenValue - tc.padding) / (width - 2 * tc.padding);
			return Math.min(1d, Math.max(0d, result));
		}
	}

	/**
	 * Callback listener interface to notify about changed range values.
	 *
	 * @param <T> The Number type the RangeSeekBar has been declared with.
	 * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
	 */
	public interface OnRangeSeekBarChangeListener<T> {

		void rangeSeekBarValuesChanged(T minValue, T maxValue, boolean changeComplete);

	}

	/**
	 * Thumb constants (min and max).
	 *
	 * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
	 */
	private static enum Thumb {
		MIN, MAX
	}

	private class ThumbContainer {
		@NotNull
		private final Bitmap thumbImage = BitmapFactory.decodeResource(getResources(), R.drawable.seek_thumb_normal);

		@NotNull
		private final Bitmap thumbPressedImage = BitmapFactory.decodeResource(getResources(), R.drawable.seek_thumb_pressed);

		private final float thumbWidth = thumbImage.getWidth();

		private final float thumbHalfWidth = 0.5f * thumbWidth;

		private final float thumbHalfHeight = 0.5f * thumbImage.getHeight();

		private final float lineHeight = 0.3f * thumbHalfHeight;

		private final float padding = thumbHalfWidth;

		public RectF getRect() {
			return new RectF(padding, 0.5f * (getHeight() - lineHeight), getWidth() - padding, 0.5f * (getHeight() + lineHeight));
		}

		public Bitmap getImage(boolean pressed) {
			return pressed ? thumbPressedImage : thumbImage;
		}
	}

}
