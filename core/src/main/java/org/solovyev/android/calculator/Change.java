package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 10/1/12
 * Time: 11:16 PM
 */
public interface Change<T> {

    @NotNull
    T getOldValue();

    @NotNull
    T getNewValue();

}
