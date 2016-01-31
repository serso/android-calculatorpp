/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import android.support.v4.app.Fragment;

import org.solovyev.android.calculator.about.CalculatorAboutFragment;
import org.solovyev.android.calculator.about.CalculatorReleaseNotesFragment;
import org.solovyev.android.calculator.history.RecentHistoryFragment;
import org.solovyev.android.calculator.history.SavedHistoryFragment;
import org.solovyev.android.calculator.functions.FunctionsFragment;
import org.solovyev.android.calculator.operators.OperatorsFragment;
import org.solovyev.android.calculator.variables.VariablesFragment;
import org.solovyev.android.calculator.matrix.CalculatorMatrixEditFragment;
import org.solovyev.android.calculator.plot.CalculatorPlotFragment;
import org.solovyev.android.calculator.plot.CalculatorPlotFunctionSettingsActivity;
import org.solovyev.android.calculator.plot.CalculatorPlotFunctionsActivity;
import org.solovyev.android.calculator.plot.CalculatorPlotRangeActivity;
import org.solovyev.android.calculator.preferences.PurchaseDialogActivity;

import javax.annotation.Nonnull;

/**
 * User: Solovyev_S
 * Date: 03.10.12
 * Time: 11:30
 */
public enum CalculatorFragmentType {

    editor(CalculatorEditorFragment.class, R.layout.cpp_app_editor, R.string.editor),
    //display(CalculatorHistoryFragment.class, "history", R.layout.fragment_history, R.string.c_history),
    //keyboard(CalculatorHistoryFragment.class, "history", R.layout.fragment_history, R.string.c_history),
    history(RecentHistoryFragment.class, R.layout.fragment_history, R.string.cpp_history_tab_recent),
    saved_history(SavedHistoryFragment.class, R.layout.fragment_history, R.string.cpp_history_tab_saved),
    variables(VariablesFragment.class, R.layout.fragment_entities, R.string.c_vars),
    functions(FunctionsFragment.class, R.layout.fragment_entities, R.string.c_functions),
    operators(OperatorsFragment.class, R.layout.fragment_entities, R.string.c_operators),
    plotter(CalculatorPlotFragment.class, R.layout.cpp_plotter_fragment, R.string.c_graph),
    plotter_functions(CalculatorPlotFunctionsActivity.CalculatorPlotFunctionsFragment.class, R.layout.cpp_plot_functions_fragment, R.string.cpp_plot_functions),
    plotter_function_settings(CalculatorPlotFunctionSettingsActivity.CalculatorPlotFunctionSettingsFragment.class, R.layout.cpp_plot_function_settings_fragment, R.string.cpp_plot_function_settings),
    plotter_range(CalculatorPlotRangeActivity.CalculatorPlotRangeFragment.class, R.layout.cpp_plot_range_fragment, R.string.cpp_plot_range),

    purchase_dialog(PurchaseDialogActivity.PurchaseDialogFragment.class, R.layout.cpp_purchase_dialog_fragment, R.string.cpp_purchase_title),

    about(CalculatorAboutFragment.class, R.layout.about_fragment, R.string.c_about),

    // todo serso: strings
    matrix_edit(CalculatorMatrixEditFragment.class, R.layout.matrix_edit_fragment, R.string.c_release_notes),
    release_notes(CalculatorReleaseNotesFragment.class, R.layout.release_notes_fragment, R.string.c_release_notes);

    private final int defaultLayoutId;
    @Nonnull
    private Class<? extends Fragment> fragmentClass;
    private int defaultTitleResId;

    CalculatorFragmentType(@Nonnull Class<? extends Fragment> fragmentClass,
                           int defaultLayoutId,
                           int defaultTitleResId) {
        this.fragmentClass = fragmentClass;
        this.defaultLayoutId = defaultLayoutId;
        this.defaultTitleResId = defaultTitleResId;
    }

    @Nonnull
    public String getFragmentTag() {
        return this.name();
    }

    public int getDefaultTitleResId() {
        return defaultTitleResId;
    }

    @Nonnull
    public Class<? extends Fragment> getFragmentClass() {
        return fragmentClass;
    }

    public int getDefaultLayoutId() {
        return defaultLayoutId;
    }

    @Nonnull
    public String createSubFragmentTag(@Nonnull String subFragmentTag) {
        return this.getFragmentTag() + "_" + subFragmentTag;
    }
}
