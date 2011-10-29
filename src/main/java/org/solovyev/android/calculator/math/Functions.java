/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.math;

import java.util.*;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 10:01 PM
 */
public class Functions {

	// not intended for instantiation
	private Functions() {
		throw new AssertionError("Not allowed!");
	}

	public final static String DEGREE = "°";
	public final static String FACTORIAL = "!";

	public static final List<String> allPostfix = Arrays.asList(FACTORIAL, DEGREE);
}
