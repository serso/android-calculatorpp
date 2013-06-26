/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.math;

import jscl.JsclMathEngine;
import jscl.NumeralBase;
import jscl.math.function.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.solovyev.android.calculator.CalculatorParseException;
import org.solovyev.android.calculator.Locator;
import org.solovyev.common.JPredicate;
import org.solovyev.common.collections.Collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public enum MathType {

	numeral_base(50, true, false, MathGroupType.number) {

		private final List<String> tokens = new ArrayList<String>(10);
		{
			for (NumeralBase numeralBase : NumeralBase.values()) {
				tokens.add(numeralBase.getJsclPrefix());
			}
		}

		@Nonnull
		@Override
		public List<String> getTokens() {
			return tokens;
		}
	},

	dot(200, true, true, MathGroupType.number, ".") {
		@Override
		public boolean isNeedMultiplicationSignBefore(@Nonnull MathType mathTypeBefore) {
			return super.isNeedMultiplicationSignBefore(mathTypeBefore) && mathTypeBefore != digit;
		}
	},

	grouping_separator(250, false, false, MathGroupType.number, "'", " ") {
		@Override
		public int processToJscl(@Nonnull StringBuilder result, int i, @Nonnull String match) throws CalculatorParseException {
			return i;
		}
	},

	power_10(300, false, false, MathGroupType.number, "E"),

	postfix_function(400, false, true, MathGroupType.function) {
		@Nonnull
		@Override
		public List<String> getTokens() {
			return Locator.getInstance().getEngine().getPostfixFunctionsRegistry().getNames();
		}
	},

	unary_operation(500, false, false, MathGroupType.operation, "-", "="),
	binary_operation(600, false, false, MathGroupType.operation, "-", "+", "*", "×", "∙", "/", "^") {
		@Override
		protected String getSubstituteToJscl(@Nonnull String match) {
			if (match.equals("×") || match.equals("∙")) {
				return "*";
			} else {
				return null;
			}
		}
	},

	open_group_symbol(800, true, false, MathGroupType.other, "[", "(", "{") {
		@Override
		public boolean isNeedMultiplicationSignBefore(@Nonnull MathType mathTypeBefore) {
			return super.isNeedMultiplicationSignBefore(mathTypeBefore) && mathTypeBefore != function && mathTypeBefore != operator;
		}

		@Override
		protected String getSubstituteToJscl(@Nonnull String match) {
			return "(";
		}
	},

	close_group_symbol(900, false, true, MathGroupType.other, "]", ")", "}") {
		@Override
		public boolean isNeedMultiplicationSignBefore(@Nonnull MathType mathTypeBefore) {
			return false;
		}

		@Override
		protected String getSubstituteToJscl(@Nonnull String match) {
			return ")";
		}
	},

	function(1000, true, true, MathGroupType.function) {
		@Nonnull
		@Override
		public List<String> getTokens() {
			return Locator.getInstance().getEngine().getFunctionsRegistry().getNames();
		}
	},

	operator(1050, true, true, MathGroupType.function) {
		@Nonnull
		@Override
		public List<String> getTokens() {
			return Locator.getInstance().getEngine().getOperatorsRegistry().getNames();
		}
	},

	constant(1100, true, true, MathGroupType.other) {
		@Nonnull
		@Override
		public List<String> getTokens() {
			return Locator.getInstance().getEngine().getVarsRegistry().getNames();
		}

		@Override
		protected String getSubstituteFromJscl(@Nonnull String match) {
			return Constants.INF_2.getName().equals(match) ? MathType.INFINITY : super.getSubstituteFromJscl(match);
		}
	},

	digit(1125, true, true, MathGroupType.number) {

		private final List<String> tokens = new ArrayList<String>(16);
		{
			for (Character character : NumeralBase.hex.getAcceptableCharacters()) {
				tokens.add(character.toString());
			}
		}

		@Override
		public boolean isNeedMultiplicationSignBefore(@Nonnull MathType mathTypeBefore) {
			return super.isNeedMultiplicationSignBefore(mathTypeBefore) && mathTypeBefore != digit && mathTypeBefore != dot /*&& mathTypeBefore != numeral_base*/;
		}

		@Nonnull
		@Override
		public List<String> getTokens() {
			return tokens;
		}
	},

	comma(1150, false, false, MathGroupType.other, ","),

	text(1200, false, false, MathGroupType.other) {
		@Override
		public int processToJscl(@Nonnull StringBuilder result, int i, @Nonnull String match) {
			if (match.length() > 0) {
				result.append(match.charAt(0));
			}
			return i;
		}

		@Override
		public int processFromJscl(@Nonnull StringBuilder result, int i, @Nonnull String match) {
			if (match.length() > 0) {
				result.append(match.charAt(0));
			}
			return i;
		}
	};

	public static enum MathGroupType {
		function,
		number,
		operation,
		other
	}

	@Nonnull
	private final List<String> tokens;

	@Nonnull
	private final Integer priority;

	private final boolean needMultiplicationSignBefore;

	private final boolean needMultiplicationSignAfter;

	@Nonnull
	private final MathGroupType groupType;

	MathType(@Nonnull Integer priority,
			 boolean needMultiplicationSignBefore,
			 boolean needMultiplicationSignAfter,
			 @Nonnull MathGroupType groupType,
			 @Nonnull String... tokens) {
		this(priority, needMultiplicationSignBefore, needMultiplicationSignAfter, groupType, Collections.asList(tokens));
	}

	MathType(@Nonnull Integer priority,
			 boolean needMultiplicationSignBefore,
			 boolean needMultiplicationSignAfter,
			 @Nonnull MathGroupType groupType,
			 @Nonnull List<String> tokens) {
		this.priority = priority;
		this.needMultiplicationSignBefore = needMultiplicationSignBefore;
		this.needMultiplicationSignAfter = needMultiplicationSignAfter;
		this.groupType = groupType;
		this.tokens = java.util.Collections.unmodifiableList(tokens);
	}

	@Nonnull
	public MathGroupType getGroupType() {
		return groupType;
	}

	/*	public static int getPostfixFunctionStart(@Nonnull CharSequence s, int position) throws ParseException {
		assert s.length() > position;

		int numberOfOpenGroups = 0;
		int result = position;
		for (; result >= 0; result--) {

			final MathType mathType = getType(s.toString(), result).getMathType();

			if (Collections.contains(mathType, digit, dot, grouping_separator, power_10)) {
				// continue
			} else if (mathType == close_group_symbol) {
				numberOfOpenGroups++;
			} else if (mathType == open_group_symbol) {
				if (numberOfOpenGroups > 0) {
					numberOfOpenGroups--;
				} else {
					 break;
				}
			} else {
				if (stop(s, numberOfOpenGroups, result)) break;
			}
		}

		if (numberOfOpenGroups != 0){
			throw new ParseException("Could not find start of prefix function!");
		}

		return result;
	}

	public static boolean stop(CharSequence s, int numberOfOpenGroups, int i) {
		if (numberOfOpenGroups == 0) {
			if (i > 0) {
				final EndsWithFinder endsWithFinder = new EndsWithFinder(s);
				endsWithFinder.setI(i + 1);
				if (!Collections.contains(function.getTokens(), FilterType.included, endsWithFinder)) {
					MathType type = getType(s.toString(), i).getMathType();
					if (type != constant) {
						return true;
					}
				}
			} else {
				return true;
			}
		}

		return false;
	}*/

	@Nonnull
	public List<String> getTokens() {
		return tokens;
	}

	private boolean isNeedMultiplicationSignBefore() {
		return needMultiplicationSignBefore;
	}

	private boolean isNeedMultiplicationSignAfter() {
		return needMultiplicationSignAfter;
	}

	public boolean isNeedMultiplicationSignBefore(@Nonnull MathType mathTypeBefore) {
		return needMultiplicationSignBefore && mathTypeBefore.isNeedMultiplicationSignAfter();
	}

	public int processToJscl(@Nonnull StringBuilder result, int i, @Nonnull String match) throws CalculatorParseException {
		final String substitute = getSubstituteToJscl(match);
		result.append(substitute == null ? match : substitute);
		return returnI(i, match);
	}

	protected int returnI(int i, @Nonnull String match) {
		if (match.length() > 1) {
			return i + match.length() - 1;
		} else {
			return i;
		}
	}

	public int processFromJscl(@Nonnull StringBuilder result, int i, @Nonnull String match) {
		final String substitute = getSubstituteFromJscl(match);
		result.append(substitute == null ? match : substitute);
		return returnI(i, match);
	}

	@Nullable
	protected String getSubstituteFromJscl(@Nonnull String match) {
		return null;
	}

	@Nullable
	protected String getSubstituteToJscl(@Nonnull String match) {
		return null;
	}

	public static final List<String> openGroupSymbols = Arrays.asList("[]", "()", "{}");

	public final static Character POWER_10 = 'E';

	public static final String IMAGINARY_NUMBER = "i";
	public static final String IMAGINARY_NUMBER_JSCL = "√(-1)";

	public static final String PI = "π";
	public static final String E = "e";
	public static final String C = "c";
	public static final Double C_VALUE = 299792458d;
	public static final String G = "G";
	public static final Double G_VALUE = 6.6738480E-11;
	public static final String H_REDUCED = "h";
	public static final Double H_REDUCED_VALUE = 6.6260695729E-34 / (2 * Math.PI);
	public final static String NAN = "NaN";

	public final static String INFINITY = "∞";
	public final static String INFINITY_JSCL = "Infinity";


	/**
	 * Method determines mathematical entity type for text substring starting from ith index
	 *
	 * @param text    analyzed text
	 * @param i       index which points to start of substring
	 * @param hexMode
	 * @return math entity type of substring starting from ith index of specified text
	 */
	@Nonnull
	public static Result getType(@Nonnull String text, int i, boolean hexMode) {
		if (i < 0) {
			throw new IllegalArgumentException("I must be more or equals to 0.");
		} else if (i >= text.length() && i != 0) {
			throw new IllegalArgumentException("I must be less than size of text.");
		} else if (i == 0 && text.length() == 0) {
			return new Result(MathType.text, text);
		}

		final StartsWithFinder startsWithFinder = new StartsWithFinder(text, i);

		for (MathType mathType : getMathTypesByPriority()) {
			final String s = Collections.find(mathType.getTokens(), startsWithFinder);
			if (s != null) {
				if (s.length() == 1) {
					if (hexMode || JsclMathEngine.getInstance().getNumeralBase() == NumeralBase.hex) {
						final Character ch = s.charAt(0);
						if (NumeralBase.hex.getAcceptableCharacters().contains(ch)) {
							return new Result(MathType.digit, s);
						}
					}
				}
				return new Result(mathType, s);
			}
		}

		return new Result(MathType.text, text.substring(i));
	}


	private static List<MathType> mathTypesByPriority;

	@Nonnull
	private static List<MathType> getMathTypesByPriority() {
		if (mathTypesByPriority == null) {
			final List<MathType> result = Collections.asList(MathType.values());

			java.util.Collections.sort(result, new Comparator<MathType>() {
				@Override
				public int compare(MathType l, MathType r) {
					return l.priority.compareTo(r.priority);
				}
			});

			mathTypesByPriority = result;
		}

		return mathTypesByPriority;
	}

	public static class Result {

		@Nonnull
		private final MathType mathType;

		@Nonnull
		private final String match;

		public Result(@Nonnull MathType mathType, @Nonnull String match) {
			this.mathType = mathType;

			this.match = match;
		}

		public int processToJscl(@Nonnull StringBuilder result, int i) throws CalculatorParseException {
			return mathType.processToJscl(result, i, match);
		}

		public int processFromJscl(@Nonnull StringBuilder result, int i) {
			return mathType.processFromJscl(result, i, match);
		}

		@Nonnull
		public String getMatch() {
			return match;
		}

		@Nonnull
		public MathType getMathType() {
			return mathType;
		}
	}

	private static class EndsWithFinder implements JPredicate<String> {

		private int i;

		@Nonnull
		private final CharSequence targetString;

		private EndsWithFinder(@Nonnull CharSequence targetString) {
			this.targetString = targetString;
		}

		@Override
		public boolean apply(@Nullable String s) {
			return targetString.subSequence(0, i).toString().endsWith(s);
		}

		public void setI(int i) {
			this.i = i;
		}
	}

	private static class StartsWithFinder implements JPredicate<String> {

		private int i;

		@Nonnull
		private final String targetString;

		public StartsWithFinder(@Nonnull String targetString, int i) {
			this.targetString = targetString;
			this.i = i;
		}

		@Override
		public boolean apply(@Nullable String s) {
			return s != null && targetString.startsWith(s, i);
		}

		public void setI(int i) {
			this.i = i;
		}
	}
}
