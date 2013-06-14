package org.solovyev.android.calculator.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorParseException;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.ToJsclTextProcessor;
import org.solovyev.android.calculator.core.R;
import org.solovyev.android.calculator.units.CalculatorNumeralBase;
import org.solovyev.common.MutableObject;
import org.solovyev.common.text.Strings;
import org.solovyev.common.units.Unit;
import org.solovyev.common.units.UnitImpl;

import java.util.Arrays;

/**
 * User: serso
 * Date: 4/22/12
 * Time: 12:20 AM
 */
public class NumeralBaseConverterDialog {

	@Nullable
	private String initialFromValue;

	public NumeralBaseConverterDialog(@Nullable String initialFromValue) {
		this.initialFromValue = initialFromValue;
	}

	public void show(@NotNull Context context) {
		final UnitConverterViewBuilder b = new UnitConverterViewBuilder();
		b.setFromUnitTypes(Arrays.asList(CalculatorNumeralBase.values()));
		b.setToUnitTypes(Arrays.asList(CalculatorNumeralBase.values()));

		if (!Strings.isEmpty(initialFromValue)) {
			String value = initialFromValue;
			try {
				value = ToJsclTextProcessor.getInstance().process(value).getExpression();
				b.setFromValue(UnitImpl.newInstance(value, CalculatorNumeralBase.valueOf(Locator.getInstance().getEngine().getNumeralBase())));
			} catch (CalculatorParseException e) {
				b.setFromValue(UnitImpl.newInstance(value, CalculatorNumeralBase.valueOf(Locator.getInstance().getEngine().getNumeralBase())));
			}
		} else {
			b.setFromValue(UnitImpl.newInstance("", CalculatorNumeralBase.valueOf(Locator.getInstance().getEngine().getNumeralBase())));
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
			public void onClick(@NotNull Unit<String> fromUnits, @NotNull Unit<String> toUnits) {
				String toUnitsValue = toUnits.getValue();

				if (!toUnits.getUnitType().equals(CalculatorNumeralBase.valueOf(Locator.getInstance().getEngine().getNumeralBase()))) {
					toUnitsValue = ((CalculatorNumeralBase) toUnits.getUnitType()).getNumeralBase().getJsclPrefix() + toUnitsValue;
				}

				Locator.getInstance().getKeyboard().buttonPressed(toUnitsValue);
				final AlertDialog alertDialog = alertDialogHolder.getObject();
				if (alertDialog != null) {
					alertDialog.dismiss();
				}
			}
		}));

		final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
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
