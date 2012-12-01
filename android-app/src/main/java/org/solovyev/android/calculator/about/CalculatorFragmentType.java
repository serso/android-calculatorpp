package org.solovyev.android.calculator.about;

import android.support.v4.app.Fragment;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.CalculatorEditorFragment;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.help.CalculatorHelpFaqFragment;
import org.solovyev.android.calculator.help.CalculatorHelpHintsFragment;
import org.solovyev.android.calculator.help.CalculatorHelpScreensFragment;
import org.solovyev.android.calculator.history.CalculatorHistoryFragment;
import org.solovyev.android.calculator.history.CalculatorSavedHistoryFragment;
import org.solovyev.android.calculator.math.edit.CalculatorFunctionsFragment;
import org.solovyev.android.calculator.math.edit.CalculatorOperatorsFragment;
import org.solovyev.android.calculator.math.edit.CalculatorVarsFragment;
import org.solovyev.android.calculator.matrix.CalculatorMatrixEditFragment;
import org.solovyev.android.calculator.plot.CalculatorPlotFragment;

/**
 * User: Solovyev_S
 * Date: 03.10.12
 * Time: 11:30
 */
public enum CalculatorFragmentType {

    editor(CalculatorEditorFragment.class, R.layout.cpp_app_editor, R.string.editor),
    //display(CalculatorHistoryFragment.class, "history", R.layout.history_fragment, R.string.c_history),
    //keyboard(CalculatorHistoryFragment.class, "history", R.layout.history_fragment, R.string.c_history),
    history(CalculatorHistoryFragment.class, R.layout.history_fragment, R.string.c_history),
    saved_history(CalculatorSavedHistoryFragment.class, R.layout.history_fragment, R.string.c_saved_history),
    variables(CalculatorVarsFragment.class, R.layout.vars_fragment, R.string.c_vars),
    functions(CalculatorFunctionsFragment.class, R.layout.math_entities_fragment, R.string.c_functions),
    operators(CalculatorOperatorsFragment.class, R.layout.math_entities_fragment, R.string.c_operators),
    plotter(CalculatorPlotFragment.class, R.layout.plot_fragment, R.string.c_graph),
    about(CalculatorAboutFragment.class, R.layout.about_fragment, R.string.c_about),
    faq(CalculatorHelpFaqFragment.class, R.layout.help_faq_fragment, R.string.c_faq),
    hints(CalculatorHelpHintsFragment.class, R.layout.help_hints_fragment, R.string.c_hints),
    screens(CalculatorHelpScreensFragment.class, R.layout.help_screens_fragment, R.string.c_screens),

    // todo serso: strings
    matrix_edit(CalculatorMatrixEditFragment.class, R.layout.matrix_edit_fragment, R.string.c_screens),
    release_notes(CalculatorReleaseNotesFragment.class, R.layout.release_notes_fragment, R.string.c_release_notes);

    @NotNull
    private Class<? extends Fragment> fragmentClass;

    private final int defaultLayoutId;

    private int defaultTitleResId;

    private CalculatorFragmentType(@NotNull Class<? extends Fragment> fragmentClass,
                                   int defaultLayoutId,
                                   int defaultTitleResId) {
        this.fragmentClass = fragmentClass;
        this.defaultLayoutId = defaultLayoutId;
        this.defaultTitleResId = defaultTitleResId;
    }

    @NotNull
    public String getFragmentTag() {
        return this.name();
    }

    public int getDefaultTitleResId() {
        return defaultTitleResId;
    }

    @NotNull
    public Class<? extends Fragment> getFragmentClass() {
        return fragmentClass;
    }

    public int getDefaultLayoutId() {
        return defaultLayoutId;
    }

    @NotNull
    public String createSubFragmentTag(@NotNull String subFragmentTag) {
        return this.getFragmentTag() + "_" + subFragmentTag;
    }
}
