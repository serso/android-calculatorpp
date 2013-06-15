package org.solovyev.android.calculator;

import android.app.Activity;
import jscl.NumeralBase;
import javax.annotation.Nonnull;
import org.solovyev.android.calculator.units.CalculatorNumeralBase;
import org.solovyev.android.view.drag.DirectionDragButton;
import org.solovyev.android.view.drag.DragDirection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: serso
 * Date: 4/21/12
 * Time: 8:00 PM
 */
public enum AndroidNumeralBase {

	bin(CalculatorNumeralBase.bin) {
		@Nonnull
		@Override
		public List<Integer> getButtonIds() {
			return Arrays.asList(R.id.cpp_button_0, R.id.cpp_button_1);
		}
	},

	oct(CalculatorNumeralBase.oct) {
		@Nonnull
		@Override
		public List<Integer> getButtonIds() {
			final List<Integer> result = new ArrayList<Integer>(bin.getButtonIds());
			result.addAll(Arrays.asList(R.id.cpp_button_2, R.id.cpp_button_3, R.id.cpp_button_4, R.id.cpp_button_5, R.id.cpp_button_6, R.id.cpp_button_7));
			return result;
		}
	},

	dec(CalculatorNumeralBase.dec) {
		@Nonnull
		@Override
		public List<Integer> getButtonIds() {
			final List<Integer> result = new ArrayList<Integer>(oct.getButtonIds());
			result.addAll(Arrays.asList(R.id.cpp_button_8, R.id.cpp_button_9));
			return result;
		}
	},

	hex(CalculatorNumeralBase.hex) {

		@Nonnull
		private List<Integer> specialHexButtonIds = Arrays.asList(R.id.cpp_button_1, R.id.cpp_button_2, R.id.cpp_button_3, R.id.cpp_button_4, R.id.cpp_button_5, R.id.cpp_button_6);

		@Nonnull
		@Override
		public List<Integer> getButtonIds() {
			return dec.getButtonIds();
		}

		@Override
		protected void toggleButton(boolean show, @Nonnull DirectionDragButton button) {
			super.toggleButton(show, button);
			if (specialHexButtonIds.contains(button.getId())) {
				button.showDirectionText(show, DragDirection.left);
				button.invalidate();
			}
		}
	};

	@Nonnull
	private final CalculatorNumeralBase calculatorNumeralBase;

	private AndroidNumeralBase(@Nonnull CalculatorNumeralBase calculatorNumeralBase) {
		this.calculatorNumeralBase = calculatorNumeralBase;
	}

	@Nonnull
	public abstract List<Integer> getButtonIds();

	public void toggleButtons(boolean show, @Nonnull Activity activity) {
		for (Integer buttonId : getButtonIds()) {
			final DirectionDragButton button = (DirectionDragButton) activity.findViewById(buttonId);
			if (button != null) {
				toggleButton(show, button);
			}
		}
	}

	protected void toggleButton(boolean show, @Nonnull DirectionDragButton button) {
		button.setShowText(show);
		button.invalidate();
	}

	@Nonnull
	public NumeralBase getNumeralBase() {
		return calculatorNumeralBase.getNumeralBase();
	}

	@Nonnull
	public static AndroidNumeralBase valueOf(@Nonnull NumeralBase nb) {
		for (AndroidNumeralBase androidNumeralBase : values()) {
			if (androidNumeralBase.calculatorNumeralBase.getNumeralBase() == nb) {
				return androidNumeralBase;
			}
		}

		throw new IllegalArgumentException(nb + " is not supported numeral base!");
	}
}
