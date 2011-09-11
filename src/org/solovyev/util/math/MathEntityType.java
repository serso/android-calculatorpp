package org.solovyev.util.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MathEntityType {

	digit,
	constant,
	dot,
	function, 
	unary_operation,
	binary_operation,
	group_symbols,
	group_symbol;
	
	public static final List<Character> constants = Arrays.asList('e', 'π');

	public static final List<Character> dots = Arrays.asList('.', ',');

	public static final List<Character> unaryOperations = Arrays.asList('-', '=', '!');

	public static final List<Character> binaryOperations = Arrays.asList('-', '+', '*', '×', '∙', '/', '^' );

	public static final List<String> functions = Arrays.asList("sin", "asin", "cos", "acos", "tg", "atg", "log", "ln", "mod", "√");
	
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
			char ch = s.charAt(0);
			
			if ( Character.isDigit(ch) ) {
				result = MathEntityType.digit;
			} else if ( unaryOperations.contains(ch) ) {
				result = MathEntityType.unary_operation; 
			} else if ( binaryOperations.contains(ch) ) {
				result = MathEntityType.binary_operation; 
			} else if ( singleGroupSymbols.contains(ch) ) {
				result = MathEntityType.group_symbol;
			} else if ( constants.contains(ch) ) {
				result = MathEntityType.constant;
			} else if ( dots.contains(ch) ) {
				result = MathEntityType.dot;
			}
		}
		
		if ( result == null ) {
			if ( functions.contains(s) ) {
				result = MathEntityType.function;
			} else if ( groupSymbols.contains(s) ) {
				result = MathEntityType.group_symbols;
			}
		}
		
		
		return result;
	}
}
