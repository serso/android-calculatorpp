/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

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
import org.solovyev.common.utils.Announcer;
import org.solovyev.common.utils.history.HistoryAction;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class CalculatorActivity extends Activity implements FontSizeAdjuster {

	private static final int HVGA_WIDTH_PIXELS = 320;

	@NotNull
	private final Announcer<DragPreferencesChangeListener> dpclRegister = new Announcer<DragPreferencesChangeListener>(DragPreferencesChangeListener.class);

	@NotNull
	private CalculatorView calculatorView;

	@NotNull
	private CalculatorModel calculatorModel;

	@NotNull
	private BroadcastReceiver preferencesChangesReceiver;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		try {
			this.calculatorModel = new CalculatorModel();
		} catch (EvalError evalError) {
			// todo serso: create serso runtime exception
			throw new RuntimeException("Could not initialize interpreter!");
		}

		this.calculatorView = new CalculatorView(this, this.calculatorModel);

		final DragButtonCalibrationActivity.Preferences dragPreferences = DragButtonCalibrationActivity.getPreferences(this);

		final SimpleOnDragListener onDragListener = new SimpleOnDragListener(new DigitButtonDragProcessor(calculatorView), dragPreferences);
		dpclRegister.addListener(onDragListener);

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

		final SimpleOnDragListener historyOnDragListener = new SimpleOnDragListener(new HistoryDragProcessor<CalculatorHistoryState>(this.calculatorView), dragPreferences);
		((DragButton) findViewById(R.id.historyButton)).setOnDragListener(historyOnDragListener);
		dpclRegister.addListener(historyOnDragListener);

		final SimpleOnDragListener toPositionOnDragListener = new SimpleOnDragListener(new CursorDragProcessor(calculatorView), dragPreferences);
		((DragButton) findViewById(R.id.rightButton)).setOnDragListener(toPositionOnDragListener);
		((DragButton) findViewById(R.id.leftButton)).setOnDragListener(toPositionOnDragListener);
		dpclRegister.addListener(toPositionOnDragListener);


		preferencesChangesReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				if (DragButtonCalibrationActivity.INTENT_ACTION.equals(intent.getAction())) {
					final DragButtonCalibrationActivity.Preferences preferences = DragButtonCalibrationActivity.getPreferences(CalculatorActivity.this);
					dpclRegister.announce().onDragPreferencesChange(preferences);
				}
			}
		};

		registerReceiver(preferencesChangesReceiver, new IntentFilter(DragButtonCalibrationActivity.INTENT_ACTION));
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(preferencesChangesReceiver);
		super.onDestroy();
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void elementaryButtonClickHandler(@NotNull View v) {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void numericButtonClickHandler(@NotNull View v) {
		this.calculatorView.evaluate();
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void eraseButtonClickHandler(@NotNull View v) {
		calculatorView.doTextOperation(new CalculatorView.TextOperation() {
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
		calculatorView.moveCursorLeft();
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void moveRightButtonClickHandler(@NotNull View v) {
		calculatorView.moveCursorRight();
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void pasteButtonClickHandler(@NotNull View v) {
		calculatorView.doTextOperation(new CalculatorView.TextOperation() {
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
		calculatorView.clear();
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void digitButtonClickHandler(@NotNull View v) {
		Log.d(String.valueOf(v.getId()), "digitButtonClickHandler() for: " + v.getId() + ". Pressed: " + v.isPressed());
		calculatorView.processDigitButtonAction(((DirectionDragButton) v).getTextMiddle());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			calculatorView.doHistoryAction(HistoryAction.undo);
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
				break;
			case R.id.menu_item_about:
				showAbout();
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

	private void showAbout() {
		startActivity(new Intent(this, AboutActivity.class));
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