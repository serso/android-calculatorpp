/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Utility enumeration used to convert between Numbers and doubles.
 *
 * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
 */
enum NumberType {

	LONG(Long.class),
	DOUBLE(Double.class),
	INTEGER(Integer.class),
	FLOAT(Float.class),
	SHORT(Short.class),
	BYTE(Byte.class),
	BIG_DECIMAL(BigDecimal.class);

	@NotNull
	private final Class underlyingClass;

	NumberType(@NotNull Class underlyingClass) {
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
