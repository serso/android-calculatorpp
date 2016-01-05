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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 10/9/12
 * Time: 9:59 PM
 */
public class CalculatorEventHolder {

    @Nonnull
    private volatile CalculatorEventData lastEventData;

    public CalculatorEventHolder(@Nonnull CalculatorEventData lastEventData) {
        this.lastEventData = lastEventData;
    }

    @Nonnull
    public synchronized CalculatorEventData getLastEventData() {
        return lastEventData;
    }

    @Nonnull
    public synchronized Result apply(@Nonnull CalculatorEventData newEventData) {
        final Result result = new Result(lastEventData, newEventData);

        if (result.isNewAfter()) {
            this.lastEventData = newEventData;
        }

        return result;
    }

    public static class Result {

        @Nonnull
        private final CalculatorEventData lastEventData;

        @Nonnull
        private final CalculatorEventData newEventData;

        @Nullable
        private Boolean after = null;

        @Nullable
        private Boolean sameSequence = null;

        public Result(@Nonnull CalculatorEventData lastEventData,
                      @Nonnull CalculatorEventData newEventData) {
            this.lastEventData = lastEventData;
            this.newEventData = newEventData;
        }

        public boolean isNewAfter() {
            if (after == null) {
                after = newEventData.isAfter(lastEventData);
            }
            return after;
        }

        public boolean isSameSequence() {
            if (sameSequence == null) {
                sameSequence = newEventData.isSameSequence(lastEventData);
            }
            return sameSequence;
        }

        public boolean isNewAfterSequence() {
            return newEventData.isAfterSequence(lastEventData);
        }

        public boolean isNewSameOrAfterSequence() {
            return isSameSequence() || isNewAfterSequence();
        }
    }
}
