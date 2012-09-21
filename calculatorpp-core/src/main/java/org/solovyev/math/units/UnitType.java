package org.solovyev.math.units;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 4/21/12
 * Time: 7:55 PM
 */
public interface UnitType<V> {

    @NotNull
    Class<V> getUnitValueClass();

    boolean equals(@NotNull Object o);
}
