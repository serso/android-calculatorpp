package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import jscl.AngleUnit;
import jscl.NumeralBase;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.android.Views;
import org.solovyev.android.calculator.core.R;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;
import org.solovyev.android.calculator.view.AngleUnitsButton;
import org.solovyev.android.calculator.view.NumeralBasesButton;
import org.solovyev.android.view.drag.DragButton;
import org.solovyev.android.view.drag.DragDirection;
import org.solovyev.android.view.drag.SimpleOnDragListener;
import org.solovyev.common.math.Point2d;

/**
 * User: serso
 * Date: 9/28/12
 * Time: 12:06 AM
 */
public final class CalculatorButtons {

	private CalculatorButtons() {
	}


	public static void processButtons(@Nonnull CalculatorPreferences.Gui.Theme theme,
									  @Nonnull CalculatorPreferences.Gui.Layout layout,
									  @Nonnull View root) {
		if (layout == CalculatorPreferences.Gui.Layout.main_calculator_mobile) {

			final float textSize = root.getContext().getResources().getDimension(R.dimen.cpp_keyboard_button_text_size_mobile);

			Views.processViewsOfType(root, DragButton.class, new Views.ViewProcessor<DragButton>() {
				@Override
				public void process(@Nonnull DragButton button) {
					button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
				}
			});
		}
	}

	static void initMultiplicationButton(@Nonnull View root) {
		final View multiplicationButton = root.findViewById(R.id.cpp_button_multiplication);
		if (multiplicationButton instanceof Button) {
			((Button) multiplicationButton).setText(Locator.getInstance().getEngine().getMultiplicationSign());
		}
	}

	public static void initMultiplicationButton(@Nonnull RemoteViews views) {
		views.setTextViewText(R.id.cpp_button_multiplication, Locator.getInstance().getEngine().getMultiplicationSign());
	}


	public static void toggleEqualsButton(@Nullable SharedPreferences preferences,
										  @Nonnull Activity activity) {
		preferences = preferences == null ? PreferenceManager.getDefaultSharedPreferences(activity) : preferences;

		final boolean large = Views.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_LARGE, activity.getResources().getConfiguration()) &&
				CalculatorPreferences.Gui.getLayout(preferences) != CalculatorPreferences.Gui.Layout.main_calculator_mobile;

