/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.jscl;

import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.calculator.model.ParseException;
import org.solovyev.android.calculator.model.TextProcessor;
import org.solovyev.common.utils.MathUtils;
import org.solovyev.util.math.Complex;

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
			final Double roundedValue = round(result);
			if ( roundedValue.isInfinite() ) {
				result =  MathType.INFINITY;
			} else {
				result = String.valueOf(roundedValue);
			}
		} catch (NumberFormatException e) {
			result = result.replace(MathType.INFINITY_JSCL, MathType.INFINITY);
			if (result.contains(MathType.IMAGINARY_NUMBER_JSCL)) {
				try {
					result = createResultForComplexNumber(result.replace(MathType.IMAGINARY_NUMBER_JSCL, MathType.IMAGINARY_NUMBER));
				} catch (NumberFormatException e1) {
					// throw original one
					throw new ParseException(e);
				}

			} else {
				throw new ParseException(e);
			}
		}

		return result;
	}

	protected String createResultForComplexNumber(@NotNull final String s) {
		final Complex complex = new Complex();

		String result = "";
		// may be it's just complex number
		int plusIndex = s.lastIndexOf("+");
		if (plusIndex >= 0) {
			complex.setReal(round(s.substring(0, plusIndex)));
			result += complex.getReal();
			result += "+";
		} else {
			plusIndex = s.lastIndexOf("-");
			if (plusIndex >= 0) {
				complex.setReal(round(s.substring(0, plusIndex)));
				result += complex.getReal();
				result += "-";
			}
		}


		int multiplyIndex = s.indexOf("*");
		if (multiplyIndex >= 0) {
			complex.setImaginary(round(s.substring(plusIndex >= 0 ? plusIndex + 1 : 0, multiplyIndex)));
			result += complex.getImaginary();

		}

		result += MathType.IMAGINARY_NUMBER;

		return result;
	}

	private Double round(@NotNull String result) {
		final Double dResult = Double.valueOf(result);
		return MathUtils.round(dResult, CalculatorEngine.instance.getNumberOfFractionDigits());
	}
}
