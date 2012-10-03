package org.solovyev.android.calculator.about;

import android.support.v4.app.Fragment;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.history.CalculatorHistoryFragment;
import org.solovyev.android.calculator.history.CalculatorSavedHistoryFragment;
import org.solovyev.android.calculator.math.edit.CalculatorFunctionsFragment;
import org.solovyev.android.calculator.math.edit.CalculatorOperatorsFragment;
import org.solovyev.android.calculator.math.edit.CalculatorVarsFragment;
import org.solovyev.android.calculator.plot.CalculatorPlotFragment;

/**
 * User: Solovyev_S
 * Date: 03.10.12
 * Time: 11:30
 */
public enum CalculatorFragmentType {

    history(CalculatorHistoryFragment.class, "history", R.string.c_history),
    saved_history(CalculatorSavedHistoryFragment.class, "saved_history", R.string.c_saved_history),
    variables(CalculatorVarsFragment.class, "vars", R.string.c_vars),
    functions(CalculatorFunctionsFragment.class, "functions", R.string.c_functions),
    operators(CalculatorOperatorsFragment.class, "operators", R.string.c_operators),
    plotter(CalculatorPlotFragment.class, "plotter", R.string.c_plot);

    @NotNull
    private Class<? extends Fragment> fragmentClass;

    @NotNull
    private final String fragmentTag;

    private int defaultTitleResId;

    private CalculatorFragmentType(@NotNull Class<? extends Fragment> fragmentClass, @NotNull String fragmentTag, int defaultTitleResId) {
        this.fragmentClass = fragmentClass;
        this.fragmentTag = fragmentTag;
        this.defaultTitleResId = defaultTitleResId;
    }

    @NotNull
    public String getFragmentTag() {
        return fragmentTag;
    }

    public int getDefaultTitleResId() {
        return defaultTitleResId;
    }

    @NotNull
    public Class<? extends Fragment> getFragmentClass() {
        return fragmentClass;
    }

    @NotNull
    public String createSubFragmentTag(@NotNull String subFragmentTag) {
        return this.fragmentTag + "_" + subFragmentTag;
    }
}
