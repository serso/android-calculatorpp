package org.solovyev.android.calculator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.R;
import org.solovyev.android.view.DragButton;
import org.solovyev.android.view.DragEvent;
import org.solovyev.android.view.OnDragListener;
import org.solovyev.android.view.SimpleOnDragListener;
import org.solovyev.util.StringUtils;
import org.solovyev.util.math.MathEntityType;

import bsh.EvalError;
import bsh.Interpreter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class Calculator extends Activity {

	@NotNull
	private EditText editText;
	
	@NotNull
	private EditText resultEditText;

	@NotNull
	private Interpreter interpreter;

	@NotNull
	private HistoryHelper<EditorHistoryState> historyHelper;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		this.editText = (EditText) findViewById(R.id.editText);
		
		this.resultEditText = (EditText) findViewById(R.id.resultEditText);

		final SimpleOnDragListener onDragListener = new SimpleOnDragListener();

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
					Log.e(Calculator.class.getName(), e.getMessage());
				} catch (IllegalAccessException e) {
					Log.e(Calculator.class.getName(), e.getMessage());
				}
			}
		}

		((DragButton) findViewById(R.id.historyButton)).setOnDragListener(new HistoryOnDragListener());
		
		this.interpreter = new Interpreter();

		try {
			interpreter.eval(Preprocessor.wrap(JsclOperation.importCommands, "/jscl/editorengine/commands"));
		} catch (EvalError e) {
			Log.e(Calculator.class.getName(), e.getMessage());
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
			Log.e(Calculator.class.getName(), e.getMessage());
			resultEditText.setText(R.string.syntax_error);
		}
	}
	
	public void digitButtonClickHandler(@NotNull View v) {
		processButtonAction(v, ((DragButton) v).getTextMiddle());
	}
	
	private final class HistoryOnDragListener implements OnDragListener {
		@Override
		public void onDrag(@NotNull DragButton dragButton, @NotNull DragEvent event) {
			Log.d(String.valueOf(dragButton.getId()), "History on drag event start: " + event.getDirection());

			String actionText = getActionText(dragButton, event);
			if (!StringUtils.isEmpty(actionText)) {
				try {
					final HistoryAction historyAction = HistoryAction.valueOf(actionText);
					if ( historyHelper.isActionAvailable(historyAction) ){
						final EditorHistoryState newState = historyHelper.doAction(historyAction, getCurrentHistoryState());
						if (newState != null) {
							setCurrentHistoryState(newState);
						}
					}
				} catch (IllegalArgumentException e) {
					Log.e(String.valueOf(dragButton.getId()), "Unsupported history action: " + actionText);
				}
			}
		}
	}

	@Nullable
	private static String getActionText(@NotNull DragButton dragButton, @NotNull DragEvent event) {
		final String result;
		
		switch(event.getDirection()) {
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
	
	public void setCurrentHistoryState(@Nullable EditorHistoryState editorHistoryState) {
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
		//Toast.makeText(Calculator.this, text, Toast.LENGTH_SHORT).show();

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
}