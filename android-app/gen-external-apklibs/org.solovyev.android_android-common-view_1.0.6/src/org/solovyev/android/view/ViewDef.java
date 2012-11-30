package org.solovyev.android.view;

import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 11/4/12
 * Time: 11:59 PM
 */
public interface ViewDef {

    @Nullable
    Float getLayoutWeight();

    @Nullable
    Integer getLayoutMarginLeft();

    @Nullable
    Integer getLayoutMarginRight();

    @Nullable
    String getTag();

    @Nullable
    Integer getBackgroundResId();

}
