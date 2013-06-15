package org.solovyev.android.calculator.about;

import android.content.res.Resources;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 4/20/12
 * Time: 3:31 PM
 */
public class TextHelper {

	@Nonnull
	public String packageName;

	@Nonnull
	public Resources resources;

	public TextHelper(@Nonnull Resources resources, @Nonnull String packageName) {
		this.packageName = packageName;
		this.resources = resources;
	}

	@Nullable
	public String getText(@Nonnull String stringName) {
		final int stringId = this.resources.getIdentifier(stringName, "string", this.packageName);
		try {
			return resources.getString(stringId);
		} catch (Resources.NotFoundException e) {
			return null;
		}
	}

}
