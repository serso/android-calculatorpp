/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.math;

import java.util.Comparator;

/**
 * User: serso
 * Date: 10/3/11
 * Time: 12:30 AM
 */
public class MathEntityComparator implements Comparator<String> {

	@Override
	public int compare(String s, String s1) {
		return s1.length() - s.length();
	}
}
