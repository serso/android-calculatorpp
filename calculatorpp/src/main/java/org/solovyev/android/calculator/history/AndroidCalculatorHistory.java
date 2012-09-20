package org.solovyev.android.calculator.history;

import android.content.Context;
import android.content.SharedPreferences;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:07
 */
public interface AndroidCalculatorHistory extends CalculatorHistory {

	void load(@Nullable Context context, @Nullable SharedPreferences preferences);

	void save(@NotNull Context context);
}
