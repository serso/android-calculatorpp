package org.solovyev.android.calculator.external;

import javax.annotation.Nonnull;

public interface CalculatorExternalListenersContainer {

	void addExternalListener(@Nonnull Class<?> externalCalculatorClass);

	boolean removeExternalListener(@Nonnull Class<?> externalCalculatorClass);
}
