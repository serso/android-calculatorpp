/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.math;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CharacterAtPositionFinder;
import org.solovyev.android.calculator.StartsWithFinder;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.Finder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.solovyev.common.utils.CollectionsUtils.get;

public enum MathType {

	digit(100, true, true, "0", "1", "2", "3", "4", "5", "6", "7", "8", "9") {
		@Override
		public boolean isNeedMultiplicationSignBefore(@NotNull MathType mathTypeBefore) {
			return super.isNeedMultiplicationSignBefore(mathTypeBefore) && mathTypeBefore != digit && mathTypeBefore != dot;
		}
	},

	dot(200, true, true, "."){
		@Override
		public boolean isNeedMultiplicationSignBefore(@NotNull MathType mathTypeBefore) {
			return super.isNeedMultiplicationSignBefore(mathTypeBefore) && mathTypeBefore != digit;
		}
	},

	power_10(300, true, false, "E") {
		@Override
		protected String getSubstitute(@NotNull String match) {
			return POWER_10_JSCL;
		}
	},

	postfix_function(400, true, false, Functions.allPostfix),
	unary_operation(500, false, false, "-", "=", "!"),
	binary_operation(600, false, false, "-", "+", "*", "×", "∙", "/", "^") {
		@Override
		protected String getSubstitute(@NotNull String match) {
			if ( match.equals("×") || match.equals("∙") ) {
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
		protected String getSubstitute(@NotNull String match) {
			return "(";
		}
	},

	close_group_symbol(900, false, true, "]", ")", "}") {
		@Override
		public boolean isNeedMultiplicationSignBefore(@NotNull MathType mathTypeBefore) {
			return false;
		}

		@Override
		protected String getSubstitute(@NotNull String match) {
			return ")";
		}
	},

	function(1000, true, true, Functions.allPrefix) {
		@Override
		protected String getSubstitute(@NotNull String match) {
			final String result;

			if (match.equals(Functions.LN)) {
				result = Functions.LN_JSCL;
			} else if (match.equals(Functions.SQRT)) {
				result = Functions.SQRT_JSCL;
			} else {
				result = match;
			}

			return result;
		}
	},

	constant(1100, true, true) {
		@NotNull
		@Override
		public List<String> getTokens() {
			return CalculatorEngine.instance.getVarsRegister().getVarNames();
		}
	},

	text(1200, false, false);

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

	public int process(@NotNull StringBuilder result, int i, @NotNull String match) {
		final String substitute = getSubstitute(match);
		result.append(substitute == null ? match : substitute);
		return i + match.length() - 1;
	}

	@Nullable
	protected String getSubstitute(@NotNull String match) {
		return null;
	}

	public static final List<String> openGroupSymbols = Arrays.asList("[]", "()", "{}");

	public final static Character POWER_10 = 'E';
	public final static String POWER_10_JSCL = "10^";

	public static final String IMAGINARY_NUMBER = "i";
	public static final String IMAGINARY_NUMBER_JSCL = "sqrt(-1)";

	public static final String PI = "π";
	public static final String E = "e";
	public final static String NAN = "NaN";

	public final static String INFINITY = "∞";
	public final static String INFINITY_JSCL = "Infinity";

	public static final List<String> constants = Arrays.asList(E, PI, IMAGINARY_NUMBER, NAN, INFINITY);

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
			final String s = get(mathType.getTokens(), startsWithFinder);
			if ( s != null ) {
				return new Result(mathType, s);
			}
		}

		return new Result(MathType.text, text.substring(i));
	}


	private static List<MathType> mathTypesByPriority;

	@NotNull
	private static List<MathType> getMathTypesByPriority() {
		if ( mathTypesByPriority == null ) {
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

		public int process(@NotNull StringBuilder result, int i) {
		 	return mathType.process(result, i, match);
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

	private static boolean contains(@NotNull List<String> list, @NotNull final Finder<String> startsWithFinder) {
		return get(list, startsWithFinder) != null;
	}

	private static boolean contains(@NotNull List<Character> list, @NotNull final CharacterAtPositionFinder atPositionFinder) {
		return get(list, atPositionFinder) != null;
	}
}
