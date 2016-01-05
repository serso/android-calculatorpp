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

package org.solovyev.android.calculator.units;

import org.solovyev.common.units.Unit;
import org.solovyev.common.units.UnitConverter;
import org.solovyev.common.units.UnitImpl;
import org.solovyev.common.units.UnitType;

import java.math.BigInteger;

import javax.annotation.Nonnull;

import jscl.NumeralBase;

/**
 * User: Solovyev_S
 * Date: 24.09.12
 * Time: 16:05
 */
public enum CalculatorNumeralBase implements UnitType<String> {


    bin(NumeralBase.bin),

    oct(NumeralBase.oct),

    dec(NumeralBase.dec),

    hex(NumeralBase.hex);

    @Nonnull
    private static final CalculatorNumeralBase.Converter converter = new CalculatorNumeralBase.Converter();
    @Nonnull
    private final NumeralBase numeralBase;

    private CalculatorNumeralBase(@Nonnull NumeralBase numeralBase) {
        this.numeralBase = numeralBase;
    }

    @Nonnull
    public static CalculatorNumeralBase.Converter getConverter() {
        return converter;
    }

    @Nonnull
    public static CalculatorNumeralBase valueOf(@Nonnull NumeralBase nb) {
        for (CalculatorNumeralBase calculatorNumeralBase : values()) {
            if (calculatorNumeralBase.numeralBase == nb) {
                return calculatorNumeralBase;
            }
        }

        throw new IllegalArgumentException(nb + " is not supported numeral base!");
    }

    @Nonnull
    public NumeralBase getNumeralBase() {
        return numeralBase;
    }

    @Nonnull
    @Override
    public Class<String> getUnitValueClass() {
        return String.class;
    }

    @Nonnull
    public Unit<String> createUnit(@Nonnull String value) {
        return UnitImpl.newInstance(value, this);
    }

    public static class Converter implements UnitConverter<String> {

        private Converter() {
        }

        @Override
        public boolean isSupported(@Nonnull UnitType<?> from, @Nonnull UnitType<String> to) {
            return CalculatorNumeralBase.class.isAssignableFrom(from.getClass()) && CalculatorNumeralBase.class.isAssignableFrom(to.getClass());
        }

        @Nonnull
        @Override
        public Unit<String> convert(@Nonnull Unit<?> from, @Nonnull UnitType<String> toType) {
            if (!isSupported(from.getUnitType(), toType)) {
                throw new IllegalArgumentException("Types are not supported!");
            }

            final CalculatorNumeralBase fromTypeAndroid = (CalculatorNumeralBase) from.getUnitType();
            final NumeralBase fromNumeralBase = fromTypeAndroid.numeralBase;
            final NumeralBase toNumeralBase = ((CalculatorNumeralBase) toType).numeralBase;
            final String fromValue = (String) from.getValue();

            final BigInteger decBigInteger = fromNumeralBase.toBigInteger(fromValue);
            return UnitImpl.newInstance(toNumeralBase.toString(decBigInteger), toType);
        }
    }
}
