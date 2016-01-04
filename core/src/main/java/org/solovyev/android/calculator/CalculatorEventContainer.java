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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:39
 */
public interface CalculatorEventContainer {

    void addCalculatorEventListener(@Nonnull CalculatorEventListener calculatorEventListener);

    void removeCalculatorEventListener(@Nonnull CalculatorEventListener calculatorEventListener);

    void fireCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data);

    void fireCalculatorEvents(@Nonnull List<CalculatorEvent> calculatorEvents);

    public static class CalculatorEvent {

        @Nonnull
        private CalculatorEventData calculatorEventData;

        @Nonnull
        private CalculatorEventType calculatorEventType;

        @Nullable
        private Object data;

        public CalculatorEvent(@Nonnull CalculatorEventData calculatorEventData,
                               @Nonnull CalculatorEventType calculatorEventType,
                               @Nullable Object data) {
            this.calculatorEventData = calculatorEventData;
            this.calculatorEventType = calculatorEventType;
            this.data = data;
        }

        @Nonnull
        public CalculatorEventData getCalculatorEventData() {
            return calculatorEventData;
        }

        @Nonnull
        public CalculatorEventType getCalculatorEventType() {
            return calculatorEventType;
        }

        @Nullable
        public Object getData() {
            return data;
        }
    }
}
