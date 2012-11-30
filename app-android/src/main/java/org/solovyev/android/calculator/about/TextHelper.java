package org.solovyev.android.calculator.about;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 4/20/12
 * Time: 3:31 PM
 */
public class TextHelper {

    @NotNull
    public String packageName;

    @NotNull
    public Resources resources;

    public TextHelper(@NotNull Resources resources, @NotNull String packageName) {
        this.packageName = packageName;
        this.resources = resources;
    }

    @Nullable
    public String getText(@NotNull String stringName) {
        final int stringId = this.resources.getIdentifier(stringName, "string", this.packageName);
        try {
            return resources.getString(stringId);
        } catch (Resources.NotFoundException e) {
            return null;
        }
    }

}
