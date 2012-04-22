package org.solovyev.android.calculator;

import android.app.Activity;
import jscl.NumeralBase;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.Unit;
import org.solovyev.android.UnitConverter;
import org.solovyev.android.UnitImpl;
import org.solovyev.android.UnitType;
import org.solovyev.android.view.drag.DirectionDragButton;
import org.solovyev.android.view.drag.DragDirection;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: serso
 * Date: 4/21/12
 * Time: 8:00 PM
 */
public enum AndroidNumeralBase implements UnitType<String> {

    bin(NumeralBase.bin) {
        @NotNull
        @Override
        public List<Integer> getButtonIds() {
            return Arrays.asList(R.id.zeroDigitButton, R.id.oneDigitButton);
        }
    },

    oct(NumeralBase.oct) {
        @NotNull
        @Override
        public List<Integer> getButtonIds() {
            final List<Integer> result = new ArrayList<Integer>(bin.getButtonIds());
            result.addAll(Arrays.asList(R.id.twoDigitButton, R.id.threeDigitButton, R.id.fourDigitButton, R.id.fiveDigitButton, R.id.sixDigitButton, R.id.sevenDigitButton));
            return result;
        }
    },

    dec(NumeralBase.dec) {
        @NotNull
        @Override
        public List<Integer> getButtonIds() {
            final List<Integer> result = new ArrayList<Integer>(oct.getButtonIds());
            result.addAll(Arrays.asList(R.id.eightDigitButton, R.id.nineDigitButton));
            return result;
        }
    },

    hex(NumeralBase.hex) {

        @NotNull
        private List<Integer> specialHexButtonIds = Arrays.asList(R.id.oneDigitButton, R.id.twoDigitButton, R.id.threeDigitButton, R.id.fourDigitButton, R.id.fiveDigitButton, R.id.sixDigitButton);

        @NotNull
        @Override
        public List<Integer> getButtonIds() {
            return dec.getButtonIds();
        }

        @Override
        protected void toggleButton(boolean show, @NotNull DirectionDragButton button) {
            super.toggleButton(show, button);
            if (specialHexButtonIds.contains(button.getId())) {
                button.showDirectionText(show, DragDirection.left);
                button.invalidate();
            }
        }
    };

    @NotNull
    private final NumeralBase numeralBase;

    private AndroidNumeralBase(@NotNull NumeralBase numeralBase) {
        this.numeralBase = numeralBase;
    }

    @NotNull
    public Unit<String> createUnit(@NotNull String value) {
        return UnitImpl.newInstance(value, this);
    }

    @NotNull
    public abstract List<Integer> getButtonIds();

    public void toggleButtons(boolean show, @NotNull Activity activity) {
        for (Integer buttonId : getButtonIds()) {
            final DirectionDragButton button = (DirectionDragButton) activity.findViewById(buttonId);
            if (button != null) {
                toggleButton(show, button);
            }
        }
    }

    protected void toggleButton(boolean show, @NotNull DirectionDragButton button) {
        button.setShowText(show);
    }

    @NotNull
    @Override
    public Class<String> getUnitValueClass() {
        return String.class;
    }

    @NotNull
    private static final Converter converter = new Converter();

    @NotNull
    public static Converter getConverter() {
        return converter;
    }

    public static class Converter implements UnitConverter<String> {

        private Converter() {
        }

        @Override
        public boolean isSupported(@NotNull UnitType<?> from, @NotNull UnitType<String> to) {
            return AndroidNumeralBase.class.isAssignableFrom(from.getClass()) && AndroidNumeralBase.class.isAssignableFrom(to.getClass());
        }

        @NotNull
        @Override
        public Unit<String> convert(@NotNull Unit<?> from, @NotNull UnitType<String> toType) {
            if (!isSupported(from.getUnitType(), toType)) {
                throw new IllegalArgumentException("Types are not supported!");
            }

            final AndroidNumeralBase fromTypeAndroid = (AndroidNumeralBase) from.getUnitType();
            final NumeralBase fromNumeralBase = fromTypeAndroid.numeralBase;
            final NumeralBase toNumeralBase = ((AndroidNumeralBase) toType).numeralBase;
            final String fromValue = (String) from.getValue();

            final BigInteger decBigInteger = fromNumeralBase.toBigInteger(fromValue);
            return UnitImpl.newInstance(toNumeralBase.toString(decBigInteger), (AndroidNumeralBase) toType);
        }
    }

    @NotNull
    public static AndroidNumeralBase valueOf(@NotNull NumeralBase nb) {
        for (AndroidNumeralBase androidNumeralBase : values()) {
            if (androidNumeralBase.numeralBase == nb) {
                return androidNumeralBase;
            }
        }

        throw new IllegalArgumentException(nb + " is not supported numeral base!");
    }
}
