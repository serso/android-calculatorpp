package org.solovyev.android.calculator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.ClipboardManager;
import android.text.InputType;
import android.util.TypedValue;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
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
import org.solovyev.util.math.MathUtils;
import org.solovyev.util.math.Point2d;

public class CalculatorActivity extends Activity implements FontSizeAdjuster {

	private static final int HVGA_WIDTH_PIXELS = 320;

	@NotNull
	private EditText editText;

	@NotNull
	private TextView resultEditText;

	@NotNull
	private Interpreter interpreter;

	@NotNull
	private HistoryHelper<CalculatorHistoryState> historyHelper;

	@NotNull
	private BroadcastReceiver preferencesChangesReceiver;

	@NotNull
	private List<SimpleOnDragListener> onDragListeners = new ArrayList<SimpleOnDragListener>();

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		this.editText = (EditText) findViewById(R.id.editText);
		this.editText.setInputType(InputType.TYPE_NULL);
		imm.hideSoftInputFromWindow(this.editText.getWindowToken(), 0);

		this.resultEditText = (TextView) findViewById(R.id.resultEditText);
		this.resultEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final CharSequence text = ((TextView) v).getText();
				if (!StringUtils.isEmpty(text)) {
					final ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
					clipboard.setText(text);
					Toast.makeText(CalculatorActivity.this, "Result copied to clipboard!", Toast.LENGTH_SHORT).show();
				}
			}
		});


		final DragButtonCalibrationActivity.Preferences dragPreferences = DragButtonCalibrationActivity.getPreferences(this);

		final SimpleOnDragListener onDragListener = new SimpleOnDragListener(new SimpleOnDragListener.DragProcessor() {
			@Override
			public boolean processDragEvent(@NotNull DragDirection dragDirection, @NotNull DragButton dragButton, @NotNull Point2d startPoint2d, @NotNull MotionEvent motionEvent) {
				assert dragButton instanceof DirectionDragButton;
				processButtonAction(dragButton, getActionText((DirectionDragButton) dragButton, dragDirection));
				return true;
			}

		}, dragPreferences);

		onDragListeners.add(onDragListener);

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

		final SimpleOnDragListener historyOnDragListener = new SimpleOnDragListener(new HistoryDragProcessor(), dragPreferences);
		((DragButton) findViewById(R.id.clearButton)).setOnDragListener(historyOnDragListener);
		((DragButton) findViewById(R.id.pasteButton)).setOnDragListener(historyOnDragListener);
		onDragListeners.add(historyOnDragListener);

		final SimpleOnDragListener toPositionOnDragListener = new SimpleOnDragListener(new SimpleOnDragListener.DragProcessor() {
			@Override
			public boolean processDragEvent(@NotNull DragDirection dragDirection, @NotNull DragButton dragButton, @NotNull Point2d startPoint2d, @NotNull MotionEvent motionEvent) {
				boolean result = false;

				if (dragButton instanceof DirectionDragButton) {
					String text = ((DirectionDragButton) dragButton).getText(dragDirection);
					if ("↞".equals(text)) {
						CalculatorActivity.this.editText.setSelection(0);
					} else if ("↠".equals(text)) {
						CalculatorActivity.this.editText.setSelection(CalculatorActivity.this.editText.getText().length());
					}
				}

				return result;
			}
		}, dragPreferences);
		((DragButton) findViewById(R.id.rightButton)).setOnDragListener(toPositionOnDragListener);
		((DragButton) findViewById(R.id.leftButton)).setOnDragListener(toPositionOnDragListener);
		onDragListeners.add(toPositionOnDragListener);

		this.interpreter = new Interpreter();

		try {
			interpreter.eval(Preprocessor.wrap(JsclOperation.importCommands, "/jscl/editorengine/commands"));
		} catch (EvalError e) {
			Log.e(CalculatorActivity.class.getName(), e.getMessage());
		}

		this.historyHelper = new SimpleHistoryHelper<CalculatorHistoryState>();
		saveHistoryState();

		this.preferencesChangesReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				if (DragButtonCalibrationActivity.INTENT_ACTION.equals(intent.getAction())) {
					final DragButtonCalibrationActivity.Preferences preferences = DragButtonCalibrationActivity.getPreferences(CalculatorActivity.this);
					for (SimpleOnDragListener dragListener : onDragListeners) {
						dragListener.setPreferences(preferences);
					}
				}
			}
		};

		registerReceiver(this.preferencesChangesReceiver, new IntentFilter(DragButtonCalibrationActivity.INTENT_ACTION));
	}

	private void saveHistoryState() {
		historyHelper.addState(getCurrentHistoryState());
	}

	public void elementaryButtonClickHandler(@NotNull View v) {
		eval(JsclOperation.elementary, true);
	}

	public void numericButtonClickHandler(@NotNull View v) {
		eval(JsclOperation.numeric, true);
	}

	public void eraseButtonClickHandler(@NotNull View v) {
		if (editText.getSelectionStart() > 0) {
			editText.getText().delete(editText.getSelectionStart() - 1, editText.getSelectionStart());
			saveHistoryState();
		}
	}

	public void simplifyButtonClickHandler(@NotNull View v) {
		eval(JsclOperation.simplify, true);
	}

	public void moveLeftButtonClickHandler(@NotNull View v) {
		if (editText.getSelectionStart() > 0) {
			editText.setSelection(editText.getSelectionStart() - 1);
		}
	}

	public void moveRightButtonClickHandler(@NotNull View v) {
		if (editText.getSelectionStart() < editText.getText().length()) {
			editText.setSelection(editText.getSelectionStart() + 1);
		}
	}

	public void pasteButtonClickHandler(@NotNull View v) {
		final ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		if ( clipboard.hasText() ) {
			editText.getText().append(clipboard.getText());
			saveHistoryState();
		}
	}


	public void clearButtonClickHandler(@NotNull View v) {
		if (!StringUtils.isEmpty(editText.getText()) || !StringUtils.isEmpty(resultEditText.getText())) {
			editText.getText().clear();
			resultEditText.setText("");
			saveHistoryState();
		}
	}

	private void eval(@NotNull JsclOperation operation, boolean showError) {
		try {
			final String preprocessedString = Preprocessor.process(String.valueOf(editText.getText()));

			String result = String.valueOf(interpreter.eval(Preprocessor.wrap(operation, preprocessedString))).trim();

			try {
				final Double dResult = Double.valueOf(result);
				result = String.valueOf(MathUtils.round(dResult, 5));
			} catch (NumberFormatException e) {
			}

			resultEditText.setText(result);

			// result editor might be changed (but main editor - no) => make undo and add new state with saved result
			CalculatorHistoryState currentHistoryState = getCurrentHistoryState();
			if (this.historyHelper.isUndoAvailable()) {
				this.historyHelper.undo(currentHistoryState);
			}

			this.historyHelper.addState(currentHistoryState);

		} catch (EvalError e) {
			if (showError) {
				Toast.makeText(CalculatorActivity.this, R.string.syntax_error, Toast.LENGTH_SHORT).show();
				Log.e(CalculatorActivity.class.getName(), e.getMessage());
			}
		}
	}

	public void digitButtonClickHandler(@NotNull View v) {
		processButtonAction(v, ((DirectionDragButton) v).getTextMiddle());
	}

	private final class HistoryDragProcessor implements SimpleOnDragListener.DragProcessor {

		@Override
		public boolean processDragEvent(@NotNull DragDirection dragDirection, @NotNull DragButton dragButton, @NotNull Point2d startPoint2d, @NotNull MotionEvent motionEvent) {
			boolean result = false;

			Log.d(String.valueOf(dragButton.getId()), "History on drag event start: " + dragDirection);

			assert dragButton instanceof DirectionDragButton;
			String actionText = getActionText((DirectionDragButton) dragButton, dragDirection);
			if (!StringUtils.isEmpty(actionText)) {
				try {
					result = true;

					final HistoryAction historyAction = HistoryAction.valueOf(actionText);
					doHistoryAction(historyAction);
				} catch (IllegalArgumentException e) {
					Log.e(String.valueOf(dragButton.getId()), "Unsupported history action: " + actionText);
				}
			}

			return result;
		}
	}

	private void doHistoryAction(@NotNull HistoryAction historyAction) {
		if (historyHelper.isActionAvailable(historyAction)) {
			final CalculatorHistoryState newState = historyHelper.doAction(historyAction, getCurrentHistoryState());
			if (newState != null) {
				setCurrentHistoryState(newState);
			}
		}
	}

	@Nullable
	private static String getActionText(@NotNull DirectionDragButton dragButton, @NotNull DragDirection direction) {
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

	public void setCurrentHistoryState(@NotNull CalculatorHistoryState editorHistoryState) {
		setValuesFromHistory(this.editText, editorHistoryState.getEditorState());
		setValuesFromHistory(this.resultEditText, editorHistoryState.getResultEditorState());
	}

	private void setValuesFromHistory(@NotNull TextView editText, EditorHistoryState editorHistoryState) {
		editText.setText(editorHistoryState.getText());
		if (editText instanceof EditText) {
			((EditText) editText).setSelection(editorHistoryState.getCursorPosition());
		}
	}

	@NotNull
	public CalculatorHistoryState getCurrentHistoryState() {
		return new CalculatorHistoryState(getEditorHistoryState(this.editText), getEditorHistoryState(this.resultEditText));
	}

	private EditorHistoryState getEditorHistoryState(@NotNull TextView textView) {
		final EditorHistoryState result = new EditorHistoryState();

		result.setText(String.valueOf(textView.getText()));
		result.setCursorPosition(textView.getSelectionStart());

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
			saveHistoryState();
			eval(JsclOperation.numeric, false);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			doHistoryAction(HistoryAction.undo);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// todo serso: inflate menu as soon as it will implemented in proper way
/*		final MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main_menu, menu);*/
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

	/**
	 * The font sizes in the layout files are specified for a HVGA display.
	 * Adjust the font sizes accordingly if we are running on a different
	 * display.
	 */
	@Override
	public void adjustFontSize(@NotNull TextView view) {
		float fontPixelSize = view.getTextSize();
		Display display = getWindowManager().getDefaultDisplay();
		int h = Math.min(display.getWidth(), display.getHeight());
		float ratio = (float) h / HVGA_WIDTH_PIXELS;
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontPixelSize * ratio);
	}
}