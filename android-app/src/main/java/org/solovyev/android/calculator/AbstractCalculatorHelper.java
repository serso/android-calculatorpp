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

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.solovyev.android.calculator.history.CalculatorHistoryState;
import org.solovyev.android.calculator.view.AngleUnitsButton;
import org.solovyev.android.calculator.view.NumeralBasesButton;
import org.solovyev.android.calculator.view.OnDragListenerVibrator;
import org.solovyev.android.history.HistoryDragProcessor;
import org.solovyev.android.view.drag.*;
import org.solovyev.common.listeners.JListeners;
import org.solovyev.common.listeners.Listeners;
import org.solovyev.common.math.Point2d;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.solovyev.android.calculator.CalculatorPreferences.Gui.Layout.simple;
import static org.solovyev.android.calculator.CalculatorPreferences.Gui.Layout.simple_mobile;

/**
 * User: serso
 * Date: 9/28/12
 * Time: 12:12 AM
 */
public abstract class AbstractCalculatorHelper implements SharedPreferences.OnSharedPreferenceChangeListener {

	@Nonnull
	private CalculatorPreferences.Gui.Layout layout;

	@Nonnull
	private CalculatorPreferences.Gui.Theme theme;

	@Nullable
	private Vibrator vibrator;

	@Nonnull
	private final JListeners<DragPreferencesChangeListener> dpclRegister = Listeners.newHardRefListeners();

	@Nonnull
	private String logTag = "CalculatorActivity";

	protected AbstractCalculatorHelper() {
	}

	protected AbstractCalculatorHelper(@Nonnull String logTag) {
		this.logTag = logTag;
	}

	protected void onCreate(@Nonnull Activity activity) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

		vibrator = (Vibrator) activity.getSystemService(Activity.VIBRATOR_SERVICE);
		layout = CalculatorPreferences.Gui.layout.getPreferenceNoError(preferences);
		theme = CalculatorPreferences.Gui.theme.getPreferenceNoError(preferences);

		preferences.registerOnSharedPreferenceChangeListener(this);

