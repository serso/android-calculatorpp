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

import jscl.NumeralBase;
import jscl.math.Generic;

/**
 * User: Solovyev_S
 * Date: 24.09.12
 * Time: 16:48
 */
public class CalculatorConversionEventDataImpl implements CalculatorConversionEventData {

    @Nonnull
    private CalculatorEventData calculatorEventData;

    @Nonnull
    private NumeralBase fromNumeralBase;

    @Nonnull
    private NumeralBase toNumeralBase;

    @Nonnull
    private Generic value;

    @Nonnull
    private DisplayState displayState;

    private CalculatorConversionEventDataImpl() {
    }

    @Nonnull
    public static CalculatorConversionEventData newInstance(@Nonnull CalculatorEventData calculatorEventData,
                                                            @Nonnull Generic value,
                                                            @Nonnull NumeralBase from,
                                                            @Nonnull NumeralBase to,
                                                            @Nonnull DisplayState displayViewState) {
        final CalculatorConversionEventDataImpl result = new CalculatorConversionEventDataImpl();

        result.calculatorEventData = calculatorEventData;
        result.value = value;
        result.displayState = displayViewState;
        result.fromNumeralBase = from;
        result.toNumeralBase = to;

        return result;
    }

    @Override
    public long getEventId() {
        return calculatorEventData.getEventId();
    }

    @Override
    @Nonnull
    public Long getSequenceId() {
        return calculatorEventData.getSequenceId();
    }

    @Override
    public Object getSource() {
        return calculatorEventData.getSource();
    }

    @Override
    public boolean isAfter(@Nonnull CalculatorEventData that) {
        return calculatorEventData.isAfter(that);
    }

    @Override
    public boolean isSameSequence(@Nonnull CalculatorEventData that) {
        return calculatorEventData.isSameSequence(that);
    }

    @Override
    public boolean isAfterSequence(@Nonnull CalculatorEventData that) {
        return calculatorEventData.isAfterSequence(that);
    }

    @Nonnull
    @Override
    public DisplayState getDisplayState() {
        return this.displayState;
    }

    @Override
    @Nonnull
    public NumeralBase getFromNumeralBase() {
        return fromNumeralBase;
    }

    @Override
    @Nonnull
    public NumeralBase getToNumeralBase() {
        return toNumeralBase;
    }

    @Override
    @Nonnull
    public Generic getValue() {
        return value;
    }
}
