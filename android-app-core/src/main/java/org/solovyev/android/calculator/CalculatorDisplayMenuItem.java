package org.solovyev.android.calculator;

import android.content.Context;
import jscl.math.Generic;
import jscl.math.function.Constant;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.core.R;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.plot.PlotInput;
import org.solovyev.android.calculator.view.NumeralBaseConverterDialog;
import org.solovyev.android.menu.LabeledMenuItem;

import java.util.ArrayList;
import java.util.List;

/**
* User: Solovyev_S
* Date: 21.09.12
* Time: 10:55
*/
public enum CalculatorDisplayMenuItem implements LabeledMenuItem<CalculatorDisplayViewState> {

    copy(R.string.c_copy) {
        @Override
        public void onClick(@NotNull CalculatorDisplayViewState data, @NotNull Context context) {
            Locator.getInstance().getKeyboard().copyButtonPressed();
        }
    },

    convert_to_bin(R.string.convert_to_bin) {
        @Override
        public void onClick(@NotNull CalculatorDisplayViewState data, @NotNull Context context) {
            ConversionMenuItem.convert_to_bin.onClick(data, context);
        }

        @Override
        protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
            return ConversionMenuItem.convert_to_bin.isItemVisibleFor(generic, operation);
        }
    },

    convert_to_dec(R.string.convert_to_dec) {
        @Override
        public void onClick(@NotNull CalculatorDisplayViewState data, @NotNull Context context) {
            ConversionMenuItem.convert_to_dec.onClick(data, context);
        }

        @Override
        protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
            return ConversionMenuItem.convert_to_dec.isItemVisibleFor(generic, operation);
        }
    },

    convert_to_hex(R.string.convert_to_hex) {
        @Override
        public void onClick(@NotNull CalculatorDisplayViewState data, @NotNull Context context) {
            ConversionMenuItem.convert_to_hex.onClick(data, context);
        }

        @Override
        protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
            return ConversionMenuItem.convert_to_hex.isItemVisibleFor(generic, operation);
        }
    },

    convert(R.string.c_convert) {
        @Override
        public void onClick(@NotNull CalculatorDisplayViewState data, @NotNull Context context) {
            final Generic result = data.getResult();
            if (result != null) {
                new NumeralBaseConverterDialog(result.toString()).show(context);
            }
        }

        @Override
        protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
            return operation == JsclOperation.numeric && generic.getConstants().isEmpty();
        }
    },

    plot(R.string.c_plot) {
        @Override
        public void onClick(@NotNull CalculatorDisplayViewState data, @NotNull Context context) {
            final Generic generic = data.getResult();
            assert generic != null;

            final List<Constant> variables = new ArrayList<Constant>(CalculatorUtils.getNotSystemConstants(generic));

            final Constant xVariable;
            if ( variables.size() > 0 ) {
                xVariable = variables.get(0);
            } else {
                xVariable = null;
            }

            final Constant yVariable;
            if ( variables.size() > 1 ) {
                yVariable = variables.get(1);
            } else {
                yVariable = null;
            }

            Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.plot_graph, PlotInput.newInstance(generic, xVariable, yVariable), context);
        }

        @Override
        protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
            return CalculatorUtils.isPlotPossible(generic, operation);
        }

    };

    private final int captionId;

    CalculatorDisplayMenuItem(int captionId) {
        this.captionId = captionId;
    }

    public final boolean isItemVisible(@NotNull CalculatorDisplayViewState displayViewState) {
        //noinspection ConstantConditions
        return displayViewState.isValid() && displayViewState.getResult() != null && isItemVisibleFor(displayViewState.getResult(), displayViewState.getOperation());
    }

    protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
        return true;
    }

    @NotNull
    @Override
    public String getCaption(@NotNull Context context) {
        return context.getString(captionId);
    }
}
