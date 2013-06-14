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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

/**
 * User: serso
 * Date: 9/28/12
 * Time: 12:12 AM
 */
public abstract class AbstractCalculatorHelper implements SharedPreferences.OnSharedPreferenceChangeListener {

	@NotNull
	private CalculatorPreferences.Gui.Layout layout;

	@NotNull
	private CalculatorPreferences.Gui.Theme theme;

	@Nullable
	private Vibrator vibrator;

	@NotNull
	private final JListeners<DragPreferencesChangeListener> dpclRegister = Listeners.newHardRefListeners();

	@NotNull
	private String logTag = "CalculatorActivity";

	protected AbstractCalculatorHelper() {
	}

	protected AbstractCalculatorHelper(@NotNull String logTag) {
		this.logTag = logTag;
	}

	protected void onCreate(@NotNull Activity activity) {
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

	public void logDebug(@NotNull String message) {
		Log.d(logTag, message);
	}

	public void logError(@NotNull String message) {
		Log.e(logTag, message);
	}

	public void processButtons(@NotNull final Activity activity, @NotNull View root) {
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
				public boolean processDragEvent(@NotNull DragDirection dragDirection, @NotNull DragButton dragButton, @NotNull Point2d startPoint2d, @NotNull MotionEvent motionEvent) {
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

		if (layout == CalculatorPreferences.Gui.Layout.simple) {
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

	private void toggleButtonDirectionText(@NotNull View root, int id, boolean showDirectionText, @NotNull DragDirection... dragDirections) {
		final View v = getButton(root, id);
		if (v instanceof DirectionDragButton) {
			final DirectionDragButton button = (DirectionDragButton) v;
			for (DragDirection dragDirection : dragDirections) {
				button.showDirectionText(showDirectionText, dragDirection);
			}
		}
	}

	@NotNull
	private Calculator getCalculator() {
		return Locator.getInstance().getCalculator();
	}


	private void setOnDragListeners(@NotNull View root, @NotNull SimpleOnDragListener.Preferences dragPreferences, @NotNull SharedPreferences preferences) {
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

	@NotNull
	private CalculatorKeyboard getKeyboard() {
		return Locator.getInstance().getKeyboard();
	}

	@Nullable
	private <T extends DragButton> T getButton(@NotNull View root, int buttonId) {
		return (T) root.findViewById(buttonId);
	}

	@NotNull
	private SimpleOnDragListener newOnDragListener(@NotNull SimpleOnDragListener.DragProcessor dragProcessor,
												   @NotNull SimpleOnDragListener.Preferences dragPreferences) {
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

	public void onDestroy(@NotNull Activity activity) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

		preferences.unregisterOnSharedPreferenceChangeListener(this);
	}
}
