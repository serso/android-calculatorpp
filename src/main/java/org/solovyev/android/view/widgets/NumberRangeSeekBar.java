package org.solovyev.android.view.widgets;

import android.content.Context;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.utils.Converter;
import org.solovyev.common.utils.NumberValuer;

/**
 * User: serso
 * Date: 9/19/11
 * Time: 4:26 PM
 */
public class NumberRangeSeekBar<T extends Number> extends AbstractRangeSeekBar<T> {

	@NotNull
	private final NumberType numberType;

	/**
	 * Creates a new RangeSeekBar.
	 *
	 * @param minValue The minimum value of the selectable range.
	 * @param maxValue The maximum value of the selectable range.
	 * @param context  parent context
	 * @throws IllegalArgumentException Will be thrown if min/max value types are not one of Long, Double, Integer, Float, Short, Byte or BigDecimal.
	 */
	public NumberRangeSeekBar(@NotNull T minValue, @NotNull T maxValue, Context context) throws IllegalArgumentException {
		super(minValue, maxValue, context);

		numberType = NumberType.fromNumber(minValue);

	}

	@NotNull
	@Override
	protected Converter<Double, T> getToTConverter() {
		return new Converter<Double, T>() {
			@NotNull
			@Override
			public T convert(@NotNull Double aDouble) {
				return (T) numberType.toNumber(aDouble);
			}
		};
	}

	@NotNull
	@Override
	protected Converter<T, Double> getToDoubleConverter() {
		return new NumberValuer<T>();
	}


}
