package org.solovyev.android.calculator;

import jscl.math.operator.*;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.collections.Collections;

import java.util.Comparator;
import java.util.List;

/**
 * User: serso
 * Date: 10/7/12
 * Time: 7:40 PM
 */
public enum OperatorCategory {

	derivatives(100) {
		@Override
		public boolean isInCategory(@NotNull Operator operator) {
			return operator instanceof Derivative || operator instanceof Integral || operator instanceof IndefiniteIntegral;
		}
	},

	other(200) {
		@Override
		public boolean isInCategory(@NotNull Operator operator) {
			return operator instanceof Sum || operator instanceof Product;
		}
	},

	my(0) {
		@Override
		public boolean isInCategory(@NotNull Operator operator) {
			return !operator.isSystem();
		}
	},

	common(50) {
		@Override
		public boolean isInCategory(@NotNull Operator operator) {
			for (OperatorCategory category : values()) {
				if (category != this) {
					if (category.isInCategory(operator)) {
						return false;
					}
				}
			}

			return true;
		}
	};

	private final int tabOrder;

	OperatorCategory(int tabOrder) {
		this.tabOrder = tabOrder;
	}

	public abstract boolean isInCategory(@NotNull Operator operator);

	@NotNull
	public static List<OperatorCategory> getCategoriesByTabOrder() {
		final List<OperatorCategory> result = Collections.asList(OperatorCategory.values());

		java.util.Collections.sort(result, new Comparator<OperatorCategory>() {
			@Override
			public int compare(OperatorCategory category, OperatorCategory category1) {
				return category.tabOrder - category1.tabOrder;
			}
		});

		// todo serso: current solution (as creating operators is not implemented yet)
		result.remove(my);

		return result;
	}
}