		if (!large) {
			if (Views.getScreenOrientation(activity) == Configuration.ORIENTATION_PORTRAIT
					|| !CalculatorPreferences.Gui.autoOrientation.getPreference(preferences)) {

				final DragButton equalsButton = (DragButton) activity.findViewById(R.id.cpp_button_equals);
				if (equalsButton != null) {
					if (CalculatorPreferences.Gui.showEqualsButton.getPreference(preferences)) {
						equalsButton.setVisibility(View.VISIBLE);
					} else {
						equalsButton.setVisibility(View.GONE);
					}
				}
			}
		}
	}

	/*
	**********************************************************************
	*
	*                           STATIC CLASSES
	*
	**********************************************************************
	*/

	static class RoundBracketsDragProcessor implements SimpleOnDragListener.DragProcessor {
		@Override
		public boolean processDragEvent(@Nonnull DragDirection dragDirection, @Nonnull DragButton dragButton, @Nonnull Point2d startPoint2d, @Nonnull MotionEvent motionEvent) {
			final boolean result;

			if (dragDirection == DragDirection.left) {
				getKeyboard().roundBracketsButtonPressed();
				result = true;
			} else {
				result = new DigitButtonDragProcessor(getKeyboard()).processDragEvent(dragDirection, dragButton, startPoint2d, motionEvent);
			}

			return result;
		}
	}

	@Nonnull
	private static CalculatorKeyboard getKeyboard() {
		return Locator.getInstance().getKeyboard();
	}

	static class VarsDragProcessor implements SimpleOnDragListener.DragProcessor {

		@Nonnull
		private Context context;

		VarsDragProcessor(@Nonnull Context context) {
			this.context = context;
		}

		@Override
		public boolean processDragEvent(@Nonnull DragDirection dragDirection,
										@Nonnull DragButton dragButton,
										@Nonnull Point2d startPoint2d,
										@Nonnull MotionEvent motionEvent) {
			boolean result = false;

			if (dragDirection == DragDirection.up) {
				Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_create_var_dialog, null, context);
				result = true;
			} else if (dragDirection == DragDirection.down) {
				Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_create_matrix_dialog, null, context);
				result = true;
			}

			return result;
		}
	}

	static class AngleUnitsChanger implements SimpleOnDragListener.DragProcessor {

		@Nonnull
		private final DigitButtonDragProcessor processor;

		@Nonnull
		private final Context context;

		AngleUnitsChanger(@Nonnull Context context) {
			this.context = context;
			this.processor = new DigitButtonDragProcessor(Locator.getInstance().getKeyboard());
		}

		@Override
		public boolean processDragEvent(@Nonnull DragDirection dragDirection,
										@Nonnull DragButton dragButton,
										@Nonnull Point2d startPoint2d,
										@Nonnull MotionEvent motionEvent) {
			boolean result = false;

			if (dragButton instanceof AngleUnitsButton) {
				if (dragDirection != DragDirection.left) {
					final String directionText = ((AngleUnitsButton) dragButton).getText(dragDirection);
					if (directionText != null) {
						try {

							final AngleUnit angleUnits = AngleUnit.valueOf(directionText);

							final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

							final AngleUnit oldAngleUnits = AndroidCalculatorEngine.Preferences.angleUnit.getPreference(preferences);
							if (oldAngleUnits != angleUnits) {
								Locator.getInstance().getPreferenceService().setAngleUnits(angleUnits);
							}

							result = true;
						} catch (IllegalArgumentException e) {
							Log.d(this.getClass().getName(), "Unsupported angle units: " + directionText);
						}
					}
				} else if (dragDirection == DragDirection.left) {
					result = processor.processDragEvent(dragDirection, dragButton, startPoint2d, motionEvent);
				}
			}

			return result;
		}
	}

	static class NumeralBasesChanger implements SimpleOnDragListener.DragProcessor {

		@Nonnull
		private final Context context;

		NumeralBasesChanger(@Nonnull Context context) {
			this.context = context;
		}

		@Override
		public boolean processDragEvent(@Nonnull DragDirection dragDirection,
										@Nonnull DragButton dragButton,
										@Nonnull Point2d startPoint2d,
										@Nonnull MotionEvent motionEvent) {
			boolean result = false;

			if (dragButton instanceof NumeralBasesButton) {
				final String directionText = ((NumeralBasesButton) dragButton).getText(dragDirection);
				if (directionText != null) {
					try {

						final NumeralBase numeralBase = NumeralBase.valueOf(directionText);

						final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

						final NumeralBase oldNumeralBase = AndroidCalculatorEngine.Preferences.numeralBase.getPreference(preferences);
						if (oldNumeralBase != numeralBase) {
							Locator.getInstance().getPreferenceService().setNumeralBase(numeralBase);
						}

						result = true;
					} catch (IllegalArgumentException e) {
						Log.d(this.getClass().getName(), "Unsupported numeral base: " + directionText);
					}
				}
			}

			return result;
		}
	}

	static class FunctionsDragProcessor implements SimpleOnDragListener.DragProcessor {

		@Nonnull
		private Context context;

		FunctionsDragProcessor(@Nonnull Context context) {
			this.context = context;
		}

		@Override
		public boolean processDragEvent(@Nonnull DragDirection dragDirection,
										@Nonnull DragButton dragButton,
										@Nonnull Point2d startPoint2d,
										@Nonnull MotionEvent motionEvent) {
			boolean result = false;

			if (dragDirection == DragDirection.up) {
				Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_create_function_dialog, null, context);
				result = true;
			}

			return result;
		}
	}
}
