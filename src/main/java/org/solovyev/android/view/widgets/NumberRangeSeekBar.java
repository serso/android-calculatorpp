package org.solovyev.android.view.widgets;

import android.content.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.Converter;
import org.solovyev.common.utils.Mapper;
import org.solovyev.common.utils.NumberValuer;

import java.math.BigDecimal;

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


	/**
	 * Utility enumeration used to convert between Numbers and doubles.
	 *
	 * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
	 */
	private static enum NumberType {

		LONG(Long.class),
		DOUBLE(Double.class),
		INTEGER(Integer.class),
		FLOAT(Float.class),
		SHORT(Short.class),
		BYTE(Byte.class),
		BIG_DECIMAL(BigDecimal.class);

		@NotNull
		private final Class underlyingClass;

		private NumberType(@NotNull Class underlyingClass) {
			this.underlyingClass = underlyingClass;
		}

		@NotNull
		public static <E extends Number> NumberType fromNumber(E value) throws IllegalArgumentException {

			for (NumberType numberType : NumberType.values()) {
				if (numberType.underlyingClass.isInstance(value)) {
					return numberType;
				}
			}

			throw new IllegalArgumentException("Number class '" + value.getClass().getName() + "' is not supported");
		}

		public <T extends Number> T toNumber(double value) {

			switch (this) {
				case LONG:
					return (T)new Long((long) value);
				case DOUBLE:
					return (T)new Double(value);
				case INTEGER:
					return (T)new Integer((int) value);
				case FLOAT:
					return (T)new Float((float) value);
				case SHORT:
					return (T)new Short((short) value);
				case BYTE:
					return (T)new Byte((byte) value);
				case BIG_DECIMAL:
					return (T)new BigDecimal(value);
			}

			throw new InstantiationError("can't convert " + this + " to a Number object");
		}
	}
}
