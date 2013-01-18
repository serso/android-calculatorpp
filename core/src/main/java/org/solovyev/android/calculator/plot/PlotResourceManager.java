package org.solovyev.android.calculator.plot;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 1/13/13
 * Time: 8:19 PM
 */
interface PlotResourceManager {

    @NotNull
    PlotLineDef generateAndRegister();

    void register(@NotNull PlotLineDef lineDef);

    void unregister(@NotNull PlotLineDef lineDef);

    void unregisterAll();
}
