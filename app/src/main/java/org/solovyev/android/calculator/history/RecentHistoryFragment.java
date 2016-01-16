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

package org.solovyev.android.calculator.history;

import android.view.ContextMenu;
import android.view.MenuItem;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;
import java.util.List;

import static android.view.Menu.NONE;

public class RecentHistoryFragment extends BaseHistoryFragment {

    public RecentHistoryFragment() {
        super(CalculatorFragmentType.history);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.history_item;
    }

    @Nonnull
    @Override
    protected List<HistoryState> getHistoryItems() {
        return history.getRecent();
    }

    @Override
    protected void clearHistory() {
        history.clearRecent();
        getAdapter().clear();
    }

    @Override
    protected boolean isRecentHistory() {
        return true;
    }

    protected void onCreateContextMenu(@Nonnull ContextMenu menu, @Nonnull HistoryState state) {
        menu.add(NONE, R.string.c_use, NONE, R.string.c_use);
        menu.add(NONE, R.string.c_copy_expression, NONE, R.string.c_copy_expression);
        if (shouldHaveCopyResult(state)) {
            menu.add(NONE, R.string.c_copy_result, NONE, R.string.c_copy_result);
        }
        menu.add(NONE, R.string.c_save, NONE, R.string.c_save);
    }

    protected boolean onContextItemSelected(@Nonnull MenuItem item, @Nonnull HistoryState state) {
        switch (item.getItemId()) {
            case R.string.c_use:
                useState(state);
                return true;
            case R.string.c_copy_expression:
                copyExpression(state);
                return true;
            case R.string.c_copy_result:
                copyResult(state);
                return true;
            case R.string.c_save:
                EditHistoryFragment.show(state, true, getFragmentManager());
                return true;
        }
        return false;
    }
}
