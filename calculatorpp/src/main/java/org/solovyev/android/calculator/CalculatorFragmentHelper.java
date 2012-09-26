package org.solovyev.android.calculator;

import android.support.v4.app.Fragment;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 9/26/12
 * Time: 10:14 PM
 */
public interface CalculatorFragmentHelper {

    boolean isPane(@NotNull Fragment fragment);

    void setPaneTitle(@NotNull Fragment fragment, int titleResId);
}
