package org.solovyev.android.calculator.model;

import jscl.math.function.IConstant;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.collections.CollectionsUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
* User: serso
* Date: 12/22/11
* Time: 4:25 PM
*/
public enum VarCategory {

	system(100){
		@Override
		boolean isInCategory(@NotNull IConstant var) {
			return var.isSystem();
		}
	},

	my(0) {
		@Override
		boolean isInCategory(@NotNull IConstant var) {
			return !var.isSystem();
		}
	};

	private final int tabOrder;

	VarCategory(int tabOrder) {
		this.tabOrder = tabOrder;
	}

	abstract boolean isInCategory(@NotNull IConstant var);

	@NotNull
	public static List<VarCategory> getCategoriesByTabOrder() {
		final List<VarCategory> result = CollectionsUtils.asList(VarCategory.values());

		Collections.sort(result, new Comparator<VarCategory>() {
			@Override
			public int compare(VarCategory category, VarCategory category1) {
				return category.tabOrder - category1.tabOrder;
			}
		});

		return result;
	}
}
