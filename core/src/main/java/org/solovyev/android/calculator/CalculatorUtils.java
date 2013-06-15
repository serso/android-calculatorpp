package org.solovyev.android.calculator;

import jscl.math.Generic;
import jscl.math.function.Constant;
import jscl.math.function.IConstant;
import javax.annotation.Nonnull;

import java.util.HashSet;
import java.util.Set;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 7:13 PM
 */
public final class CalculatorUtils {

	static final long FIRST_ID = 0;

	private CalculatorUtils() {
		throw new AssertionError();
	}

	@Nonnull
	public static CalculatorEventData createFirstEventDataId() {
		return CalculatorEventDataImpl.newInstance(FIRST_ID, FIRST_ID);
	}

	@Nonnull
	public static Set<Constant> getNotSystemConstants(@Nonnull Generic expression) {
		final Set<Constant> notSystemConstants = new HashSet<Constant>();

		for (Constant constant : expression.getConstants()) {
			IConstant var = Locator.getInstance().getEngine().getVarsRegistry().get(constant.getName());
			if (var != null && !var.isSystem() && !var.isDefined()) {
				notSystemConstants.add(constant);
			}
		}

		return notSystemConstants;
	}

}
