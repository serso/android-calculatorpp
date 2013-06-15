package org.solovyev.android.calculator;

import jscl.math.function.IConstant;
import javax.annotation.Nonnull;
import org.solovyev.common.collections.Collections;

import java.util.Comparator;
import java.util.List;

/**
 * User: serso
 * Date: 12/22/11
 * Time: 4:25 PM
 */
public enum VarCategory {

	system(100) {
		@Override
		public boolean isInCategory(@Nonnull IConstant var) {
			return var.isSystem();
		}
	},

	my(0) {
		@Override
		public boolean isInCategory(@Nonnull IConstant var) {
			return !var.isSystem();
		}
	};

	private final int tabOrder;

	VarCategory(int tabOrder) {
		this.tabOrder = tabOrder;
	}

	public abstract boolean isInCategory(@Nonnull IConstant var);

	@Nonnull
	public static List<VarCategory> getCategoriesByTabOrder() {
		final List<VarCategory> result = Collections.asList(VarCategory.values());

		java.util.Collections.sort(result, new Comparator<VarCategory>() {
			@Override
			public int compare(VarCategory category, VarCategory category1) {
				return category.tabOrder - category1.tabOrder;
			}
		});

		return result;
	}
}
