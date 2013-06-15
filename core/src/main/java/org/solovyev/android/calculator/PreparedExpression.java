/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import jscl.math.function.IConstant;
import javax.annotation.Nonnull;

import java.util.List;

/**
 * User: serso
 * Date: 10/18/11
 * Time: 10:07 PM
 */
public class PreparedExpression implements CharSequence {

	@Nonnull
	private String expression;

	@Nonnull
	private List<IConstant> undefinedVars;

	public PreparedExpression(@Nonnull String expression, @Nonnull List<IConstant> undefinedVars) {
		this.expression = expression;
		this.undefinedVars = undefinedVars;
	}

	@Nonnull
	public String getExpression() {
		return expression;
	}

	public boolean isExistsUndefinedVar() {
		return !this.undefinedVars.isEmpty();
	}

	@Nonnull
	public List<IConstant> getUndefinedVars() {
		return undefinedVars;
	}

	@Override
	public int length() {
		return expression.length();
	}

	@Override
	public char charAt(int i) {
		return expression.charAt(i);
	}

	@Override
	public CharSequence subSequence(int i, int i1) {
		return expression.subSequence(i, i1);
	}

	@Override
	public String toString() {
		return this.expression;
	}
}
