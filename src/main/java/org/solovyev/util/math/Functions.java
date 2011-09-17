/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.util.math;

import org.jetbrains.annotations.NonNls;

import java.util.Arrays;
import java.util.List;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 10:01 PM
 */
public interface Functions {

	String SIN = "sin";
	String SINH = "sinh";
	String ASIN = "asin";
	String ASINH = "asinh";
	String COS = "cos";
	String COSH = "cosh";
	String ACOS = "acos";
	String ACOSH = "acosh";
	String TAN = "tan";
	String TANH = "tanh";
	String ATAN = "atan";
	String ATANH = "atanh";
	String LOG = "log";
	String LN = "ln";
	String MOD = "mod";
	String EXP = "exp";
	String SQRT_SIGN = "√";
	String SQRT = "sqrt";

	public static final List<String> all = Arrays.asList(SIN, SINH, ASIN, ASINH, COS, COSH, ACOS, ACOSH, TAN, TANH, ATAN, ATANH, LOG, LN, MOD, SQRT, SQRT_SIGN, EXP);
}
