package org.solovyev.android.calculator;

import android.content.Context;
import jscl.NumeralBase;
import jscl.math.Generic;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.menu.AMenuItem;

/**
 * User: serso
 * Date: 9/21/12
 * Time: 12:11 AM
 */
enum ConversionMenuItem implements AMenuItem<CalculatorDisplayViewState> {

    convert_to_bin(NumeralBase.bin),
    convert_to_dec(NumeralBase.dec),
    convert_to_hex(NumeralBase.hex);

    @NotNull
    private final NumeralBase toNumeralBase;

    ConversionMenuItem(@NotNull NumeralBase toNumeralBase) {
        this.toNumeralBase = toNumeralBase;
    }

    protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
        boolean result = false;

        if (operation == JsclOperation.numeric) {
            if (generic.getConstants().isEmpty()) {
                // conversion possible => return true
                final NumeralBase fromNumeralBase = Locator.getInstance().getEngine().getNumeralBase();
                if (fromNumeralBase != toNumeralBase) {
                    result = Locator.getInstance().getCalculator().isConversionPossible(generic, fromNumeralBase, this.toNumeralBase);
                }
            }
        }

        return result;
    }

    @Override
    public void onClick(@NotNull CalculatorDisplayViewState data, @NotNull Context context) {
        final Generic result = data.getResult();

        if (result != null) {
            Locator.getInstance().getCalculator().convert(result, this.toNumeralBase);
        }
    }
}
