package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import org.solovyev.android.calculator.external.CalculatorExternalListenersContainer;
import org.solovyev.android.calculator.history.CalculatorHistory;
import org.solovyev.android.calculator.plot.CalculatorPlotter;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 12:45
 */
public interface CalculatorLocator {

	void init(@Nonnull Calculator calculator,
			  @Nonnull CalculatorEngine engine,
			  @Nonnull CalculatorClipboard clipboard,
			  @Nonnull CalculatorNotifier notifier,
			  @Nonnull CalculatorHistory history,
			  @Nonnull CalculatorLogger logger,
			  @Nonnull CalculatorPreferenceService preferenceService,
			  @Nonnull CalculatorKeyboard keyboard,
			  @Nonnull CalculatorExternalListenersContainer externalListenersContainer,
			  @Nonnull CalculatorPlotter plotter);

	@Nonnull
	Calculator getCalculator();

	@Nonnull
	CalculatorEngine getEngine();

	@Nonnull
	CalculatorDisplay getDisplay();

	@Nonnull
	CalculatorEditor getEditor();

	@Nonnull
	CalculatorKeyboard getKeyboard();

	@Nonnull
	CalculatorClipboard getClipboard();

	@Nonnull
	CalculatorNotifier getNotifier();

	@Nonnull
	CalculatorHistory getHistory();

	@Nonnull
	CalculatorLogger getLogger();

	@Nonnull
	CalculatorPlotter getPlotter();

	@Nonnull
	CalculatorPreferenceService getPreferenceService();

	@Nonnull
	CalculatorExternalListenersContainer getExternalListenersContainer();
}
