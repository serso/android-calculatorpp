package org.solovyev.math.units;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 4/21/12
 * Time: 7:54 PM
 */
public interface Unit<V> {

    @NotNull
    V getValue();

    @NotNull
    UnitType<V> getUnitType();
}
