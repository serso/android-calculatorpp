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

import android.widget.ArrayAdapter;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 12/18/11
 * Time: 3:10 PM
 */
public class HistoryItemMenuData {

    @Nonnull
    private final ArrayAdapter<OldHistoryState> adapter;

    @Nonnull
    private final OldHistoryState historyState;

    public HistoryItemMenuData(@Nonnull OldHistoryState historyState, ArrayAdapter<OldHistoryState> adapter) {
        this.historyState = historyState;
        this.adapter = adapter;
    }

    @Nonnull
    public OldHistoryState getHistoryState() {
        return historyState;
    }

    @Nonnull
    public ArrayAdapter<OldHistoryState> getAdapter() {
        return adapter;
    }
}
