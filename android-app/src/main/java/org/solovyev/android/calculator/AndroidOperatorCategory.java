package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 10/7/12
 * Time: 7:41 PM
 */
public enum AndroidOperatorCategory {

	derivatives(R.string.derivatives),
	other(R.string.other),
	my(R.string.c_fun_category_my),
	common(R.string.c_fun_category_common);

	private final int captionId;

	AndroidOperatorCategory(int captionId) {
		this.captionId = captionId;
	}

	public int getCaptionId() {
		return captionId;
	}

	@Nullable
	public static AndroidOperatorCategory valueOf(@Nonnull OperatorCategory operatorCategory) {
		for (AndroidOperatorCategory androidOperatorCategory : values()) {
			if (androidOperatorCategory.name().equals(operatorCategory.name())) {
				return androidOperatorCategory;
			}
		}

		return null;
	}
}