		// let's disable locking of screen for monkeyrunner
		if (CalculatorApplication.isMonkeyRunner(activity)) {
			final KeyguardManager km = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
			km.newKeyguardLock(activity.getClass().getName()).disableKeyguard();
		}
	}

	public void logDebug(@Nonnull String message) {
		Log.d(logTag, message);
	}

	public void logError(@Nonnull String message) {
		Log.e(logTag, message);
	}

	public void processButtons(@Nonnull final Activity activity, @Nonnull View root) {
		dpclRegister.removeListeners();

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		final SimpleOnDragListener.Preferences dragPreferences = SimpleOnDragListener.getPreferences(preferences, activity);

		setOnDragListeners(root, dragPreferences, preferences);

		final OnDragListener historyOnDragListener = new OnDragListenerVibrator(newOnDragListener(new HistoryDragProcessor<CalculatorHistoryState>(getCalculator()), dragPreferences), vibrator, preferences);
		final DragButton historyButton = getButton(root, R.id.cpp_button_history);
		if (historyButton != null) {
			historyButton.setOnDragListener(historyOnDragListener);
		}

		final DragButton subtractionButton = getButton(root, R.id.cpp_button_subtraction);
		if (subtractionButton != null) {
			subtractionButton.setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new SimpleOnDragListener.DragProcessor() {
				@Override
				public boolean processDragEvent(@Nonnull DragDirection dragDirection, @Nonnull DragButton dragButton, @Nonnull Point2d startPoint2d, @Nonnull MotionEvent motionEvent) {
					if (dragDirection == DragDirection.down) {
						Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_operators, null);
						return true;
					}
					return false;
				}
			}, dragPreferences), vibrator, preferences));
		}

		final OnDragListener toPositionOnDragListener = new OnDragListenerVibrator(new SimpleOnDragListener(new CursorDragProcessor(), dragPreferences), vibrator, preferences);

		final DragButton rightButton = getButton(root, R.id.cpp_button_right);
		if (rightButton != null) {
			rightButton.setOnDragListener(toPositionOnDragListener);
		}

		final DragButton leftButton = getButton(root, R.id.cpp_button_left);
		if (leftButton != null) {
			leftButton.setOnDragListener(toPositionOnDragListener);
		}

		final DragButton equalsButton = getButton(root, R.id.cpp_button_equals);
		if (equalsButton != null) {
			equalsButton.setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new EqualsDragProcessor(), dragPreferences), vibrator, preferences));
		}

		final AngleUnitsButton angleUnitsButton = (AngleUnitsButton) getButton(root, R.id.cpp_button_6);
		if (angleUnitsButton != null) {
			angleUnitsButton.setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new CalculatorButtons.AngleUnitsChanger(activity), dragPreferences), vibrator, preferences));
		}

		final NumeralBasesButton clearButton = (NumeralBasesButton) getButton(root, R.id.cpp_button_clear);
		if (clearButton != null) {
			clearButton.setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new CalculatorButtons.NumeralBasesChanger(activity), dragPreferences), vibrator, preferences));
		}

		final DragButton varsButton = getButton(root, R.id.cpp_button_vars);
		if (varsButton != null) {
			varsButton.setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new CalculatorButtons.VarsDragProcessor(activity), dragPreferences), vibrator, preferences));
		}

		final DragButton functionsButton = getButton(root, R.id.cpp_button_functions);
		if (functionsButton != null) {
			functionsButton.setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new CalculatorButtons.FunctionsDragProcessor(activity), dragPreferences), vibrator, preferences));
		}

		final DragButton roundBracketsButton = getButton(root, R.id.cpp_button_round_brackets);
		if (roundBracketsButton != null) {
			roundBracketsButton.setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new CalculatorButtons.RoundBracketsDragProcessor(), dragPreferences), vibrator, preferences));
		}

		if (layout == simple || layout == simple_mobile) {
			toggleButtonDirectionText(root, R.id.cpp_button_1, false, DragDirection.up, DragDirection.down);
			toggleButtonDirectionText(root, R.id.cpp_button_2, false, DragDirection.up, DragDirection.down);
			toggleButtonDirectionText(root, R.id.cpp_button_3, false, DragDirection.up, DragDirection.down);

			toggleButtonDirectionText(root, R.id.cpp_button_6, false, DragDirection.up, DragDirection.down);
			toggleButtonDirectionText(root, R.id.cpp_button_7, false, DragDirection.left, DragDirection.up, DragDirection.down);
			toggleButtonDirectionText(root, R.id.cpp_button_8, false, DragDirection.left, DragDirection.up, DragDirection.down);

			toggleButtonDirectionText(root, R.id.cpp_button_clear, false, DragDirection.left, DragDirection.up, DragDirection.down);

			toggleButtonDirectionText(root, R.id.cpp_button_4, false, DragDirection.down);
			toggleButtonDirectionText(root, R.id.cpp_button_5, false, DragDirection.down);

			toggleButtonDirectionText(root, R.id.cpp_button_9, false, DragDirection.left);

			toggleButtonDirectionText(root, R.id.cpp_button_multiplication, false, DragDirection.left);
			toggleButtonDirectionText(root, R.id.cpp_button_plus, false, DragDirection.down, DragDirection.up);
		}

		CalculatorButtons.processButtons(theme, layout, root);
		CalculatorButtons.toggleEqualsButton(preferences, activity);
		CalculatorButtons.initMultiplicationButton(root);
		NumeralBaseButtons.toggleNumericDigits(activity, preferences);
	}

	private void toggleButtonDirectionText(@Nonnull View root, int id, boolean showDirectionText, @Nonnull DragDirection... dragDirections) {
		final View v = getButton(root, id);
		if (v instanceof DirectionDragButton) {
			final DirectionDragButton button = (DirectionDragButton) v;
			for (DragDirection dragDirection : dragDirections) {
				button.showDirectionText(showDirectionText, dragDirection);
			}
		}
	}

	@Nonnull
	private Calculator getCalculator() {
		return Locator.getInstance().getCalculator();
	}


	private void setOnDragListeners(@Nonnull View root, @Nonnull SimpleOnDragListener.Preferences dragPreferences, @Nonnull SharedPreferences preferences) {
		final OnDragListener onDragListener = new OnDragListenerVibrator(newOnDragListener(new DigitButtonDragProcessor(getKeyboard()), dragPreferences), vibrator, preferences);

		final List<Integer> dragButtonIds = new ArrayList<Integer>();

		for (Field field : R.id.class.getDeclaredFields()) {
			int modifiers = field.getModifiers();
			if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
				try {
					int viewId = field.getInt(R.id.class);
					final View view = root.findViewById(viewId);
					if (view instanceof DragButton) {
						dragButtonIds.add(viewId);
					}
				} catch (IllegalAccessException e) {
					Log.e(R.id.class.getName(), e.getMessage());
				}
			}
		}

		for (Integer dragButtonId : dragButtonIds) {
			final DragButton button = getButton(root, dragButtonId);
			if (button != null) {
				button.setOnDragListener(onDragListener);
			}
		}
	}

	@Nonnull
	private CalculatorKeyboard getKeyboard() {
		return Locator.getInstance().getKeyboard();
	}

	@Nullable
	private <T extends DragButton> T getButton(@Nonnull View root, int buttonId) {
		return (T) root.findViewById(buttonId);
	}

	@Nonnull
	private SimpleOnDragListener newOnDragListener(@Nonnull SimpleOnDragListener.DragProcessor dragProcessor,
												   @Nonnull SimpleOnDragListener.Preferences dragPreferences) {
		final SimpleOnDragListener onDragListener = new SimpleOnDragListener(dragProcessor, dragPreferences);
		dpclRegister.addListener(onDragListener);
		return onDragListener;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
		if (key != null && key.startsWith("org.solovyev.android.calculator.DragButtonCalibrationActivity")) {
			final SimpleOnDragListener.Preferences dragPreferences = SimpleOnDragListener.getPreferences(preferences, CalculatorApplication.getInstance());
			for (DragPreferencesChangeListener dragPreferencesChangeListener : dpclRegister.getListeners()) {
				dragPreferencesChangeListener.onDragPreferencesChange(dragPreferences);
			}
		}
	}

	public void onDestroy(@Nonnull Activity activity) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

		preferences.unregisterOnSharedPreferenceChangeListener(this);
	}
}
