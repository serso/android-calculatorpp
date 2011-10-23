/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.Finder;

/**
* User: serso
* Date: 10/23/11
* Time: 2:57 PM
*/
public class NumberBuilder {

	@Nullable
	private StringBuilder numberBuilder = null;
	@Nullable
	private String number = null;

	public void process(@NotNull StringBuilder sb, @NotNull MathType.Result mathTypeResult) {
		number = null;
		if (mathTypeResult.getMathType() == MathType.digit || mathTypeResult.getMathType() == MathType.dot || mathTypeResult.getMathType() == MathType.power_10) {
			if (numberBuilder == null) {
				numberBuilder = new StringBuilder();
			}
			numberBuilder.append(mathTypeResult.getMatch());

			replaceSystemVars(sb, number);
		} else {
			process(sb);
		}
	}

	public void process(@NotNull StringBuilder sb) {
		if (numberBuilder != null) {
			try {
				number = numberBuilder.toString();
				Double.valueOf(number);
			} catch (NumberFormatException e) {
				number = null;
			}

			numberBuilder = null;
		} else {
			number = null;
		}

		replaceSystemVars(sb, number);
	}

	@Nullable
	private MathType replaceSystemVars(StringBuilder sb, String number) {
		MathType result = null;

		if (number != null) {
			final String finalNumber = number;
			final Var var = CollectionsUtils.get(CalculatorEngine.instance.getVarsRegister().getSystemVars(), new Finder<Var>() {
				@Override
				public boolean isFound(@Nullable Var var) {
					return var != null && finalNumber.equals(var.getValue());
				}
			});

			if (var != null) {
				sb.delete(sb.length() - number.length(), sb.length());
				sb.append(var.getName());
				result = MathType.constant;
			} else {
				sb.delete(sb.length() - number.length(), sb.length());
				sb.append(CalculatorEngine.instance.format(Double.valueOf(number)));
			}
		}

		return result;
	}
}
