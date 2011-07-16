package org.solovyev.android.calculator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import android.content.Intent;
import android.view.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.view.*;
import org.solovyev.util.StringUtils;
import org.solovyev.util.math.MathEntityType;

import bsh.EvalError;
import bsh.Interpreter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import org.solovyev.util.math.Point2d;

public class CalculatorActivity extends Activity {

	@NotNull
	private EditText editText;

	@NotNull
	private EditText resultEditText;

	@NotNull
	private Interpreter interpreter;

	@NotNull
	private HistoryHelper<EditorHistoryState> historyHelper;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		this.editText = (EditText) findViewById(R.id.editText);

		this.resultEditText = (EditText) findViewById(R.id.resultEditText);

		final SimpleOnDragListener onDragListener = new SimpleOnDragListener(new SimpleOnDragListener.DragProcessor() {
			@Override
			public boolean processDragEvent(@NotNull DragDirection dragDirection, @NotNull DragButton dragButton, @NotNull Point2d startPoint2d, @NotNull MotionEvent motionEvent) {
				boolean result = isDirectionSupported(dragButton, dragDirection);

				if (result) {
					processButtonAction(dragButton, getActionText(dragButton, dragDirection));
				}

				return result;
			}

			public boolean isDirectionSupported(@NotNull DragButton dragButton, @NotNull DragDirection direction) {
				return !StringUtils.isEmpty(getActionText(dragButton, direction));
			}

		});

		// todo serso: check if there is more convenient method for doing this
		final R.id ids = new R.id();
		for (Field field : R.id.class.getDeclaredFields()) {
			int modifiers = field.getModifiers();
			if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
				try {
					final View view = findViewById(field.getInt(ids));
					if (view instanceof DragButton) {
						((DragButton) view).setOnDragListener(onDragListener);
					}
				} catch (IllegalArgumentException e) {
					Log.e(CalculatorActivity.class.getName(), e.getMessage());
				} catch (IllegalAccessException e) {
					Log.e(CalculatorActivity.class.getName(), e.getMessage());
				}
			}
		}

		((DragButton) findViewById(R.id.historyButton)).setOnDragListener(new SimpleOnDragListener(new HistoryDragProcessor()));

		this.interpreter = new Interpreter();

		try {
			interpreter.eval(Preprocessor.wrap(JsclOperation.importCommands, "/jscl/editorengine/commands"));
		} catch (EvalError e) {
			Log.e(CalculatorActivity.class.getName(), e.getMessage());
		}

		this.historyHelper = new SimpleHistoryHelper<EditorHistoryState>();
		this.historyHelper.addState(getCurrentHistoryState());
	}

	public void elementaryButtonClickHandler(@NotNull View v) {
		eval(JsclOperation.elementary);
	}

	public void numericButtonClickHandler(@NotNull View v) {
		eval(JsclOperation.numeric);
	}

	public void simplifyButtonClickHandler(@NotNull View v) {
		eval(JsclOperation.simplify);
	}

	private void eval(@NotNull JsclOperation operation) {
		try {
			final String preprocessedString = Preprocessor.process(String.valueOf(editText.getText()));
			resultEditText.setText(String.valueOf(interpreter.eval(Preprocessor.wrap(operation, preprocessedString))));
		} catch (EvalError e) {
			Log.e(CalculatorActivity.class.getName(), e.getMessage());
			resultEditText.setText(R.string.syntax_error);
		}
	}

	public void digitButtonClickHandler(@NotNull View v) {
		processButtonAction(v, ((DragButton) v).getTextMiddle());
	}

	private final class HistoryDragProcessor implements SimpleOnDragListener.DragProcessor {

		@Override
		public boolean processDragEvent(@NotNull DragDirection dragDirection, @NotNull DragButton dragButton, @NotNull Point2d startPoint2d, @NotNull MotionEvent motionEvent) {
			boolean result = false;

			Log.d(String.valueOf(dragButton.getId()), "History on drag event start: " + dragDirection);

			String actionText = getActionText(dragButton, dragDirection);
			if (!StringUtils.isEmpty(actionText)) {
				try {
					result = true;

					final HistoryAction historyAction = HistoryAction.valueOf(actionText);
					if (historyHelper.isActionAvailable(historyAction)) {
						final EditorHistoryState newState = historyHelper.doAction(historyAction, getCurrentHistoryState());
						if (newState != null) {
							setCurrentHistoryState(newState);
						}
					}
				} catch (IllegalArgumentException e) {
					Log.e(String.valueOf(dragButton.getId()), "Unsupported history action: " + actionText);
				}
			}

			return result;
		}
	}

	@Nullable
	private static String getActionText(@NotNull DragButton dragButton, @NotNull DragDirection direction) {
		final String result;

		switch (direction) {
			case up:
				result = dragButton.getTextUp();
				break;

			case down:
				result = dragButton.getTextDown();
				break;

			default:
				result = null;
				break;
		}

		return result;
	}

	public void setCurrentHistoryState(@NotNull EditorHistoryState editorHistoryState) {
		this.editText.setText(editorHistoryState.getText());
		this.editText.setSelection(editorHistoryState.getCursorPosition(), editorHistoryState.getCursorPosition());
	}

	@NotNull
	public EditorHistoryState getCurrentHistoryState() {
		final EditorHistoryState result = new EditorHistoryState();

		result.setText(String.valueOf(this.editText.getText()));
		result.setCursorPosition(this.editText.getSelectionStart());

		return result;
	}

	private void processButtonAction(@NotNull View v, @Nullable String text) {
		//Toast.makeText(CalculatorActivity.this, text, Toast.LENGTH_SHORT).show();

		if (!StringUtils.isEmpty(text)) {
			final MathEntityType type = MathEntityType.getType(text);

			int cursorPositionOffset = 0;

			if (type != null) {
				switch (type) {
					case function:
						text += "()";
						cursorPositionOffset = -1;
						break;
					case group_symbols:
						cursorPositionOffset = -1;
						break;

					default:
						break;
				}

			}

			this.editText.getText().insert(this.editText.getSelectionStart(), text);
			this.editText.setSelection(this.editText.getSelectionStart() + cursorPositionOffset, this.editText.getSelectionEnd() + cursorPositionOffset);
			this.historyHelper.addState(getCurrentHistoryState());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;

		switch (item.getItemId()) {
			case R.id.menu_item_settings:
				showSettings();
				result = true;
			case R.id.menu_item_help:
				showHelp();
				result = true;
			default:
				result = super.onOptionsItemSelected(item);
		}

		return result;
	}

	private void showSettings() {
		startActivity(new Intent(this, CalculatorPreferencesActivity.class));
	}

	private void showHelp() {
		Log.d(CalculatorActivity.class + "showHelp()", "Show help!");
	}
}