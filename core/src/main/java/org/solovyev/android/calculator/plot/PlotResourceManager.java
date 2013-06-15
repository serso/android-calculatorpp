package org.solovyev.android.calculator.plot;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 1/13/13
 * Time: 8:19 PM
 */
interface PlotResourceManager {

	@Nonnull
	PlotLineDef generateAndRegister();

	void register(@Nonnull PlotLineDef lineDef);

	void unregister(@Nonnull PlotLineDef lineDef);

	void unregisterAll();
}
