/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import bsh.EvalError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.calculator.model.ParseException;
import org.solovyev.android.view.CursorControl;
import org.solovyev.android.view.HistoryControl;
import org.solovyev.common.BooleanMapper;
import org.solovyev.common.utils.MutableObject;
import org.solovyev.common.utils.StringUtils;
import org.solovyev.common.utils.history.HistoryAction;

/**
 * User: serso
 * Date: 9/12/11
 * Time: 11:15 PM
 */
public enum CalculatorModel implements CursorControl, HistoryControl<CalculatorHistoryState>, CalculatorEngineControl {

	instance;

	// millis to wait before evaluation after user edit action
	public static final int EVAL_DELAY_MILLIS = 1000;

	@NotNull
	private CalculatorEditor editor;

	@NotNull
	private CalculatorDisplay display;

	@NotNull
	private CalculatorEngine calculatorEngine;

	public CalculatorModel init(@NotNull final Activity activity, @NotNull SharedPreferences preferences, @NotNull CalculatorEngine calculator) {
		Log.d(this.getClass().getName(), "CalculatorModel initialization with activity: " + activity);
		this.calculatorEngine = calculator;

		this.editor = (CalculatorEditor) activity.findViewById(R.id.calculatorEditor);

		final Boolean colorExpressionsInBracketsDefault = new BooleanMapper().parseValue(activity.getString(R.string.p_calc_color_display));
		assert colorExpressionsInBracketsDefault != null;
		this.editor.setHighlightText(preferences.getBoolean(activity.getString(R.string.p_calc_color_display_key), colorExpressionsInBracketsDefault));

		this.display = (CalculatorDisplay) activity.findViewById(R.id.calculatorDisplay);
		this.display.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				copyResult(activity);
			}
		});


		final CalculatorHistoryState lastState = CalculatorHistory.instance.getLastHistoryState();
		if (lastState == null) {
			saveHistoryState();
		} else {
			setCurrentHistoryState(lastState);
		}


		return this;
	}

	public void copyResult(@NotNull Context context) {
		if (display.isValid()) {
			final CharSequence text = display.getText();
			if (!StringUtils.isEmpty(text)) {
				final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
				clipboard.setText(text);
				Toast.makeText(context, context.getText(R.string.c_result_copied), Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void saveHistoryState() {
		CalculatorHistory.instance.addState(getCurrentHistoryState());
	}

	public void setCursorOnStart() {
		editor.setSelection(0);
	}

	public void setCursorOnEnd() {
		editor.setSelection(editor.getText().length());
	}

	public void moveCursorLeft() {
		if (editor.getSelectionStart() > 0) {
			editor.setSelection(editor.getSelectionStart() - 1);
		}
	}

	public void moveCursorRight() {
		if (editor.getSelectionStart() < editor.getText().length()) {
			editor.setSelection(editor.getSelectionStart() + 1);
		}
	}

	public void doTextOperation(@NotNull TextOperation operation) {
		doTextOperation(operation, true);
	}

	public void doTextOperation(@NotNull TextOperation operation, boolean delayEvaluate) {
		doTextOperation(operation, delayEvaluate, JsclOperation.numeric, false);
	}

	public void doTextOperation(@NotNull TextOperation operation, boolean delayEvaluate, @NotNull JsclOperation jsclOperation, boolean forceEval) {
		final String editorStateBefore = this.editor.getText().toString();

		Log.d(CalculatorModel.class.getName(), "Editor state changed before '" + editorStateBefore + "'");
		operation.doOperation(this.editor);
		//Log.d(CalculatorModel.class.getName(), "Doing text operation" + StringUtils.fromStackTrace(Thread.currentThread().getStackTrace()));

		final String editorStateAfter = this.editor.getText().toString();
		if (forceEval ||!editorStateBefore.equals(editorStateAfter)) {

			editor.redraw();

			evaluate(delayEvaluate, editorStateAfter, jsclOperation, null);
		}
	}

	@NotNull
	private final static MutableObject<Runnable> pendingOperation = new MutableObject<Runnable>();

	private void evaluate(boolean delayEvaluate, @NotNull final String expression, @NotNull final JsclOperation operation, @Nullable CalculatorHistoryState historyState) {
		final CalculatorHistoryState localHistoryState;
		if (historyState == null) {
			localHistoryState = getCurrentHistoryState();
		} else {
			localHistoryState = historyState;
		}

		pendingOperation.setObject(new Runnable() {
			@Override
			public void run() {
				// allow only one runner at one time
				synchronized (pendingOperation) {
					//lock all operations with history
					if (pendingOperation.getObject() == this) {
						// actually nothing shall be logged while text operations are done
						evaluate(expression, operation, this);

						localHistoryState.setDisplayState(getCurrentHistoryState().getDisplayState());

						if (pendingOperation.getObject() == this) {
							// todo serso: of course there is small probability that someone will set pendingOperation after if statement but before .setObject(null)
							pendingOperation.setObject(null);
						}
					}
				}
			}
		});

		if (delayEvaluate) {
			if (historyState == null) {
				CalculatorHistory.instance.addState(localHistoryState);
			}
			new Handler().postDelayed(pendingOperation.getObject(), EVAL_DELAY_MILLIS);
		} else {
			pendingOperation.getObject().run();
			if (historyState == null) {
				CalculatorHistory.instance.addState(localHistoryState);
			}
		}
	}

	@Override
	public void evaluate() {
   		evaluate(false, this.editor.getText().toString(), JsclOperation.numeric, null);
	}

	@Override
	public void simplify() {
   		evaluate(false, this.editor.getText().toString(), JsclOperation.simplify, null);
	}

	private void evaluate(@Nullable final String expression,
						  @NotNull JsclOperation operation,
						  @NotNull Runnable currentRunner) {

		if (!StringUtils.isEmpty(expression)) {
			try {
				Log.d(CalculatorModel.class.getName(), "Trying to evaluate '" + operation + "': " + expression /*+ StringUtils.fromStackTrace(Thread.currentThread().getStackTrace())*/);
				final CalculatorEngine.Result result = calculatorEngine.evaluate(operation, expression);
				if (currentRunner == pendingOperation.getObject()) {
					display.setText(result.getResult());
					display.setJsclOperation(result.getUserOperation());
				} else {
					display.setText("");
				}
			} catch (EvalError e) {
				handleEvaluationException(expression, display, operation, e);
			} catch (ParseException e) {
				handleEvaluationException(expression, display, operation, e);
			}
		} else {
			this.display.setText("");
		}

		this.display.redraw();
	}

	private void handleEvaluationException(@NotNull String expression,
										   @NotNull CalculatorDisplay localDisplay,
										   @NotNull JsclOperation operation,
										   @NotNull Exception e) {
		Log.d(CalculatorModel.class.getName(), "Evaluation failed for : " + expression + ". Error message: " + e.getMessage());
		localDisplay.setText(R.string.c_syntax_error);
		localDisplay.setJsclOperation(operation);
		localDisplay.setValid(false);
	}

	public void clear() {
		if (!StringUtils.isEmpty(editor.getText()) || !StringUtils.isEmpty(editor.getText())) {
			editor.getText().clear();
			display.setText("");
			saveHistoryState();
		}
	}

	public void processDigitButtonAction(@Nullable final String text) {
		//Toast.makeText(CalculatorActivity.this, text, Toast.LENGTH_SHORT).show();

		if (!StringUtils.isEmpty(text)) {
			doTextOperation(new CalculatorModel.TextOperation() {

				@Override
				public void doOperation(@NotNull EditText editor) {

					final MathType.Result mathType = MathType.getType(text, 0);

					int cursorPositionOffset = 0;

					final StringBuilder textToBeInserted = new StringBuilder(text);
					switch (mathType.getMathType()) {
						case function:
							textToBeInserted.append("()");
							cursorPositionOffset = -1;
							break;
					}

					if (cursorPositionOffset == 0) {
						if ( MathType.openGroupSymbols.contains(text) ) {
							cursorPositionOffset = -1;
						}
					}

					editor.getText().insert(editor.getSelectionStart(), textToBeInserted.toString());
					editor.setSelection(editor.getSelectionStart() + cursorPositionOffset, editor.getSelectionEnd() + cursorPositionOffset);
				}
			});
		}
	}

	public static interface TextOperation {

		void doOperation(@NotNull EditText editor);

	}

	@Override
	public void doHistoryAction(@NotNull HistoryAction historyAction) {
		synchronized (CalculatorHistory.instance) {
			if (CalculatorHistory.instance.isActionAvailable(historyAction)) {
				final CalculatorHistoryState newState = CalculatorHistory.instance.doAction(historyAction, getCurrentHistoryState());
				if (newState != null) {
					setCurrentHistoryState(newState);
				}
			}
		}
	}

	@Override
	public void setCurrentHistoryState(@NotNull CalculatorHistoryState editorHistoryState) {
		synchronized (CalculatorHistory.instance) {
			Log.d(this.getClass().getName(), "Saved history found: " + editorHistoryState);
			setValuesFromHistory(this.editor, editorHistoryState.getEditorState());
			setValuesFromHistory(this.display, editorHistoryState.getDisplayState());

			final String expression = this.editor.getText().toString();
			if ( !StringUtils.isEmpty(expression) ) {
				if ( StringUtils.isEmpty(this.display.getText().toString()) ) {
					evaluate(false, expression, JsclOperation.numeric, editorHistoryState);
				}
			}

			editor.redraw();
			display.redraw();
		}
	}

	private void setValuesFromHistory(@NotNull CalculatorDisplay display, CalculatorDisplayHistoryState editorHistoryState) {
		setValuesFromHistory(display, editorHistoryState.getEditorHistoryState());
		display.setValid(editorHistoryState.isValid());
		display.setJsclOperation(editorHistoryState.getJsclOperation());
	}

	private void setValuesFromHistory(@NotNull TextView editText, EditorHistoryState editorHistoryState) {
		editText.setText(editorHistoryState.getText());
		if (editText instanceof EditText) {
			((EditText) editText).setSelection(editorHistoryState.getCursorPosition());
		}
	}

	@Override
	@NotNull
	public CalculatorHistoryState getCurrentHistoryState() {
		synchronized (CalculatorHistory.instance) {
			return new CalculatorHistoryState(getEditorHistoryState(this.editor), getCalculatorDisplayHistoryState(this.display));
		}
	}

	private EditorHistoryState getEditorHistoryState(@NotNull TextView textView) {
		return EditorHistoryState.newInstance(textView);
	}

	private CalculatorDisplayHistoryState getCalculatorDisplayHistoryState(@NotNull CalculatorDisplay display) {
		return CalculatorDisplayHistoryState.newInstance(display);
	}

	@NotNull
	public CalculatorEditor getEditor() {
		return editor;
	}
}
