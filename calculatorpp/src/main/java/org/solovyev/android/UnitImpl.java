package org.solovyev.android;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 4/21/12
 * Time: 8:01 PM
 */
public class UnitImpl<V> implements Unit<V> {

    @NotNull
    private V value;

    @NotNull
    private UnitType<V> unitType;

    private UnitImpl() {
    }

    @NotNull
    public static <V> Unit<V> newInstance(@NotNull V value, @NotNull UnitType<V> unitType) {
        final UnitImpl<V> result = new UnitImpl<V>();

        result.value = value;
        result.unitType = unitType;

        return result;
    }

    @NotNull
    @Override
    public V getValue() {
        return this.value;
    }

    @NotNull
    @Override
    public UnitType<V> getUnitType() {
        return unitType;
    }
}
