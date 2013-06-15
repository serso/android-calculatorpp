/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import jscl.MathContext;
import jscl.MathEngine;
import jscl.NumeralBase;
import jscl.math.numeric.Real;
import jscl.text.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.common.MutableObject;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 10/23/11
 * Time: 2:57 PM
 */
public class NumberBuilder extends AbstractNumberBuilder {

	public NumberBuilder(@Nonnull CalculatorEngine engine) {
		super(engine);
	}

	/**
	 * Method replaces number in text according to some rules (e.g. formatting)
	 *
	 * @param text           text where number can be replaced
	 * @param mathTypeResult math type result of current token
	 * @param offset         offset between new number length and old number length (newNumberLength - oldNumberLength)
	 * @return new math type result (as one can be changed due to substituting of number with constant)
	 */
	@Nonnull
	public MathType.Result process(@Nonnull StringBuilder text, @Nonnull MathType.Result mathTypeResult, @Nullable MutableObject<Integer> offset) {
		final MathType.Result possibleResult;
		if (canContinue(mathTypeResult)) {
			// let's continue building number
			if (numberBuilder == null) {
				// if new number => create new builder
				numberBuilder = new StringBuilder();
			}

			if (mathTypeResult.getMathType() != MathType.numeral_base) {
				// just add matching string
				numberBuilder.append(mathTypeResult.getMatch());
			} else {
				// set explicitly numeral base (do not include it into number)
				nb = NumeralBase.getByPrefix(mathTypeResult.getMatch());
			}

			possibleResult = null;
		} else {
			// process current number (and go to the next one)
			possibleResult = processNumber(text, offset);
		}

		return possibleResult == null ? mathTypeResult : possibleResult;
	}

	/**
	 * Method replaces number in text according to some rules (e.g. formatting)
	 *
	 * @param text   text where number can be replaced
	 * @param offset offset between new number length and old number length (newNumberLength - oldNumberLength)
	 * @return new math type result (as one can be changed due to substituting of number with constant)
	 */
	@Nullable
	public MathType.Result processNumber(@Nonnull StringBuilder text, @Nullable MutableObject<Integer> offset) {
		// total number of trimmed chars
		int trimmedChars = 0;

		String number = null;

		// toXml numeral base (as later it might be replaced)
		final NumeralBase localNb = getNumeralBase();

		if (numberBuilder != null) {
			try {
				number = numberBuilder.toString();

				// let's get rid of unnecessary characters (grouping separators, + after E)
				final List<String> tokens = new ArrayList<String>();
				tokens.addAll(MathType.grouping_separator.getTokens());
				// + after E can be omitted: 10+E = 10E (NOTE: - cannot be omitted )
				tokens.add("+");
				for (String groupingSeparator : tokens) {
					final String trimmedNumber = number.replace(groupingSeparator, "");
					trimmedChars += number.length() - trimmedNumber.length();
					number = trimmedNumber;
				}

				// check if number still valid
				toDouble(number, getNumeralBase(), engine.getMathEngine0());

			} catch (NumberFormatException e) {
				// number is not valid => stop
				number = null;
			}

			numberBuilder = null;

			// must set default numeral base (exit numeral base mode)
			nb = engine.getNumeralBase();
		}

		return replaceNumberInText(text, number, trimmedChars, offset, localNb, engine.getMathEngine0());
	}

	@Nullable
	private static MathType.Result replaceNumberInText(@Nonnull StringBuilder text,
													   @Nullable String number,
													   int trimmedChars,
													   @Nullable MutableObject<Integer> offset,
													   @Nonnull NumeralBase nb,
													   @Nonnull final MathEngine engine) {
		MathType.Result result = null;

		if (number != null) {
			// in any case remove old number from text
			final int oldNumberLength = number.length() + trimmedChars;
			text.delete(text.length() - oldNumberLength, text.length());

			final String newNumber = formatNumber(number, nb, engine);
			if (offset != null) {
				// register offset between old number and new number
				offset.setObject(newNumber.length() - oldNumberLength);
			}
			text.append(newNumber);
		}

		return result;
	}

	@Nonnull
	private static String formatNumber(@Nonnull String number, @Nonnull NumeralBase nb, @Nonnull MathEngine engine) {
		String result;

		int indexOfDot = number.indexOf('.');

		if (indexOfDot < 0) {
			int indexOfE;
			if (nb == NumeralBase.hex) {
				indexOfE = -1;
			} else {
				indexOfE = number.indexOf(MathType.POWER_10);
			}
			if (indexOfE < 0) {
				result = engine.addGroupingSeparators(nb, number);
			} else {
				final String partBeforeE;
				if (indexOfE != 0) {
					partBeforeE = engine.addGroupingSeparators(nb, number.substring(0, indexOfE));
				} else {
					partBeforeE = "";
				}
				result = partBeforeE + number.substring(indexOfE);
			}
		} else {
			final String integerPart;
			if (indexOfDot != 0) {
				integerPart = engine.addGroupingSeparators(nb, number.substring(0, indexOfDot));
			} else {
				integerPart = "";
			}
			result = integerPart + number.substring(indexOfDot);
		}

		return result;
	}

	@Nonnull
	private static Double toDouble(@Nonnull String s, @Nonnull NumeralBase nb, @Nonnull final MathContext mc) throws NumberFormatException {
		final NumeralBase defaultNb = mc.getNumeralBase();
		try {
			mc.setNumeralBase(nb);

			try {
				return JsclIntegerParser.parser.parse(Parser.Parameters.newInstance(s, new MutableInt(0), mc), null).content().doubleValue();
			} catch (ParseException e) {
				try {
					return ((Real) DoubleParser.parser.parse(Parser.Parameters.newInstance(s, new MutableInt(0), mc), null).content()).doubleValue();
				} catch (ParseException e1) {
					throw new NumberFormatException();
				}
			}

		} finally {
			mc.setNumeralBase(defaultNb);
		}
	}
}
