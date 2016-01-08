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

import org.solovyev.android.calculator.CalculatorEventListener;
import org.solovyev.common.history.HistoryHelper;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:11
 */
public interface CalculatorHistory extends HistoryHelper<HistoryState>, CalculatorEventListener {

    void load();

    void save();

    void fromXml(@Nonnull String xml);

    String toXml();

    void clearSavedHistory();

    void removeSavedHistory(@Nonnull HistoryState historyState);

    @Nonnull
    List<HistoryState> getSavedHistory();

    @Nonnull
    HistoryState addSavedState(@Nonnull HistoryState historyState);

    @Nonnull
    List<HistoryState> getStates();

    @Nonnull
    List<HistoryState> getStates(boolean includeIntermediateStates);

}
