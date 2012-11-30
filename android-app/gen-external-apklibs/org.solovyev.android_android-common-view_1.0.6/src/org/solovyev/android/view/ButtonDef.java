package org.solovyev.android.view;

import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 11/4/12
 * Time: 11:52 PM
 */
public interface ButtonDef extends ViewDef {

    @Nullable
    Integer getDrawableResId();

    @Nullable
    CharSequence getText();
}
