/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.msg.AndroidMessageRegistry;
import org.solovyev.android.view.FontSizeAdjuster;
import org.solovyev.android.view.widgets.*;
import org.solovyev.common.BooleanMapper;
import org.solovyev.common.utils.Announcer;
import org.solovyev.common.utils.history.HistoryAction;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class CalculatorActivity extends Activity implements FontSizeAdjuster, SharedPreferences.OnSharedPreferenceChangeListener {

	private static final int HVGA_WIDTH_PIXELS = 320;

	private static final long ON_CLICK_VIBRATION_TIME = 100;

	@NotNull
	private final Announcer<DragPreferencesChangeListener> dpclRegister = new Announcer<DragPreferencesChangeListener>(DragPreferencesChangeListener.class);

	@NotNull
	private final static Map<Class<?>, Map<String, Integer>> caches = new HashMap<Class<?>, Map<String, Integer>>(3);

	@NotNull
	private CalculatorModel calculatorModel;

	private volatile boolean initialized;

	@NotNull
	private String themeName;

	@NotNull
	private String layoutName;

	// ids of drag buttons in R.class
	private List<Integer> dragButtonIds = null;

	// ids of buttons in R.class
	private List<Integer> buttonIds = null;

	@Nullable
	private Vibrator vibrator;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		Log.d(this.getClass().getName(), "org.solovyev.android.calculator.CalculatorActivity.onCreate()");

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		setDefaultValues(preferences);

		setTheme(preferences);
		super.onCreate(savedInstanceState);
		setLayout(preferences);

		firstTimeInit(preferences);

		vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

		calculatorModel = CalculatorModel.instance.init(this, preferences, CalculatorEngine.instance);

		dpclRegister.clear();

		final SimpleOnDragListener.Preferences dragPreferences = SimpleOnDragListener.getPreferences(preferences, this);

		setOnDragListeners(dragPreferences, preferences);

		final OnDragListener historyOnDragListener = new OnDragListenerVibrator(newOnDragListener(new HistoryDragProcessor<CalculatorHistoryState>(this.calculatorModel), dragPreferences), vibrator, preferences);
		((DragButton) findViewById(R.id.historyButton)).setOnDragListener(historyOnDragListener);

		final OnDragListener toPositionOnDragListener = new OnDragListenerVibrator(new SimpleOnDragListener(new CursorDragProcessor(calculatorModel), dragPreferences), vibrator, preferences);
		((DragButton) findViewById(R.id.rightButton)).setOnDragListener(toPositionOnDragListener);
		((DragButton) findViewById(R.id.leftButton)).setOnDragListener(toPositionOnDragListener);

		final DragButton equalsButton = (DragButton) findViewById(R.id.equalsButton);
		if (equalsButton != null) {
			final OnDragListener evalOnDragListener = new OnDragListenerVibrator(newOnDragListener(new EvalDragProcessor(calculatorModel), dragPreferences), vibrator, preferences);
			equalsButton.setOnDragListener(evalOnDragListener);
		}

		CalculatorEngine.instance.reset(this, preferences);

		preferences.registerOnSharedPreferenceChangeListener(this);
	}

	private void setDefaultValues(@NotNull SharedPreferences preferences) {
		if (!preferences.contains(CalculatorEngine.GROUPING_SEPARATOR_P_KEY)) {
			final Locale locale = Locale.getDefault();
			if (locale != null) {
				final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(locale);
				int index = MathType.grouping_separator.getTokens().indexOf(String.valueOf(decimalFormatSymbols.getGroupingSeparator()));
				final String groupingSeparator;
				if (index >= 0) {
					groupingSeparator = MathType.grouping_separator.getTokens().get(index);
				} else {
					groupingSeparator = " ";
				}
				final SharedPreferences.Editor editor = preferences.edit();
				editor.putString(CalculatorEngine.GROUPING_SEPARATOR_P_KEY, groupingSeparator);
				editor.commit();
			}
		}
	}

	private synchronized void setOnDragListeners(@NotNull SimpleOnDragListener.Preferences dragPreferences, @NotNull SharedPreferences preferences) {
		final OnDragListener onDragListener = new OnDragListenerVibrator(newOnDragListener(new DigitButtonDragProcessor(calculatorModel), dragPreferences), vibrator, preferences);

		for (Integer dragButtonId : dragButtonIds) {
			((DragButton) findViewById(dragButtonId)).setOnDragListener(onDragListener);
		}
	}

	@NotNull
	private SimpleOnDragListener newOnDragListener(@NotNull SimpleOnDragListener.DragProcessor dragProcessor,
												   @NotNull SimpleOnDragListener.Preferences dragPreferences) {
		final SimpleOnDragListener onDragListener = new SimpleOnDragListener(dragProcessor, dragPreferences);
		dpclRegister.addListener(onDragListener);
		return onDragListener;
	}

	private class OnDragListenerVibrator extends OnDragListenerWrapper {

		private static final float VIBRATION_TIME_SCALE = 0.5f;

		@NotNull
		private final VibratorContainer vibrator;

		public OnDragListenerVibrator(@NotNull OnDragListener onDragListener,
									  @Nullable Vibrator vibrator,
									  @NotNull SharedPreferences preferences) {
			super(onDragListener);
			this.vibrator = new VibratorContainer(vibrator, preferences, VIBRATION_TIME_SCALE);
		}

		@Override
		public boolean onDrag(@NotNull DragButton dragButton, @NotNull DragEvent event) {
			boolean result = super.onDrag(dragButton, event);

			if (result) {
				vibrator.vibrate();
			}

			return result;
		}
	}


	private synchronized void setLayout(@NotNull SharedPreferences preferences) {
		final Map<String, Integer> layouts = getCache(R.layout.class);

		layoutName = preferences.getString(getString(R.string.p_calc_layout_key), getString(R.string.p_calc_layout));

		Integer layoutId = layouts.get(layoutName);
		if (layoutId == null) {
			Log.d(this.getClass().getName(), "No saved layout found => applying default layout: " + R.layout.main_calculator);
			layoutId = R.layout.main_calculator;
		} else {
			Log.d(this.getClass().getName(), "Saved layout found: " + layoutId);
		}

		setContentView(layoutId);
	}

	private synchronized void setTheme(@NotNull SharedPreferences preferences) {
		final Map<String, Integer> styles = getCache(R.style.class);

		themeName = preferences.getString(getString(R.string.p_calc_theme_key), getString(R.string.p_calc_theme));

		Integer styleId = styles.get(themeName);
		if (styleId == null) {
			Log.d(this.getClass().getName(), "No saved theme found => applying default theme: " + R.style.default_theme);
			styleId = R.style.default_theme;
		} else {
			Log.d(this.getClass().getName(), "Saved theme found: " + styleId);
		}

		setTheme(styleId);
	}

	@NotNull
	private static Map<String, Integer> getCache(@NotNull Class<?> clazz) {
		Map<String, Integer> result = caches.get(clazz);

		if (result == null) {
			result = new HashMap<String, Integer>();

			for (Field field : clazz.getDeclaredFields()) {
				int modifiers = field.getModifiers();
				if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
					try {
						result.put(field.getName(), field.getInt(R.style.class));
					} catch (IllegalAccessException e) {
						Log.e(CalculatorActivity.class.getName(), e.getMessage());
					}
				}
			}

			caches.put(clazz, result);
		}

		return result;
	}

	private synchronized void firstTimeInit(@NotNull SharedPreferences preferences) {
		if (!initialized) {
			dragButtonIds = new ArrayList<Integer>();
			buttonIds = new ArrayList<Integer>();

			for (Field field : R.id.class.getDeclaredFields()) {
				int modifiers = field.getModifiers();
				if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
					try {
						int viewId = field.getInt(R.id.class);
						final View view = findViewById(viewId);
						if (view instanceof DragButton) {
							dragButtonIds.add(viewId);
						}
						if (view instanceof Button) {
							buttonIds.add(viewId);
						}
					} catch (IllegalAccessException e) {
						Log.e(CalculatorActivity.class.getName(), e.getMessage());
					}
				}
			}

			CalculatorEngine.instance.init(this, preferences);
			initialized = true;
		}
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void elementaryButtonClickHandler(@NotNull View v) {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void numericButtonClickHandler(@NotNull View v) {
		this.calculatorModel.evaluate();
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void historyButtonClickHandler(@NotNull View v) {
		this.showHistory();
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void eraseButtonClickHandler(@NotNull View v) {
		calculatorModel.doTextOperation(new CalculatorModel.TextOperation() {
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
		calculatorModel.moveCursorLeft();
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void moveRightButtonClickHandler(@NotNull View v) {
		calculatorModel.moveCursorRight();
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void pasteButtonClickHandler(@NotNull View v) {
		calculatorModel.doTextOperation(new CalculatorModel.TextOperation() {
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
	public void copyButtonClickHandler(@NotNull View v) {
		calculatorModel.copyResult(this);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void clearButtonClickHandler(@NotNull View v) {
		calculatorModel.clear();
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void digitButtonClickHandler(@NotNull View v) {
		Log.d(String.valueOf(v.getId()), "digitButtonClickHandler() for: " + v.getId() + ". Pressed: " + v.isPressed());
		calculatorModel.processDigitButtonAction(((DirectionDragButton) v).getTextMiddle());
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void functionsButtonClickHandler(@NotNull View v) {
		startActivity(new Intent(this, CalculatorFunctionsActivity.class));
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void varsButtonClickHandler(@NotNull View v) {
		startActivity(new Intent(this, CalculatorVarsActivity.class));
	}

	private final static String paypalDonateUrl = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=se%2esolovyev%40gmail%2ecom&lc=RU&item_name=Android%20Calculator&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted";

	@SuppressWarnings({"UnusedDeclaration"})
	public void donateButtonClickHandler(@NotNull View v) {
		final LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		final View view = layoutInflater.inflate(R.layout.donate, null);

		final TextView donate = (TextView) view.findViewById(R.id.donateText);
		donate.setMovementMethod(LinkMovementMethod.getInstance());

		final AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setCancelable(true)
				.setNegativeButton(R.string.c_cancel, null)
				.setPositiveButton(R.string.c_donate, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(paypalDonateUrl));
						startActivity(i);
					}
				})
				.setView(view);

		builder.create().show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			calculatorModel.doHistoryAction(HistoryAction.undo);
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
			case R.id.main_menu_item_settings:
				showSettings();
				result = true;
				break;
			case R.id.main_menu_item_history:
				showHistory();
				result = true;
				break;
			case R.id.main_menu_item_about:
				showAbout();
				result = true;
				break;
			case R.id.main_menu_item_exit:
				this.finish();
				result = true;
				break;
			default:
				result = super.onOptionsItemSelected(item);
		}

		return result;
	}

	private void showHistory() {
		startActivity(new Intent(this, CalculatorHistoryActivity.class));
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

	public void restart() {
		final Intent intent = getIntent();
		/*
		for compatibility with android_1.6_compatibility
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);*/

		Log.d(this.getClass().getName(), "Finishing current activity!");
		finish();

		/*
		for compatibility with android_1.6_compatibility

		overridePendingTransition(0, 0);*/
		Log.d(this.getClass().getName(), "Starting new activity!");
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		final String newLayoutName = preferences.getString(getString(R.string.p_calc_layout_key), getString(R.string.p_calc_layout));
		final String newThemeName = preferences.getString(getString(R.string.p_calc_theme_key), getString(R.string.p_calc_theme));
		if (!themeName.equals(newThemeName) || !layoutName.equals(newLayoutName)) {
			restart();
		}

		calculatorModel = CalculatorModel.instance.init(this, preferences, CalculatorEngine.instance);
		AndroidMessageRegistry.instance.init(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		AndroidMessageRegistry.instance.finish();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, @Nullable String key) {
		dpclRegister.announce().onDragPreferencesChange(SimpleOnDragListener.getPreferences(preferences, this));

		if (CalculatorEngine.GROUPING_SEPARATOR_P_KEY.equals(key) ||
				CalculatorEngine.ROUND_RESULT_P_KEY.equals(key) ||
					CalculatorEngine.RESULT_PRECISION_P_KEY.equals(key)) {
			CalculatorEngine.instance.reset(this, preferences);
			this.calculatorModel.evaluate();
		}

		final Boolean colorExpressionsInBracketsDefault = new BooleanMapper().parseValue(this.getString(R.string.p_calc_color_display));
		assert colorExpressionsInBracketsDefault != null;
		this.calculatorModel.getEditor().setHighlightText(preferences.getBoolean(this.getString(R.string.p_calc_color_display_key), colorExpressionsInBracketsDefault));
	}
}