package org.solovyev.android.calculator;

import jscl.math.function.ArcTrigonometric;
import jscl.math.function.Comparison;
import jscl.math.function.Function;
import jscl.math.function.Trigonometric;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.collections.Collections;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * User: serso
 * Date: 10/7/12
 * Time: 7:15 PM
 */
public enum FunctionCategory {

	trigonometric(100) {
		@Override
		public boolean isInCategory(@NotNull Function function) {
			return (function instanceof Trigonometric || function instanceof ArcTrigonometric) && !hyperbolic_trigonometric.isInCategory(function);
		}
	},

	hyperbolic_trigonometric(300) {

		private final List<String> names = Arrays.asList("sinh", "cosh", "tanh", "coth", "asinh", "acosh", "atanh", "acoth");

		@Override
		public boolean isInCategory(@NotNull Function function) {
			return names.contains(function.getName());
		}
	},

	comparison(200) {
		@Override
		public boolean isInCategory(@NotNull Function function) {
			return function instanceof Comparison;
		}
	},

	my(0) {
		@Override
		public boolean isInCategory(@NotNull Function function) {
			return !function.isSystem();
		}
	},

	common(50) {
		@Override
		public boolean isInCategory(@NotNull Function function) {
			for (FunctionCategory category : values()) {
				if (category != this) {
					if (category.isInCategory(function)) {
						return false;
					}
				}
			}

			return true;
		}
	};

	private final int tabOrder;

	FunctionCategory(int tabOrder) {
		this.tabOrder = tabOrder;
	}

	public abstract boolean isInCategory(@NotNull Function function);

	@NotNull
	public static List<FunctionCategory> getCategoriesByTabOrder() {
		final List<FunctionCategory> result = Collections.asList(FunctionCategory.values());

		java.util.Collections.sort(result, new Comparator<FunctionCategory>() {
			@Override
			public int compare(FunctionCategory category, FunctionCategory category1) {
				return category.tabOrder - category1.tabOrder;
			}
		});

		return result;
	}
}
