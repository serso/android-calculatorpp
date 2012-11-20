package org.solovyev.android.calculator.external;

import android.content.Context;
import android.content.Intent;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 11/20/12
 * Time: 10:33 PM
 */
public interface ExternalCalculatorIntentHandler {

    void onIntent(@NotNull Context context, @NotNull Intent intent);
}
