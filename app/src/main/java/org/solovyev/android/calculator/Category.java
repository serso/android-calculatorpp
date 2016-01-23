package org.solovyev.android.calculator;

import android.support.annotation.StringRes;

import javax.annotation.Nonnull;

public interface Category {

    int ordinal();

    @Nonnull
    String name();

    @StringRes
    int title();
}
