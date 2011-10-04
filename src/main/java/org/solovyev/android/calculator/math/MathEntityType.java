/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.math;

import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.CalculatorModel;
import org.solovyev.android.calculator.CharacterAtPositionFinder;
import org.solovyev.android.calculator.StartsWithFinder;
import org.solovyev.common.utils.Finder;

import java.util.Arrays;
import java.util.List;

import static org.solovyev.common.utils.CollectionsUtils.get;

public enum MathEntityType {

	digit,
	constant,
	dot,
	function,
	postfix_function,
	unary_operation,
	binary_operation,
	group_symbols,
	open_group_symbol,
	close_group_symbol,
	text;

	public static final List<String> constants = Arrays.asList("e", "π", "i");

	public static final List<String> digits = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");

	public static final List<Character> dots = Arrays.asList('.');

	public static final List<Character> unaryOperations = Arrays.asList('-', '=', '!');

	public static final List<Character> binaryOperations = Arrays.asList('-', '+', '*', '×', '∙', '/', '^');

	public static final List<String> prefixFunctions = Functions.allPrefix;

	public static final List<Character> postfixFunctions = Functions.allPostfix;

	public static final List<String> groupSymbols = Arrays.asList("[]", "()", "{}");

	public static final List<Character> openGroupSymbols = Arrays.asList('[', '(', '{');

	public static final List<Character> closeGroupSymbols = Arrays.asList(']', ')', '}');

	/**
	 * Method determines mathematical entity type for text substring starting from ith index
	 *
	 * @param text analyzed text
	 * @param i	index which points to start of substring
	 * @return math entity type of substring starting from ith index of specified text
	 */
	@NotNull
	public static MathEntityType getMathEntityType(@NotNull String text, int i) {
		return getType(text, i).getMathEntityType();
	}

	@NotNull
	public static Result getType(@NotNull String text, int i) {
		if (i < 0) {
			throw new IllegalArgumentException("I must be more or equals to 0.");
		} else if (i >= text.length() && i != 0) {
			throw new IllegalArgumentException("I must be less than size of text.");
		} else if (i == 0 && text.length() == 0) {
			return new Result(MathEntityType.text, text);
		}

		final StartsWithFinder stringStartWithFinder = new StartsWithFinder(text, i);
		final CharacterAtPositionFinder characterStartWithFinder = new CharacterAtPositionFinder(text, i);

		String foundString = get(digits, stringStartWithFinder);
		if (foundString != null) {
			return new Result(MathEntityType.digit, foundString);
		}

		Character foundCharacter = get(dots, characterStartWithFinder);
		if (foundCharacter != null) {
			return new Result(dot, String.valueOf(foundCharacter));
		}

		foundCharacter = get(postfixFunctions, characterStartWithFinder);
		if (foundCharacter != null) {
			return new Result(postfix_function, String.valueOf(foundCharacter));
		}

		foundCharacter = get(unaryOperations, characterStartWithFinder);
		if (foundCharacter != null) {
			return new Result(unary_operation, String.valueOf(foundCharacter));
		}

		foundCharacter = get(binaryOperations, characterStartWithFinder);
		if (foundCharacter != null) {
			return new Result(binary_operation, String.valueOf(foundCharacter));
		}

		foundString = get(groupSymbols, stringStartWithFinder);
		if (foundString != null) {
			return new Result(MathEntityType.group_symbols, foundString);
		}

		foundCharacter = get(openGroupSymbols, characterStartWithFinder);
		if (foundCharacter != null) {
			return new Result(open_group_symbol, String.valueOf(foundCharacter));
		}

		foundCharacter = get(closeGroupSymbols, characterStartWithFinder);
		if (foundCharacter != null) {
			return new Result(close_group_symbol, String.valueOf(foundCharacter));
		}

		foundString = get(prefixFunctions, stringStartWithFinder);
		if (foundString != null) {
			return new Result(MathEntityType.function, foundString);
		}

		foundString = get(CalculatorModel.getInstance().getVarsRegister().getVarNames(), stringStartWithFinder);
		if (foundString != null) {
			return new Result(MathEntityType.constant, foundString);
		}

		return new Result(MathEntityType.text, text.substring(i));
	}

	public static class Result {

		@NotNull
		private final MathEntityType mathEntityType;

		@NotNull
		private final String s;

		private Result(@NotNull MathEntityType mathEntityType, @NotNull String s){
			this.mathEntityType = mathEntityType;

			this.s = s;
		}

		@NotNull
		public String getS() {
			return s;
		}

		@NotNull
		public MathEntityType getMathEntityType() {
			return mathEntityType;
		}
	}

	private static boolean contains(@NotNull List<String> list, @NotNull final Finder<String> startsWithFinder) {
		return get(list, startsWithFinder) != null;
	}

	private static boolean contains(@NotNull List<Character> list, @NotNull final CharacterAtPositionFinder atPositionFinder) {
		return get(list, atPositionFinder) != null;
	}
}
