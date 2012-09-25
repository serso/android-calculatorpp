package org.solovyev.android.calculator;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 9/25/12
 * Time: 10:31 PM
 */
public interface CalculatorActivityHelper {

    void onCreate(@NotNull SherlockFragmentActivity activity, @Nullable Bundle savedInstanceState);

    void onSaveInstanceState(@NotNull SherlockFragmentActivity activity, @NotNull Bundle outState);

}
