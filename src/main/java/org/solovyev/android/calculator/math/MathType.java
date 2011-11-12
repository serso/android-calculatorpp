/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.math;

import jscl.math.function.Constant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.StartsWithFinder;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.calculator.model.ParseException;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.Finder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public enum MathType {

	digit(100, true, true, "0", "1", "2", "3", "4", "5", "6", "7", "8", "9") {
		@Override
		public boolean isNeedMultiplicationSignBefore(@NotNull MathType mathTypeBefore) {
			return super.isNeedMultiplicationSignBefore(mathTypeBefore) && mathTypeBefore != digit && mathTypeBefore != dot;
		}
	},

	dot(200, true, true, ".") {
		@Override
		public boolean isNeedMultiplicationSignBefore(@NotNull MathType mathTypeBefore) {
			return super.isNeedMultiplicationSignBefore(mathTypeBefore) && mathTypeBefore != digit;
		}
	},

	grouping_separator(250, false, false, "'", " "){
		@Override
		public int processToJscl(@NotNull StringBuilder result, int i, @NotNull String match) throws ParseException {
			return i;
		}
	},

	power_10(300, false, false, "E"),

	postfix_function(400, false, true) {
		@NotNull
		@Override
		public List<String> getTokens() {
			return CalculatorEngine.instance.getPostfixFunctionsRegistry().getNames();
		}
	},

	unary_operation(500, false, false, "-", "="),
	binary_operation(600, false, false, "-", "+", "*", "×", "∙", "/", "^") {
		@Override
		protected String getSubstituteToJscl(@NotNull String match) {
			if (match.equals("×") || match.equals("∙")) {
				return "*";
			} else {
				return null;
			}
		}
	},

	open_group_symbol(800, true, false, "[", "(", "{") {
		@Override
		public boolean isNeedMultiplicationSignBefore(@NotNull MathType mathTypeBefore) {
			return super.isNeedMultiplicationSignBefore(mathTypeBefore) && mathTypeBefore != function;
		}

		@Override
		protected String getSubstituteToJscl(@NotNull String match) {
			return "(";
		}
	},

	close_group_symbol(900, false, true, "]", ")", "}") {
		@Override
		public boolean isNeedMultiplicationSignBefore(@NotNull MathType mathTypeBefore) {
			return false;
		}

		@Override
		protected String getSubstituteToJscl(@NotNull String match) {
			return ")";
		}
	},

	function(1000, true, true) {
		@NotNull
		@Override
		public List<String> getTokens() {
			return CalculatorEngine.instance.getFunctionsRegistry().getNames();
		}
	},

	constant(1100, true, true) {
		@NotNull
		@Override
		public List<String> getTokens() {
			return CalculatorEngine.instance.getVarsRegister().getNames();
		}

		@Override
		protected String getSubstituteFromJscl(@NotNull String match) {
			return Constant.INF_CONST2.getName().equals(match) ? MathType.INFINITY : super.getSubstituteFromJscl(match);
		}
	},

	text(1200, false, false) {
		@Override
		public int processToJscl(@NotNull StringBuilder result, int i, @NotNull String match) {
			if (match.length() > 0) {
				result.append(match.charAt(0));
			}
			return i;
		}

		@Override
		public int processFromJscl(@NotNull StringBuilder result, int i, @NotNull String match) {
			if (match.length() > 0) {
				result.append(match.charAt(0));
			}
			return i;
		}
	};

	@NotNull
	private final List<String> tokens;

	@NotNull
	private final Integer priority;

	private final boolean needMultiplicationSignBefore;

	private final boolean needMultiplicationSignAfter;

	MathType(@NotNull Integer priority,
			 boolean needMultiplicationSignBefore,
			 boolean needMultiplicationSignAfter,
			 @NotNull String... tokens) {
		this(priority, needMultiplicationSignBefore, needMultiplicationSignAfter, CollectionsUtils.asList(tokens));
	}

	MathType(@NotNull Integer priority,
			 boolean needMultiplicationSignBefore,
			 boolean needMultiplicationSignAfter,
			 @NotNull List<String> tokens) {
		this.priority = priority;
		this.needMultiplicationSignBefore = needMultiplicationSignBefore;
		this.needMultiplicationSignAfter = needMultiplicationSignAfter;
		this.tokens = Collections.unmodifiableList(tokens);
	}

/*	public static int getPostfixFunctionStart(@NotNull CharSequence s, int position) throws ParseException {
		assert s.length() > position;

		int numberOfOpenGroups = 0;
		int result = position;
		for (; result >= 0; result--) {

			final MathType mathType = getType(s.toString(), result).getMathType();

			if (CollectionsUtils.contains(mathType, digit, dot, grouping_separator, power_10)) {
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
				if (!CollectionsUtils.contains(function.getTokens(), FilterType.included, endsWithFinder)) {
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

	@NotNull
	public List<String> getTokens() {
		return tokens;
	}

	private boolean isNeedMultiplicationSignBefore() {
		return needMultiplicationSignBefore;
	}

	private boolean isNeedMultiplicationSignAfter() {
		return needMultiplicationSignAfter;
	}

	public boolean isNeedMultiplicationSignBefore(@NotNull MathType mathTypeBefore) {
		return needMultiplicationSignBefore && mathTypeBefore.isNeedMultiplicationSignAfter();
	}

	public int processToJscl(@NotNull StringBuilder result, int i, @NotNull String match) throws ParseException {
		final String substitute = getSubstituteToJscl(match);
		result.append(substitute == null ? match : substitute);
		return returnI(i, match);
	}

	protected int returnI(int i, @NotNull String match) {
		if (match.length() > 1) {
			return i + match.length() - 1;
		} else {
			return i;
		}
	}

	public int processFromJscl(@NotNull StringBuilder result, int i, @NotNull String match) {
		final String substitute = getSubstituteFromJscl(match);
		result.append(substitute == null ? match : substitute);
		return returnI(i, match);
	}

	@Nullable
	protected String getSubstituteFromJscl(@NotNull String match) {
		return null;
	}

	@Nullable
	protected String getSubstituteToJscl(@NotNull String match) {
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
	public static final Double H_REDUCED_VALUE = 6.6260695729E-34 / ( 2 * Math.PI );
	public final static String NAN = "NaN";

	public final static String INFINITY = "∞";
	public final static String INFINITY_JSCL = "Infinity";

	public static final List<String> constants = Arrays.asList(E, PI, C, G, H_REDUCED, IMAGINARY_NUMBER, NAN, INFINITY);

	/**
	 * Method determines mathematical entity type for text substring starting from ith index
	 *
	 * @param text analyzed text
	 * @param i	index which points to start of substring
	 * @return math entity type of substring starting from ith index of specified text
	 */
	@NotNull
	public static Result getType(@NotNull String text, int i) {
		if (i < 0) {
			throw new IllegalArgumentException("I must be more or equals to 0.");
		} else if (i >= text.length() && i != 0) {
			throw new IllegalArgumentException("I must be less than size of text.");
		} else if (i == 0 && text.length() == 0) {
			return new Result(MathType.text, text);
		}

		final StartsWithFinder startsWithFinder = new StartsWithFinder(text, i);

		for (MathType mathType : getMathTypesByPriority()) {
			final String s = CollectionsUtils.find(mathType.getTokens(), startsWithFinder);
			if (s != null) {
				return new Result(mathType, s);
			}
		}

		return new Result(MathType.text, text.substring(i));
	}


	private static List<MathType> mathTypesByPriority;

	@NotNull
	private static List<MathType> getMathTypesByPriority() {
		if (mathTypesByPriority == null) {
			final List<MathType> result = CollectionsUtils.asList(MathType.values());

			Collections.sort(result, new Comparator<MathType>() {
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

		@NotNull
		private final MathType mathType;

		@NotNull
		private final String match;

		public Result(@NotNull MathType mathType, @NotNull String match) {
			this.mathType = mathType;

			this.match = match;
		}

		public int processToJscl(@NotNull StringBuilder result, int i) throws ParseException {
			return mathType.processToJscl(result, i, match);
		}

		public int processFromJscl(@NotNull StringBuilder result, int i) {
			return mathType.processFromJscl(result, i, match);
		}

		@NotNull
		public String getMatch() {
			return match;
		}

		@NotNull
		public MathType getMathType() {
			return mathType;
		}
	}

	private static class EndsWithFinder implements Finder<String> {

		private int i;

		@NotNull
		private final CharSequence targetString;

		private EndsWithFinder(@NotNull CharSequence targetString) {
			this.targetString = targetString;
		}

		@Override
		public boolean isFound(@Nullable String s) {
			return targetString.subSequence(0, i).toString().endsWith(s);
		}

		public void setI(int i) {
			this.i = i;
		}
	}
}
