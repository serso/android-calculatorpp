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

package org.solovyev.android.calculator.view;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;

import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.ParseException;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.ToJsclTextProcessor;
import org.solovyev.android.calculator.units.CalculatorNumeralBase;
import org.solovyev.common.MutableObject;
import org.solovyev.common.text.Strings;
import org.solovyev.common.units.Unit;
import org.solovyev.common.units.UnitImpl;

import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NumeralBaseConverterDialog {

    @Nullable
    private String initialFromValue;

    public NumeralBaseConverterDialog(@Nullable String initialFromValue) {
        this.initialFromValue = initialFromValue;
    }

    public void show(@Nonnull Context context) {
        final UnitConverterViewBuilder b = new UnitConverterViewBuilder();
        b.setFromUnitTypes(Arrays.asList(CalculatorNumeralBase.values()));
        b.setToUnitTypes(Arrays.asList(CalculatorNumeralBase.values()));

        if (!Strings.isEmpty(initialFromValue)) {
            String value = initialFromValue;
            try {
                value = ToJsclTextProcessor.getInstance().process(value).getValue();
                b.setFromValue(UnitImpl.newInstance(value, CalculatorNumeralBase.valueOf(Locator.getInstance().getEngine().getMathEngine().getNumeralBase())));
            } catch (ParseException e) {
                b.setFromValue(UnitImpl.newInstance(value, CalculatorNumeralBase.valueOf(Locator.getInstance().getEngine().getMathEngine().getNumeralBase())));
            }
        } else {
            b.setFromValue(UnitImpl.newInstance("", CalculatorNumeralBase.valueOf(Locator.getInstance().getEngine().getMathEngine().getNumeralBase())));
        }

        b.setConverter(CalculatorNumeralBase.getConverter());

        final MutableObject<AlertDialog> alertDialogHolder = new MutableObject<AlertDialog>();
        b.setOkButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog = alertDialogHolder.getObject();
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
            }
        });

        b.setCustomButtonData(new UnitConverterViewBuilder.CustomButtonData(context.getString(R.string.c_use_short), new UnitConverterViewBuilder.CustomButtonOnClickListener() {
            @Override
            public void onClick(@Nonnull Unit<String> fromUnits, @Nonnull Unit<String> toUnits) {
                String toUnitsValue = toUnits.getValue();

                if (!toUnits.getUnitType().equals(CalculatorNumeralBase.valueOf(Locator.getInstance().getEngine().getMathEngine().getNumeralBase()))) {
                    toUnitsValue = ((CalculatorNumeralBase) toUnits.getUnitType()).getNumeralBase().getJsclPrefix() + toUnitsValue;
                }

                Locator.getInstance().getKeyboard().buttonPressed(toUnitsValue);
                final AlertDialog alertDialog = alertDialogHolder.getObject();
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
            }
        }));

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context, App.getTheme().alertDialogTheme);
        alertBuilder.setView(b.build(context));
        alertBuilder.setTitle(R.string.c_conversion_tool);

        final AlertDialog alertDialog = alertBuilder.create();

        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());

        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        alertDialogHolder.setObject(alertDialog);
        alertDialog.show();
        alertDialog.getWindow().setAttributes(lp);
    }
}
