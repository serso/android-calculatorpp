/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.jscl;

import jscl.text.msg.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.calculator.model.ParseException;
import org.solovyev.android.calculator.model.TextProcessor;
import org.solovyev.common.utils.StringUtils;

/**
 * User: serso
 * Date: 10/6/11
 * Time: 9:48 PM
 */
class FromJsclNumericTextProcessor implements TextProcessor<String> {

	@NotNull
	@Override
	public String process(@NotNull String result) throws ParseException {
		try {
			final Double doubleValue = Double.valueOf(result);

			if (doubleValue.isInfinite()) {
				result = MathType.INFINITY;
			} else {
				result = CalculatorEngine.instance.format(doubleValue);
			}
		} catch (NumberFormatException e) {
			result = result.replace(MathType.INFINITY_JSCL, MathType.INFINITY);
			try {
				result = createResultForComplexNumber(result.replace(MathType.IMAGINARY_NUMBER_JSCL, MathType.IMAGINARY_NUMBER));
			} catch (NumberFormatException e1) {
				// throw original one
				throw new ParseException(new jscl.text.ParseException(Messages.msg_8, 0, result, result));
			}
		}

		return result;
	}

	private String format(@NotNull String value) throws java.lang.NumberFormatException {
		return CalculatorEngine.instance.format(Double.valueOf(value));
	}

	protected String createResultForComplexNumber(@NotNull final String s) {
		final Complex complex = new Complex();

		final StringBuilder result = new StringBuilder();

		// may be it's just complex number
		int signIndex = tryRealPart(s, complex, result, "+");
		if (signIndex < 0) {
			signIndex = tryRealPart(s, complex, result, "-");
		}

		int multiplyIndex = s.indexOf("*");
		if (multiplyIndex >= 0) {
			complex.setImaginary(format(s.substring(signIndex >= 0 ? signIndex + 1 : 0, multiplyIndex)));
			result.append(complex.getImaginary());

		}

		result.append(MathType.IMAGINARY_NUMBER);

		return result.toString();
	}

	private int tryRealPart(@NotNull String s,
							@NotNull Complex complex,
							@NotNull StringBuilder result,
							@NotNull String sign) {
		int index = s.lastIndexOf(sign);
		if (index >= 0) {
			final String substring = s.substring(0, index);

			if (!StringUtils.isEmpty(substring)) {
				try {
					complex.setReal(format(substring));
					result.append(complex.getReal());
				} catch (NumberFormatException e) {
					// do nothing
				}
			}

			result.append(sign);
		}

		return index;
	}

	private class Complex {

		@Nullable
		private String real;

		@Nullable
		private String imaginary;

		@Nullable
		public String getReal() {
			return real;
		}

		public void setReal(@Nullable String real) {
			this.real = real;
		}

		@Nullable
		public String getImaginary() {
			return imaginary;
		}

		public void setImaginary(@Nullable String imaginary) {
			this.imaginary = imaginary;
		}
	}

}
