package org.solovyev.android.calculator;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import jscl.NumeralBase;
import jscl.math.Generic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.history.CalculatorHistoryState;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.history.HistoryAction;
import org.solovyev.common.msg.Message;

import java.util.List;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 5:42 PM
 */
public class AndroidCalculator implements Calculator, CalculatorEventListener, SharedPreferences.OnSharedPreferenceChangeListener {

	@NotNull
	private final CalculatorImpl calculator = new CalculatorImpl();

	@NotNull
	private final Application context;

	public AndroidCalculator(@NotNull Application application) {
		this.context = application;
		this.calculator.addCalculatorEventListener(this);

		PreferenceManager.getDefaultSharedPreferences(application).registerOnSharedPreferenceChangeListener(this);
	}

	public void init(@NotNull final Activity activity) {
		setEditor(activity);
		setDisplay(activity);
	}

	public void setDisplay(@NotNull Activity activity) {
		final AndroidCalculatorDisplayView displayView = (AndroidCalculatorDisplayView) activity.findViewById(R.id.calculator_display);
		setDisplay(activity, displayView);
	}

	public void setDisplay(@NotNull Context context, @NotNull AndroidCalculatorDisplayView displayView) {
		displayView.init(context);
		Locator.getInstance().getDisplay().setView(displayView);
	}

	public void setEditor(@NotNull Activity activity) {
		final AndroidCalculatorEditorView editorView = (AndroidCalculatorEditorView) activity.findViewById(R.id.calculator_editor);
		setEditor(activity, editorView);
	}

	public void setEditor(@NotNull Context context, @NotNull AndroidCalculatorEditorView editorView) {
		editorView.init(context);
		Locator.getInstance().getEditor().setView(editorView);
	}


	/*
	**********************************************************************
	*
	*                           DELEGATED TO CALCULATOR
	*
	**********************************************************************
	*/

	@Override
	@NotNull
	public CalculatorEventData evaluate(@NotNull JsclOperation operation, @NotNull String expression) {
		return calculator.evaluate(operation, expression);
	}

	@Override
	@NotNull
	public CalculatorEventData evaluate(@NotNull JsclOperation operation, @NotNull String expression, @NotNull Long sequenceId) {
		return calculator.evaluate(operation, expression, sequenceId);
	}

	@Override
	public boolean isConversionPossible(@NotNull Generic generic, @NotNull NumeralBase from, @NotNull NumeralBase to) {
		return calculator.isConversionPossible(generic, from, to);
	}

	@Override
	@NotNull
	public CalculatorEventData convert(@NotNull Generic generic, @NotNull NumeralBase to) {
		return calculator.convert(generic, to);
	}

	@Override
	@NotNull
	public CalculatorEventData fireCalculatorEvent(@NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
		return calculator.fireCalculatorEvent(calculatorEventType, data);
	}

	@NotNull
	@Override
	public CalculatorEventData fireCalculatorEvent(@NotNull CalculatorEventType calculatorEventType, @Nullable Object data, @NotNull Object source) {
		return calculator.fireCalculatorEvent(calculatorEventType, data, source);
	}

	@Override
	@NotNull
	public CalculatorEventData fireCalculatorEvent(@NotNull CalculatorEventType calculatorEventType, @Nullable Object data, @NotNull Long sequenceId) {
		return calculator.fireCalculatorEvent(calculatorEventType, data, sequenceId);
	}

	@NotNull
	@Override
	public PreparedExpression prepareExpression(@NotNull String expression) throws CalculatorParseException {
		return calculator.prepareExpression(expression);
	}

	@Override
	public void init() {
		this.calculator.init();

		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		this.calculator.setCalculateOnFly(CalculatorPreferences.Calculations.calculateOnFly.getPreference(prefs));
	}

	@Override
	public void addCalculatorEventListener(@NotNull CalculatorEventListener calculatorEventListener) {
		calculator.addCalculatorEventListener(calculatorEventListener);
	}

	@Override
	public void removeCalculatorEventListener(@NotNull CalculatorEventListener calculatorEventListener) {
		calculator.removeCalculatorEventListener(calculatorEventListener);
	}

	@Override
	public void fireCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
		calculator.fireCalculatorEvent(calculatorEventData, calculatorEventType, data);
	}

	@Override
	public void fireCalculatorEvents(@NotNull List<CalculatorEvent> calculatorEvents) {
		calculator.fireCalculatorEvents(calculatorEvents);
	}

	@Override
	public void doHistoryAction(@NotNull HistoryAction historyAction) {
		calculator.doHistoryAction(historyAction);
	}

	@Override
	public void setCurrentHistoryState(@NotNull CalculatorHistoryState editorHistoryState) {
		calculator.setCurrentHistoryState(editorHistoryState);
	}

	@Override
	@NotNull
	public CalculatorHistoryState getCurrentHistoryState() {
		return calculator.getCurrentHistoryState();
	}

	@Override
	public void evaluate() {
		calculator.evaluate();
	}

	@Override
	public void evaluate(@NotNull Long sequenceId) {
		calculator.evaluate(sequenceId);
	}

	@Override
	public void simplify() {
		calculator.simplify();
	}

	@Override
	public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
		switch (calculatorEventType) {
			case calculation_messages:
				CalculatorActivityLauncher.showCalculationMessagesDialog(CalculatorApplication.getInstance(), (List<Message>) data);
				break;
			case show_history:
				CalculatorActivityLauncher.showHistory(CalculatorApplication.getInstance());
				break;
			case show_history_detached:
				CalculatorActivityLauncher.showHistory(CalculatorApplication.getInstance(), true);
				break;
			case show_functions:
				CalculatorActivityLauncher.showFunctions(CalculatorApplication.getInstance());
				break;
			case show_functions_detached:
				CalculatorActivityLauncher.showFunctions(CalculatorApplication.getInstance(), true);
				break;
			case show_operators:
				CalculatorActivityLauncher.showOperators(CalculatorApplication.getInstance());
				break;
			case show_operators_detached:
				CalculatorActivityLauncher.showOperators(CalculatorApplication.getInstance(), true);
				break;
			case show_vars:
				CalculatorActivityLauncher.showVars(CalculatorApplication.getInstance());
				break;
			case show_vars_detached:
				CalculatorActivityLauncher.showVars(CalculatorApplication.getInstance(), true);
				break;
			case show_settings:
				CalculatorActivityLauncher.showSettings(CalculatorApplication.getInstance());
				break;
			case show_settings_detached:
				CalculatorActivityLauncher.showSettings(CalculatorApplication.getInstance(), true);
				break;
			case show_like_dialog:
				CalculatorActivityLauncher.likeButtonPressed(CalculatorApplication.getInstance());
				break;
			case open_app:
				CalculatorActivityLauncher.openApp(CalculatorApplication.getInstance());
				break;
		}
	}

	@Override
	public void onSharedPreferenceChanged(@NotNull SharedPreferences prefs, @NotNull String key) {
		if (CalculatorPreferences.Calculations.calculateOnFly.getKey().equals(key)) {
			this.calculator.setCalculateOnFly(CalculatorPreferences.Calculations.calculateOnFly.getPreference(prefs));
		}
	}
}
