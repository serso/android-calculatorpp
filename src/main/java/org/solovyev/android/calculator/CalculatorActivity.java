package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import bsh.EvalError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.view.*;
import org.solovyev.util.StringUtils;
import org.solovyev.util.math.Point2d;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class CalculatorActivity extends Activity implements FontSizeAdjuster {

	private static final int HVGA_WIDTH_PIXELS = 320;

	@NotNull
	private List<SimpleOnDragListener> onDragListeners = new ArrayList<SimpleOnDragListener>();

	@NotNull
	private CalculatorView view;

	@NotNull
	private CalculatorModel calculator;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		try {
			this.calculator = new CalculatorModel();
		} catch (EvalError evalError) {
			// todo serso: create serso runtime exception
			throw new RuntimeException("Could not initialize interpreter!");
		}

		this.view = new CalculatorView(this, this.calculator);

		final DragButtonCalibrationActivity.Preferences dragPreferences = DragButtonCalibrationActivity.getPreferences(this);

		final SimpleOnDragListener onDragListener = new SimpleOnDragListener(new SimpleOnDragListener.DragProcessor() {
			@Override
			public boolean processDragEvent(@NotNull DragDirection dragDirection, @NotNull DragButton dragButton, @NotNull Point2d startPoint2d, @NotNull MotionEvent motionEvent) {
				assert dragButton instanceof DirectionDragButton;
				view.processButtonAction(getActionText((DirectionDragButton) dragButton, dragDirection));
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
		((DragButton) findViewById(R.id.historyButton)).setOnDragListener(historyOnDragListener);
		onDragListeners.add(historyOnDragListener);

		final SimpleOnDragListener toPositionOnDragListener = new SimpleOnDragListener(new SimpleOnDragListener.DragProcessor() {
			@Override
			public boolean processDragEvent(@NotNull DragDirection dragDirection, @NotNull DragButton dragButton, @NotNull Point2d startPoint2d, @NotNull MotionEvent motionEvent) {
				boolean result = false;

				if (dragButton instanceof DirectionDragButton) {
					String text = ((DirectionDragButton) dragButton).getText(dragDirection);
					if ("↞".equals(text)) {
						CalculatorActivity.this.view.setCursorOnStart();
					} else if ("↠".equals(text)) {
						CalculatorActivity.this.view.setCursorOnEnd();
					}
				}

				return result;
			}
		}, dragPreferences);
		((DragButton) findViewById(R.id.rightButton)).setOnDragListener(toPositionOnDragListener);
		((DragButton) findViewById(R.id.leftButton)).setOnDragListener(toPositionOnDragListener);
		onDragListeners.add(toPositionOnDragListener);


		final BroadcastReceiver preferencesChangesReceiver = new BroadcastReceiver() {
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

		registerReceiver(preferencesChangesReceiver, new IntentFilter(DragButtonCalibrationActivity.INTENT_ACTION));
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void elementaryButtonClickHandler(@NotNull View v) {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void numericButtonClickHandler(@NotNull View v) {
		this.view.evaluate();
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void eraseButtonClickHandler(@NotNull View v) {
		view.doTextOperation(new CalculatorView.TextOperation() {
			@Override
			public void doOperation(@NotNull EditText editor) {
				if (editor.getSelectionStart() > 0) {
					editor.getText().delete(editor.getSelectionStart() - 1, editor.getSelectionStart());
				}
			}
		});
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void simplifyButtonClickHandler(@NotNull View v) {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void moveLeftButtonClickHandler(@NotNull View v) {
		view.moveCursorLeft();
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void moveRightButtonClickHandler(@NotNull View v) {
		view.moveCursorRight();
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void pasteButtonClickHandler(@NotNull View v) {
		view.doTextOperation(new CalculatorView.TextOperation() {
			@Override
			public void doOperation(@NotNull EditText editor) {
				final ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				if (clipboard.hasText()) {
					editor.getText().insert(editor.getSelectionStart(), clipboard.getText());
				}
			}
		});
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void clearButtonClickHandler(@NotNull View v) {
		view.clear();
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void digitButtonClickHandler(@NotNull View v) {
		view.processButtonAction(((DirectionDragButton) v).getTextMiddle());
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
					view.doHistoryAction(historyAction);
				} catch (IllegalArgumentException e) {
					Log.e(String.valueOf(dragButton.getId()), "Unsupported history action: " + actionText);
				}
			}

			return result;
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			view.doHistoryAction(HistoryAction.undo);
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
				break;
			case R.id.menu_item_help:
				showHelp();
				result = true;
				break;
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