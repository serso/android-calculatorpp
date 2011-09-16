package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.ClipboardManager;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import bsh.EvalError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.MutableObject;
import org.solovyev.common.utils.StringUtils;
import org.solovyev.common.utils.history.HistoryAction;
import org.solovyev.common.utils.history.HistoryHelper;
import org.solovyev.common.utils.history.SimpleHistoryHelper;
import org.solovyev.util.math.MathEntityType;

/**
 * User: serso
 * Date: 9/12/11
 * Time: 11:15 PM
 */
public class CalculatorView implements CursorControl{

	@NotNull
	private final EditText editor;

	@NotNull
	private final TextView display;

	@NotNull
	private final Activity activity;

	@NotNull
	private final CalculatorModel calculator;

	@NotNull
	private HistoryHelper<CalculatorHistoryState> history;

	public CalculatorView(@NotNull final Activity activity, @NotNull CalculatorModel calculator) {
		this.activity = activity;
		this.calculator = calculator;

		final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

		this.editor = (EditText) activity.findViewById(R.id.editText);
		this.editor.setInputType(InputType.TYPE_NULL);
		imm.hideSoftInputFromWindow(this.editor.getWindowToken(), 0);

		this.display = (TextView) activity.findViewById(R.id.resultEditText);
		this.display.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final CharSequence text = ((TextView) v).getText();
				if (!StringUtils.isEmpty(text)) {
					final ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Activity.CLIPBOARD_SERVICE);
					clipboard.setText(text);
					Toast.makeText(activity, "Result copied to clipboard!", Toast.LENGTH_SHORT).show();
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

			currentRunner.setObject(new Runnable() {
				@Override
				public void run() {
					synchronized (currentRunner) {
						// do only if nothing was post delayed before current instance was posted
						if (currentRunner.getObject() == this) {
							// actually nothing shall be logged while text operations are done
							evaluate(editorStateAfter, true);

							if (history.isRedoAvailable()) {
								history.redo(getCurrentHistoryState());
							}
							saveHistoryState();
						}
					}
				}
			});

			new Handler().postDelayed(currentRunner.getObject(), 500);

			saveHistoryState();
		}
	}

	private void evaluate(@Nullable final String expression, final boolean showError) {
		if (!StringUtils.isEmpty(expression)) {

			final TextView localDisplay = display;
			final Activity localActivity = activity;

			try {
				localDisplay.setText(calculator.evaluate(JsclOperation.numeric, expression));
			} catch (EvalError evalError) {
				if (showError) {
					Toast.makeText(localActivity, R.string.syntax_error, Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	public void clear() {
		if (!StringUtils.isEmpty(editor.getText()) || !StringUtils.isEmpty(editor.getText())) {
			editor.getText().clear();
			display.setText("");
			saveHistoryState();
		}
	}

	public void evaluate() {
		evaluate(editor.getText().toString(), true);
	}

	public void processButtonAction(@Nullable final String text) {
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

	public void doHistoryAction(@NotNull HistoryAction historyAction) {
		if (history.isActionAvailable(historyAction)) {
			final CalculatorHistoryState newState = history.doAction(historyAction, getCurrentHistoryState());
			if (newState != null) {
				setCurrentHistoryState(newState);
			}
		}
	}

	public void setCurrentHistoryState(@NotNull CalculatorHistoryState editorHistoryState) {
		setValuesFromHistory(this.editor, editorHistoryState.getEditorState());
		setValuesFromHistory(this.display, editorHistoryState.getDisplayState());
	}

	private void setValuesFromHistory(@NotNull TextView editText, EditorHistoryState editorHistoryState) {
		editText.setText(editorHistoryState.getText());
		if (editText instanceof EditText) {
			((EditText) editText).setSelection(editorHistoryState.getCursorPosition());
		}
	}

	@NotNull
	public CalculatorHistoryState getCurrentHistoryState() {
		return new CalculatorHistoryState(getEditorHistoryState(this.editor), getEditorHistoryState(this.display));
	}

	private EditorHistoryState getEditorHistoryState(@NotNull TextView textView) {
		final EditorHistoryState result = new EditorHistoryState();

		result.setText(String.valueOf(textView.getText()));
		result.setCursorPosition(textView.getSelectionStart());

		return result;
	}
}
