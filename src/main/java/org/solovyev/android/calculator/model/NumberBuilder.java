/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import jscl.MathEngine;
import jscl.NumeralBase;
import jscl.math.numeric.Numeric;
import jscl.math.numeric.Real;
import jscl.text.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.Finder;
import org.solovyev.common.utils.MutableObject;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 10/23/11
 * Time: 2:57 PM
 */
public class NumberBuilder {

	@Nullable
	private StringBuilder numberBuilder = null;
	@Nullable
	private String number = null;

	private final boolean simpleFormat;

	@NotNull
	private final NumeralBase defaultNumeralBase;

	@Nullable
	private NumeralBase nb;

	public NumberBuilder(boolean simpleFormat, @NotNull NumeralBase defaultNumeralBase) {
		this.simpleFormat = simpleFormat;
		this.defaultNumeralBase = defaultNumeralBase;
		this.nb = defaultNumeralBase;
	}

	@NotNull
	public MathType.Result process(@NotNull StringBuilder sb, @NotNull MathType.Result mathTypeResult, @Nullable MutableObject<Integer> numberOffset) {
		number = null;

		final MathType.Result possibleResult;
		if ((CollectionsUtils.contains(mathTypeResult.getMathType(), MathType.digit, MathType.numeral_base, MathType.dot, MathType.grouping_separator, MathType.power_10) ||
				isSignAfterE(mathTypeResult)) && numeralBaseCheck(mathTypeResult) && numeralBaseInTheStart(mathTypeResult.getMathType())) {
			if (numberBuilder == null) {
				numberBuilder = new StringBuilder();
			}

			if (mathTypeResult.getMathType() != MathType.numeral_base) {
				numberBuilder.append(mathTypeResult.getMatch());
			} else {
				nb = NumeralBase.getByPrefix(mathTypeResult.getMatch());
			}

			possibleResult = null;
		} else {
			possibleResult = process(sb, numberOffset);
		}

		return possibleResult == null ? mathTypeResult : possibleResult;
	}

	private boolean numeralBaseInTheStart(@NotNull MathType mathType) {
		return mathType != MathType.numeral_base || numberBuilder == null;
	}

	private boolean numeralBaseCheck( @NotNull MathType.Result mathType ) {
		if ( mathType.getMathType() == MathType.digit ) {
			final Character ch = mathType.getMatch().charAt(0);
			if ( NumeralBase.hex.getAcceptableCharacters().contains(ch) && !NumeralBase.dec.getAcceptableCharacters().contains(ch) ) {
				if ( nb == NumeralBase.hex ) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	private boolean isSignAfterE(@NotNull MathType.Result mathTypeResult) {
		if ("-".equals(mathTypeResult.getMatch()) || "+".equals(mathTypeResult.getMatch())) {
			if (numberBuilder != null && numberBuilder.length() > 0) {
				if (numberBuilder.charAt(numberBuilder.length() - 1) == MathType.POWER_10) {
					return true;
				}
			}
		}
		return false;
	}


	@Nullable
	public MathType.Result process(@NotNull StringBuilder sb, @Nullable MutableObject<Integer> numberOffset) {
		int numberOfTokens = 0;

		final NumeralBase localNb;
		if (numberBuilder != null) {
			try {
				number = numberBuilder.toString();
				List<String> tokens = new ArrayList<String>();
				tokens.addAll(MathType.grouping_separator.getTokens());
				tokens.add("+");
				for (String groupingSeparator : tokens) {
					String newNumber = number.replace(groupingSeparator, "");
					numberOfTokens += number.length() - newNumber.length();
					number = newNumber;
				}
				
				toDouble(number, getNumeralBase());
				
			} catch (NumberFormatException e) {
				number = null;
			}

			numberBuilder = null;
			localNb = getNumeralBase();
			nb = defaultNumeralBase;
		} else {
			number = null;
			localNb = getNumeralBase();
		}

		return replaceSystemVars(sb, number, numberOfTokens, numberOffset, localNb, simpleFormat);
	}

	@Nullable
	private static MathType.Result replaceSystemVars(StringBuilder sb, String number, int numberOfTokens, @Nullable MutableObject<Integer> numberOffset, @NotNull NumeralBase nb, boolean simpleFormat) {
		MathType.Result result = null;

		if (number != null) {
			final String finalNumber = number;
			final Var var = CollectionsUtils.find(CalculatorEngine.instance.getVarsRegister().getSystemEntities(), new Finder<Var>() {
				@Override
				public boolean isFound(@Nullable Var var) {
					return var != null && finalNumber.equals(var.getValue());
				}
			});

			if (var != null) {
				sb.delete(sb.length() - number.length() - numberOfTokens, sb.length());
				sb.append(var.getName());
				result = new MathType.Result(MathType.constant, var.getName());
			} else {
				sb.delete(sb.length() - number.length() - numberOfTokens, sb.length());

				final String formattedNumber;

				if (!simpleFormat) {
					int indexOfDot = number.indexOf('.');

					if (indexOfDot < 0) {
						int indexOfE;
						if (nb == NumeralBase.hex) {
							indexOfE = -1;
						} else {
							indexOfE = number.indexOf('E');
						}
						if (indexOfE < 0) {
							formattedNumber = toString(toDouble(number, nb), nb);
						} else {
							final String part;
							if (indexOfDot != 0) {
								part = toString(toDouble(number.substring(0, indexOfE), nb), nb);
							} else {
								part = "";
							}
							formattedNumber = part + number.substring(indexOfE);
						}
					} else {
						final String integerPart;
						if (indexOfDot != 0) {
							integerPart = toString(toDouble(number.substring(0, indexOfDot), nb), nb);
						} else {
							integerPart = "";
						}
						formattedNumber = integerPart + number.substring(indexOfDot);
					}
				} else {
					formattedNumber = toString(toDouble(number, nb), nb);
				}

				if (numberOffset != null) {
					numberOffset.setObject(formattedNumber.length() - number.length() - numberOfTokens);
				}
				sb.append(formattedNumber);
			}
		}

		return result;
	}

	@NotNull
	private static String toString(@NotNull Double value, @NotNull NumeralBase nb) {
		return CalculatorEngine.instance.getEngine().format(value, nb);
	}

	public boolean isHexMode() {
		return nb == NumeralBase.hex || ( nb == null && defaultNumeralBase == NumeralBase.hex);
	}

	@NotNull
	private NumeralBase getNumeralBase(){
		return nb == null ? defaultNumeralBase : nb;
	}
	
	@NotNull
	private static Double toDouble(@NotNull String s, @NotNull NumeralBase nb) throws NumberFormatException{

		final MathEngine me = CalculatorEngine.instance.getEngine();

		final NumeralBase defaultNb = me.getNumeralBase();
		try {
			me.setNumeralBase(nb);

			try {
				return JsclIntegerParser.parser.parse(Parser.Parameters.newInstance(s, new MutableInt(0), me), null).content().doubleValue();
			} catch (ParseException e) {
				try {
					return ((Real) DoubleParser.parser.parse(Parser.Parameters.newInstance(s, new MutableInt(0), me), null).content()).doubleValue();
				} catch (ParseException e1) {
					throw new NumberFormatException();
				}
			}

		} finally {
			me.setNumeralBase(defaultNb);
		}
	}
}
