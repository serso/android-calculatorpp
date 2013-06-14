package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 10/7/12
 * Time: 7:56 PM
 */
public enum AndroidVarCategory {

	system(R.string.c_var_system),
	my(R.string.c_var_my);

	private final int captionId;

	AndroidVarCategory(int captionId) {
		this.captionId = captionId;
	}

	public int getCaptionId() {
		return captionId;
	}

	@Nullable
	public static AndroidVarCategory valueOf(@NotNull VarCategory varCategory) {
		for (AndroidVarCategory androidVarCategory : values()) {
			if (androidVarCategory.name().equals(varCategory.name())) {
				return androidVarCategory;
			}
		}

		return null;
	}
}
