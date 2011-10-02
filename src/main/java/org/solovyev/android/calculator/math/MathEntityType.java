/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.math;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorModel;
import org.solovyev.android.calculator.Var;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.Finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum MathEntityType {

	digit,
	constant,
	dot,
	function, 
	postfix_function,
	unary_operation,
	binary_operation,
	group_symbols,
	group_symbol;
	
	public static final List<String> constants = Arrays.asList("e", "π", "i");

	public static final List<Character> dots = Arrays.asList('.');

	public static final List<Character> unaryOperations = Arrays.asList('-', '=', '!');

	public static final List<Character> binaryOperations = Arrays.asList('-', '+', '*', '×', '∙', '/', '^' );

	public static final List<String> prefixFunctions = Functions.allPrefix;

	public static final List<Character> postfixFunctions = Functions.allPostfix;

	public static final List<String> groupSymbols = Arrays.asList("[]", "()", "{}");

	public static final List<Character> openGroupSymbols = Arrays.asList('[', '(', '{');

	public static final List<Character> closeGroupSymbols = Arrays.asList(']', ')', '}');

	public static final List<Character> singleGroupSymbols;
	static {
		final List<Character> list = new ArrayList<Character>();
		list.addAll(openGroupSymbols);
		list.addAll(closeGroupSymbols);
		singleGroupSymbols = Collections.unmodifiableList(list);
	}

	@Nullable
	public static MathEntityType getType( @NotNull String s ) {
		MathEntityType result = null;
		
		if ( s.length() == 1 ) {
			result = getType(s.charAt(0));
		}
		
		if ( result == null ) {
			if ( isConstant(s) ) {
				result = MathEntityType.constant;
			} else if ( prefixFunctions.contains(s) ) {
				result = MathEntityType.function;
			} else if ( groupSymbols.contains(s) ) {
				result = MathEntityType.group_symbols;
			}
		}
		
		
		return result;
	}

	@Nullable
	public static MathEntityType getType(final char ch) {
		MathEntityType result = null;

		if ( Character.isDigit(ch) ) {
			result = MathEntityType.digit;
		} else if ( postfixFunctions.contains(ch) ) {
			result = MathEntityType.postfix_function;
		} else if ( unaryOperations.contains(ch) ) {
			result = MathEntityType.unary_operation;
		} else if ( binaryOperations.contains(ch) ) {
			result = MathEntityType.binary_operation;
		} else if ( singleGroupSymbols.contains(ch) ) {
			result = MathEntityType.group_symbol;
		} else if (isConstant(ch)) {
			result = MathEntityType.constant;
		} else if ( dots.contains(ch) ) {
			result = MathEntityType.dot;
		}
		return result;
	}

	private static boolean isConstant(final char ch) {
		final String name = String.valueOf(ch);

		return isConstant(name);
	}

	private static boolean isConstant(final String name) {
		return CollectionsUtils.get(CalculatorModel.getInstance().getVarsRegister().getVars(), new Finder<Var>() {
			@Override
			public boolean isFound(@Nullable Var var) {
				return var != null && var.getName().equals(name);
			}
		}) != null;
	}
}
