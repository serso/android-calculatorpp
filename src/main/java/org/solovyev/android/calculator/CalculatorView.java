/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.ClipboardManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import bsh.EvalError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.math.MathEntityType;
import org.solovyev.android.view.CursorControl;
import org.solovyev.android.view.HistoryControl;
import org.solovyev.common.utils.MutableObject;
import org.solovyev.common.utils.StringUtils;
import org.solovyev.common.utils.history.HistoryAction;
import org.solovyev.common.utils.history.HistoryHelper;
import org.solovyev.common.utils.history.SimpleHistoryHelper;

/**
 * User: serso
 * Date: 9/12/11
 * Time: 11:15 PM
 */
public class CalculatorView implements CursorControl, HistoryControl<CalculatorHistoryState> {

	// millis to wait before evaluation after user edit action
	public static final int EVAL_DELAY_MILLIS = 500;

	@NotNull
	private final CalculatorEditText editor;

	@NotNull
	private final CalculatorDisplay display;

	@NotNull
	private final Activity activity;

	@NotNull
	private final CalculatorModel calculatorModel;

	@NotNull
	private final HistoryHelper<CalculatorHistoryState> history;

	public CalculatorView(@NotNull final Activity activity, @NotNull CalculatorModel calculator) {
		this.activity = activity;
		this.calculatorModel = calculator;

		final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

		this.editor = (CalculatorEditText) activity.findViewById(R.id.editText);
		this.editor.setInputType(InputType.TYPE_NULL);
		imm.hideSoftInputFromWindow(this.editor.getWindowToken(), 0);

		this.display = (CalculatorDisplay) activity.findViewById(R.id.resultEditText);
		this.display.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (((CalculatorDisplay) v).isValid()) {
					final CharSequence text = ((TextView) v).getText();
					if (!StringUtils.isEmpty(text)) {
						final ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Activity.CLIPBOARD_SERVICE);
						clipboard.setText(text);
						Toast.makeText(activity, "Result copied to clipboard!", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		this.history = new SimpleHistoryHelper<CalculatorHistoryState>();
		saveHistoryState();
	}

	private void saveHistoryState() {
		history.addState(getCurrentHistoryState());
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

	@NotNull
	private final MutableObject<Runnable> currentRunner = new MutableObject<Runnable>();

	public void doTextOperation(@NotNull TextOperation operation) {
		final String editorStateBefore = this.editor.getText().toString();

		operation.doOperation(this.editor);

		final String editorStateAfter = this.editor.getText().toString();
		if (!editorStateBefore.equals(editorStateAfter)) {

			editor.redraw();

			currentRunner.setObject(new Runnable() {
				@Override
				public void run() {
					// allow only one runner at one time
					synchronized (currentRunner) {
						//lock all operations with history
						synchronized (history) {
							// do only if nothing was post delayed before current instance was posted
							if (currentRunner.getObject() == this) {
								// actually nothing shall be logged while text operations are done
								evaluate(editorStateAfter);

								if (history.isUndoAvailable()) {
									history.undo(getCurrentHistoryState());
								}

								saveHistoryState();
							}
						}
					}
				}
			});

			new Handler().postDelayed(currentRunner.getObject(), EVAL_DELAY_MILLIS);

			saveHistoryState();
		}
	}

	private void evaluate(@Nullable final String expression) {
		if (!StringUtils.isEmpty(expression)) {

			final CalculatorDisplay localDisplay = display;

			try {
				Log.d(CalculatorView.class.getName(), "Trying to evaluate: " + expression);
				localDisplay.setText(calculatorModel.evaluate(JsclOperation.numeric, expression));
			} catch (EvalError e) {
				handleEvaluationException(expression, localDisplay, e);
			} catch (CalculatorModel.ParseException e) {
				handleEvaluationException(expression, localDisplay, e);
			}
		}
	}

	private void handleEvaluationException(@NotNull String expression, @NotNull CalculatorDisplay localDisplay, @NotNull Exception e) {
		Log.d(CalculatorView.class.getName(), "Evaluation failed for : " + expression + ". Error message: " + e.getMessage());
		localDisplay.setText(R.string.c_syntax_error);
		localDisplay.setValid(false);
	}

	public void clear() {
		if (!StringUtils.isEmpty(editor.getText()) || !StringUtils.isEmpty(editor.getText())) {
			editor.getText().clear();
			display.setText("");
			saveHistoryState();
		}
	}

	public void evaluate() {
		evaluate(editor.getText().toString());
	}

	public void processDigitButtonAction(@Nullable final String text) {
		//Toast.makeText(CalculatorActivity.this, text, Toast.LENGTH_SHORT).show();

		if (!StringUtils.isEmpty(text)) {
			doTextOperation(new CalculatorView.TextOperation() {

				@Override
				public void doOperation(@NotNull EditText editor) {

					final MathEntityType type = MathEntityType.getType(text);

					int cursorPositionOffset = 0;
					final StringBuilder textToBeInserted = new StringBuilder(text);
					if (type != null) {
						switch (type) {
							case function:
								textToBeInserted.append("()");
								cursorPositionOffset = -1;
								break;
							case group_symbols:
								cursorPositionOffset = -1;
								break;

							default:
								break;
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
		synchronized (history) {
			if (history.isActionAvailable(historyAction)) {
				final CalculatorHistoryState newState = history.doAction(historyAction, getCurrentHistoryState());
				if (newState != null) {
					setCurrentHistoryState(newState);
				}
			}
		}
	}

	@Override
	public void setCurrentHistoryState(@NotNull CalculatorHistoryState editorHistoryState) {
		synchronized (history) {
			setValuesFromHistory(this.editor, editorHistoryState.getEditorState());
			setValuesFromHistory(this.display, editorHistoryState.getDisplayState());

			editor.redraw();
		}
	}

	private void setValuesFromHistory(@NotNull CalculatorDisplay display, CalculatorDisplayHistoryState editorHistoryState) {
		setValuesFromHistory(display, (EditorHistoryState)editorHistoryState);
		display.setValid(editorHistoryState.isValid());
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
		synchronized (history) {
			return new CalculatorHistoryState(getEditorHistoryState(this.editor), getCalculatorDisplayHistoryState(this.display));
		}
	}

	private EditorHistoryState getEditorHistoryState(@NotNull TextView textView) {
		final EditorHistoryState result = new EditorHistoryState();

		result.setText(String.valueOf(textView.getText()));
		result.setCursorPosition(textView.getSelectionStart());

		return result;
	}

	private CalculatorDisplayHistoryState getCalculatorDisplayHistoryState(@NotNull CalculatorDisplay display) {
		final CalculatorDisplayHistoryState result = new CalculatorDisplayHistoryState();

		result.setText(String.valueOf(display.getText()));
		result.setCursorPosition(display.getSelectionStart());
		result.setValid(display.isValid());

		return result;
	}

	@NotNull
	public CalculatorEditText getEditor() {
		return editor;
	}
}
