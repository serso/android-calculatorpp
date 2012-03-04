package org.solovyev.android.calculator.model;

import jscl.math.function.IConstant;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.R;
import org.solovyev.common.utils.CollectionsUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
* User: serso
* Date: 12/22/11
* Time: 4:25 PM
*/
public enum VarCategory {

	system(R.string.c_var_system, 100){
		@Override
		boolean isInCategory(@NotNull IConstant var) {
			return var.isSystem();
		}
	},

	my(R.string.c_var_my, 0) {
		@Override
		boolean isInCategory(@NotNull IConstant var) {
			return !var.isSystem();
		}
	};

	private final int captionId;

	private final int tabOrder;

	VarCategory(int captionId, int tabOrder) {
		this.captionId = captionId;
		this.tabOrder = tabOrder;
	}

	public int getCaptionId() {
		return captionId;
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
