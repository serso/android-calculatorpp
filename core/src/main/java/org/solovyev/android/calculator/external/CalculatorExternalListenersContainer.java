package org.solovyev.android.calculator.external;

import org.jetbrains.annotations.NotNull;

public interface CalculatorExternalListenersContainer {

	void addExternalListener(@NotNull Class<?> externalCalculatorClass);

	boolean removeExternalListener(@NotNull Class<?> externalCalculatorClass);
}
