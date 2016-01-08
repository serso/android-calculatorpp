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

import android.content.Context;

import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.plot.CalculatorPlotter;
import org.solovyev.android.calculator.view.NumeralBaseConverterDialog;
import org.solovyev.android.menu.LabeledMenuItem;

import javax.annotation.Nonnull;

import jscl.math.Generic;

/**
 * User: Solovyev_S
 * Date: 21.09.12
 * Time: 10:55
 */
public enum CalculatorDisplayMenuItem implements LabeledMenuItem<DisplayState> {

    copy(R.string.c_copy) {
        @Override
        public void onClick(@Nonnull DisplayState data, @Nonnull Context context) {
            Locator.getInstance().getKeyboard().copyButtonPressed();
        }
    },

    convert_to_bin(R.string.convert_to_bin) {
        @Override
        public void onClick(@Nonnull DisplayState data, @Nonnull Context context) {
            ConversionMenuItem.convert_to_bin.onClick(data, context);
        }

        @Override
        protected boolean isItemVisibleFor(@Nonnull Generic generic, @Nonnull JsclOperation operation) {
            return ConversionMenuItem.convert_to_bin.isItemVisibleFor(generic, operation);
        }
    },

    convert_to_dec(R.string.convert_to_dec) {
        @Override
        public void onClick(@Nonnull DisplayState data, @Nonnull Context context) {
            ConversionMenuItem.convert_to_dec.onClick(data, context);
        }

        @Override
        protected boolean isItemVisibleFor(@Nonnull Generic generic, @Nonnull JsclOperation operation) {
            return ConversionMenuItem.convert_to_dec.isItemVisibleFor(generic, operation);
        }
    },

    convert_to_hex(R.string.convert_to_hex) {
        @Override
        public void onClick(@Nonnull DisplayState data, @Nonnull Context context) {
            ConversionMenuItem.convert_to_hex.onClick(data, context);
        }

        @Override
        protected boolean isItemVisibleFor(@Nonnull Generic generic, @Nonnull JsclOperation operation) {
            return ConversionMenuItem.convert_to_hex.isItemVisibleFor(generic, operation);
        }
    },

    convert(R.string.c_convert) {
        @Override
        public void onClick(@Nonnull DisplayState data, @Nonnull Context context) {
            final Generic result = data.getResult();
            if (result != null) {
                new NumeralBaseConverterDialog(result.toString()).show(context);
            }
        }

        @Override
        protected boolean isItemVisibleFor(@Nonnull Generic generic, @Nonnull JsclOperation operation) {
            return operation == JsclOperation.numeric && generic.getConstants().isEmpty();
        }
    },

    plot(R.string.c_plot) {
        @Override
        public void onClick(@Nonnull DisplayState data, @Nonnull Context context) {
            final Generic expression = data.getResult();
            if (expression == null) throw new AssertionError();

            final CalculatorPlotter plotter = Locator.getInstance().getPlotter();
            plotter.plot(expression);
        }

        @Override
        protected boolean isItemVisibleFor(@Nonnull Generic generic, @Nonnull JsclOperation operation) {
            return Locator.getInstance().getPlotter().isPlotPossibleFor(generic);
        }

    };

    private final int captionId;

    CalculatorDisplayMenuItem(int captionId) {
        this.captionId = captionId;
    }

    public final boolean isItemVisible(@Nonnull DisplayState displayViewState) {
        //noinspection ConstantConditions
        return displayViewState.isValid() && displayViewState.getResult() != null && isItemVisibleFor(displayViewState.getResult(), displayViewState.getOperation());
    }

    protected boolean isItemVisibleFor(@Nonnull Generic generic, @Nonnull JsclOperation operation) {
        return true;
    }

    @Nonnull
    @Override
    public String getCaption(@Nonnull Context context) {
        return context.getString(captionId);
    }
}
