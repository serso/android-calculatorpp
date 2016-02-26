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

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import org.solovyev.android.calculator.about.AboutFragment;
import org.solovyev.android.calculator.about.ReleaseNotesFragment;
import org.solovyev.android.calculator.functions.FunctionsFragment;
import org.solovyev.android.calculator.history.RecentHistoryFragment;
import org.solovyev.android.calculator.history.SavedHistoryFragment;
import org.solovyev.android.calculator.matrix.EditMatrixFragment;
import org.solovyev.android.calculator.operators.OperatorsFragment;
import org.solovyev.android.calculator.variables.VariablesFragment;

import javax.annotation.Nonnull;

public enum FragmentTab {

    history(RecentHistoryFragment.class, R.string.cpp_history_tab_recent),
    saved_history(SavedHistoryFragment.class, R.string.cpp_history_tab_saved),
    variables(VariablesFragment.class, R.string.c_vars),
    functions(FunctionsFragment.class, R.string.c_functions),
    operators(OperatorsFragment.class, R.string.c_operators),
    about(AboutFragment.class, R.string.cpp_about),

    // todo serso: strings
    matrix_edit(EditMatrixFragment.class, R.string.c_release_notes),
    release_notes(ReleaseNotesFragment.class, R.string.c_release_notes);

    @Nonnull
    public final Class<? extends Fragment> type;
    @StringRes
    public final int title;
    @Nonnull
    public final String tag;

    FragmentTab(@Nonnull Class<? extends Fragment> type, int title) {
        this.type = type;
        this.title = title;
        this.tag = name();
    }
}
