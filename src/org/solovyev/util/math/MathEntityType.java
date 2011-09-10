package org.solovyev.util.math;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MathEntityType {

	digit,
	function, 
	unary_operation,
	binary_operation,
	group_symbols,
	group_symbol;
	
	private static final List<Character> unaryOperations = Arrays.asList('-', '=', '!');
	
	private static final List<Character> binaryOperations = Arrays.asList('-', '+', '*', '/', '^' );

	private static final List<String> functions = Arrays.asList("sin", "asin", "cos", "acos", "tg", "atg", "exp", "log", "ln", "mod", "√");
	
	private static final List<String> groupSymbols = Arrays.asList("[]", "()", "{}");

	private static final List<Character> singleGroupSymbols = Arrays.asList('[', ']', '(', ')', '{', '}');
	
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
