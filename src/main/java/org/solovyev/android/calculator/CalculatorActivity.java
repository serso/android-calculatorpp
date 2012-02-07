/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.ads.AdView;
import jscl.AngleUnit;
import jscl.NumeralBase;
import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.IBillingObserver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils;
import org.solovyev.android.FontSizeAdjuster;
import org.solovyev.android.ResourceCache;
import org.solovyev.android.calculator.about.CalculatorReleaseNotesActivity;
import org.solovyev.android.calculator.history.CalculatorHistory;
import org.solovyev.android.calculator.history.CalculatorHistoryState;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.calculator.view.AngleUnitsButton;
import org.solovyev.android.calculator.view.NumeralBasesButton;
import org.solovyev.android.history.HistoryDragProcessor;
import org.solovyev.android.prefs.BooleanPreference;
import org.solovyev.android.prefs.IntegerPreference;
import org.solovyev.android.prefs.Preference;
import org.solovyev.android.prefs.StringPreference;
import org.solovyev.android.view.ColorButton;
import org.solovyev.android.view.VibratorContainer;
import org.solovyev.android.view.drag.*;
import org.solovyev.common.utils.Announcer;
import org.solovyev.common.utils.Point2d;
import org.solovyev.common.utils.StringUtils;
import org.solovyev.common.utils.history.HistoryAction;

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;

public class CalculatorActivity extends Activity implements FontSizeAdjuster, SharedPreferences.OnSharedPreferenceChangeListener {

	private static final int HVGA_WIDTH_PIXELS = 320;

	@Nullable
	private IBillingObserver billingObserver;

	public static enum Theme {

		default_theme(ThemeType.other, R.style.default_theme),
		violet_theme(ThemeType.other, R.style.violet_theme),
		light_blue_theme(ThemeType.other, R.style.light_blue_theme),
		metro_theme(ThemeType.metro, R.style.metro_theme);

		@NotNull
		private final ThemeType themeType;

		@NotNull
		private final Integer themeId;

		Theme(@NotNull ThemeType themeType, Integer themeId) {
			this.themeType = themeType;
			this.themeId = themeId;
		}

		@NotNull
		public ThemeType getThemeType() {
			return themeType;
		}

		@NotNull
		public Integer getThemeId() {
			return themeId;
		}
	}

	public static enum ThemeType {
		metro,
		other
	}

	public static class Preferences {
		@NotNull
		private static final String APP_VERSION_P_KEY = "application.version";
		private static final int APP_VERSION_DEFAULT = -1;
		public static final Preference<Integer> appVersion = new IntegerPreference(APP_VERSION_P_KEY, APP_VERSION_DEFAULT);

		private static final Preference<Integer> appOpenedCounter = new IntegerPreference(APP_OPENED_COUNTER_P_KEY, APP_OPENED_COUNTER_P_DEFAULT);
		private static final Preference<Boolean> feedbackWindowShown = new BooleanPreference(FEEDBACK_WINDOW_SHOWN_P_KEY, FEEDBACK_WINDOW_SHOWN_P_DEFAULT);
		private static final Preference<Theme> theme = StringPreference.newInstance(THEME_P_KEY, THEME_P_DEFAULT, Theme.class);
	}

	@NotNull
	private static final String THEME_P_KEY = "org.solovyev.android.calculator.CalculatorActivity_calc_theme";
	private static final Theme THEME_P_DEFAULT = Theme.default_theme;

	@NotNull
	private static final String APP_OPENED_COUNTER_P_KEY = "app_opened_counter";
	private static final Integer APP_OPENED_COUNTER_P_DEFAULT = 0;

	@NotNull
	public static final String FEEDBACK_WINDOW_SHOWN_P_KEY = "feedback_window_shown";
	public static final boolean FEEDBACK_WINDOW_SHOWN_P_DEFAULT = false;

	@NotNull
	public static final String SHOW_RELEASE_NOTES_P_KEY = "org.solovyev.android.calculator.CalculatorActivity_show_release_notes";
	public static final boolean SHOW_RELEASE_NOTES_P_DEFAULT = true;

	@NotNull
	public static final String USE_BACK_AS_PREV_P_KEY = "org.solovyev.android.calculator.CalculatorActivity_use_back_button_as_prev";
	public static final boolean USE_BACK_AS_PREV_DEFAULT = false;

	@NotNull
	private final Announcer<DragPreferencesChangeListener> dpclRegister = new Announcer<DragPreferencesChangeListener>(DragPreferencesChangeListener.class);

	@NotNull
	private CalculatorModel calculatorModel;

	private volatile boolean initialized;

	@NotNull
	private Theme theme;

	@NotNull
	private String layoutName;

	@Nullable
	private Vibrator vibrator;

	private boolean useBackAsPrev = USE_BACK_AS_PREV_DEFAULT;

	@Nullable
	private AdView adView;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {

		final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		setDefaultValues(preferences);

		setTheme(preferences);
		super.onCreate(savedInstanceState);
		setLayout(preferences);

		//adView = AndroidUtils.createAndInflateAdView(this, R.id.ad_parent_view, ADMOB_USER_ID);

		if (customTitleSupported) {
			try {
				getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.calc_title);
				final CalculatorAdditionalTitle additionalAdditionalTitleText = (CalculatorAdditionalTitle)findViewById(R.id.additional_title_text);
				additionalAdditionalTitleText.init(preferences);
				preferences.registerOnSharedPreferenceChangeListener(additionalAdditionalTitleText);
			} catch (ClassCastException e) {
				// super fix for issue with class cast in android.view.Window.setFeatureInt() (see app error reports)
				Log.e(CalculatorActivity.class.getName(), e.getMessage(), e);
			}
		}

		ResourceCache.instance.initCaptions(CalculatorApplication.getInstance(), R.string.class);

		billingObserver = new CalculatorBillingObserver(this);
		BillingController.registerObserver(billingObserver);

		firstTimeInit(preferences);

		// init billing controller
		BillingController.checkBillingSupported(this);

		vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

		CalculatorHistory.instance.load(this, preferences);
		calculatorModel = CalculatorModel.instance.init(this, preferences, CalculatorEngine.instance);

		dpclRegister.clear();

		final SimpleOnDragListener.Preferences dragPreferences = SimpleOnDragListener.getPreferences(preferences, this);

		setOnDragListeners(dragPreferences, preferences);

		final OnDragListener historyOnDragListener = new OnDragListenerVibrator(newOnDragListener(new HistoryDragProcessor<CalculatorHistoryState>(this.calculatorModel), dragPreferences), vibrator, preferences);
		((DragButton) findViewById(R.id.historyButton)).setOnDragListener(historyOnDragListener);

		((DragButton) findViewById(R.id.subtractionButton)).setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new SimpleOnDragListener.DragProcessor() {
			@Override
			public boolean processDragEvent(@NotNull DragDirection dragDirection, @NotNull DragButton dragButton, @NotNull Point2d startPoint2d, @NotNull MotionEvent motionEvent) {
				if (dragDirection == DragDirection.down) {
					operatorsButtonClickHandler(dragButton);
					return true;
				}
				return false;
			}
		}, dragPreferences), vibrator, preferences));


		final OnDragListener toPositionOnDragListener = new OnDragListenerVibrator(new SimpleOnDragListener(new CursorDragProcessor(calculatorModel), dragPreferences), vibrator, preferences);
		((DragButton) findViewById(R.id.rightButton)).setOnDragListener(toPositionOnDragListener);
		((DragButton) findViewById(R.id.leftButton)).setOnDragListener(toPositionOnDragListener);

		final DragButton equalsButton = (DragButton) findViewById(R.id.equalsButton);
		if (equalsButton != null) {
			equalsButton.setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new EvalDragProcessor(calculatorModel), dragPreferences), vibrator, preferences));
		}

		final AngleUnitsButton angleUnitsButton = (AngleUnitsButton) findViewById(R.id.sixDigitButton);
		if (angleUnitsButton != null) {
			angleUnitsButton.setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new AngleUnitsChanger(), dragPreferences), vibrator, preferences));
		}

		final NumeralBasesButton numeralBasesButton = (NumeralBasesButton) findViewById(R.id.clearButton);
		if (numeralBasesButton != null) {
			numeralBasesButton.setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new NumeralBasesChanger(), dragPreferences), vibrator, preferences));
		}

		final DragButton varsButton = (DragButton) findViewById(R.id.varsButton);
		if (varsButton != null) {
			varsButton.setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new VarsDragProcessor(), dragPreferences), vibrator, preferences));
		}

		final DragButton roundBracketsButton = (DragButton) findViewById(R.id.roundBracketsButton);
		if ( roundBracketsButton != null ) {
			roundBracketsButton.setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new RoundBracketsDragProcessor(), dragPreferences), vibrator, preferences));
		}


		CalculatorEngine.instance.softReset(this, preferences);

		initMultiplicationButton();

		if (theme.getThemeType() == ThemeType.metro) {
			// for metro themes we should turn off magic flames
			AndroidUtils.processViewsOfType(this.getWindow().getDecorView(), ColorButton.class, new AndroidUtils.ViewProcessor<ColorButton>() {
				@Override
				public void process(@NotNull ColorButton colorButton) {
					colorButton.setDrawMagicFlame(false);
				}
			});
		}

		preferences.registerOnSharedPreferenceChangeListener(this);
	}

	private class AngleUnitsChanger implements SimpleOnDragListener.DragProcessor {

		private final DigitButtonDragProcessor processor = new DigitButtonDragProcessor(calculatorModel);

		@Override
		public boolean processDragEvent(@NotNull DragDirection dragDirection,
										@NotNull DragButton dragButton,
										@NotNull Point2d startPoint2d,
										@NotNull MotionEvent motionEvent) {
			boolean result = false;

			if (dragButton instanceof AngleUnitsButton) {
				if (dragDirection != DragDirection.left ) {
					final String directionText = ((AngleUnitsButton) dragButton).getText(dragDirection);
					if ( directionText != null ) {
						try {

							final AngleUnit angleUnits = AngleUnit.valueOf(directionText);

							final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CalculatorActivity.this);

							CalculatorEngine.Preferences.angleUnit.putPreference(preferences, angleUnits);

							result = true;
						} catch (IllegalArgumentException e) {
							Log.d(this.getClass().getName(), "Unsupported angle units: " + directionText);
						}
					}
				} else if ( dragDirection == DragDirection.left ) {
					result = processor.processDragEvent(dragDirection, dragButton, startPoint2d, motionEvent);
				}
			}

			return result;
		}
	}

	private class NumeralBasesChanger implements SimpleOnDragListener.DragProcessor {

		@Override
		public boolean processDragEvent(@NotNull DragDirection dragDirection,
										@NotNull DragButton dragButton,
										@NotNull Point2d startPoint2d,
										@NotNull MotionEvent motionEvent) {
			boolean result = false;

			if ( dragButton instanceof NumeralBasesButton ) {
				final String directionText = ((NumeralBasesButton) dragButton).getText(dragDirection);
				if ( directionText != null ) {
					try {

						final NumeralBase numeralBase = NumeralBase.valueOf(directionText);

						final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CalculatorActivity.this);
						CalculatorEngine.Preferences.numeralBase.putPreference(preferences, numeralBase);

						result = true;
					} catch (IllegalArgumentException e) {
						Log.d(this.getClass().getName(), "Unsupported numeral base: " + directionText);
					}
				}
			}

			return result;
		}
	}


	private class VarsDragProcessor implements SimpleOnDragListener.DragProcessor {

		@Override
		public boolean processDragEvent(@NotNull DragDirection dragDirection,
										@NotNull DragButton dragButton,
										@NotNull Point2d startPoint2d,
										@NotNull MotionEvent motionEvent) {
			boolean result = false;

			if (dragDirection == DragDirection.up) {
				CalculatorActivityLauncher.createVar(CalculatorActivity.this, CalculatorActivity.this.calculatorModel);
				result = true;
			}

			return result;
		}
	}

	private void setDefaultValues(@NotNull SharedPreferences preferences) {
		if (!preferences.contains(CalculatorEngine.Preferences.groupingSeparator.getKey())) {
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

				CalculatorEngine.Preferences.groupingSeparator.putPreference(preferences, groupingSeparator);
			}
		}

		if (!preferences.contains(CalculatorEngine.Preferences.angleUnit.getKey())) {
			CalculatorEngine.Preferences.angleUnit.putDefault(preferences);
		}

		if (!preferences.contains(CalculatorEngine.Preferences.numeralBase.getKey())) {
			CalculatorEngine.Preferences.numeralBase.putDefault(preferences);
		}

	}

	private synchronized void setOnDragListeners(@NotNull SimpleOnDragListener.Preferences dragPreferences, @NotNull SharedPreferences preferences) {
		final OnDragListener onDragListener = new OnDragListenerVibrator(newOnDragListener(new DigitButtonDragProcessor(calculatorModel), dragPreferences), vibrator, preferences);

		for (Integer dragButtonId : ResourceCache.instance.getDragButtonIds()) {
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
		public boolean onDrag(@NotNull DragButton dragButton, @NotNull org.solovyev.android.view.drag.DragEvent event) {
			boolean result = super.onDrag(dragButton, event);

			if (result) {
				vibrator.vibrate();
			}

			return result;
		}
	}


	private synchronized void setLayout(@NotNull SharedPreferences preferences) {
		final Map<String, Integer> layouts = ResourceCache.instance.getNameToIdCache(R.layout.class);

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

		try {
			theme = Preferences.theme.getPreference(preferences);
		} catch (IllegalArgumentException e) {
			theme = Theme.default_theme;
		}

		setTheme(theme.getThemeId());
	}

	private synchronized void firstTimeInit(@NotNull SharedPreferences preferences) {
		if (!initialized) {
			final Integer appOpenedCounter = Preferences.appOpenedCounter.getPreference(preferences);
			if (appOpenedCounter != null) {
				Preferences.appOpenedCounter.putPreference(preferences, appOpenedCounter + 1);
			}

			final int savedVersion = Preferences.appVersion.getPreference(preferences);

			final int appVersion = AndroidUtils.getAppVersionCode(this, CalculatorActivity.class.getPackage().getName());

			Preferences.appVersion.putPreference(preferences, appVersion);

			boolean dialogShown = false;
			if (savedVersion == Preferences.APP_VERSION_DEFAULT) {
				// new start
				final AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage(R.string.c_first_start_text);
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setTitle(R.string.c_first_start_text_title);
				builder.create().show();
				dialogShown = true;
			} else {
				if (savedVersion < appVersion) {
					final boolean showReleaseNotes = preferences.getBoolean(SHOW_RELEASE_NOTES_P_KEY, SHOW_RELEASE_NOTES_P_DEFAULT);
					if (showReleaseNotes) {
						final String releaseNotes = CalculatorReleaseNotesActivity.getReleaseNotes(this, savedVersion + 1);
						if (!StringUtils.isEmpty(releaseNotes)) {
							final AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage(Html.fromHtml(releaseNotes));
							builder.setPositiveButton(android.R.string.ok, null);
							builder.setTitle(R.string.c_release_notes);
							builder.create().show();
							dialogShown = true;
						}
					}
				}
			}


			//Log.d(this.getClass().getName(), "Application was opened " + appOpenedCounter + " time!");
			if (!dialogShown) {
				if ( appOpenedCounter != null && appOpenedCounter > 10 ) {
					final Boolean feedbackWindowShown = Preferences.feedbackWindowShown.getPreference(preferences);
					if ( feedbackWindowShown != null && !feedbackWindowShown ) {
						final LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
						final View view = layoutInflater.inflate(R.layout.feedback, null);

						final TextView feedbackTextView = (TextView) view.findViewById(R.id.feedbackText);
						feedbackTextView.setMovementMethod(LinkMovementMethod.getInstance());

						final AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(view);
						builder.setPositiveButton(android.R.string.ok, null);
						builder.create().show();

						dialogShown = true;
						Preferences.feedbackWindowShown.putPreference(preferences, true);
					}
				}
			}


			ResourceCache.instance.init(R.id.class, this);

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
		CalculatorActivityLauncher.showHistory(this);
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
		calculatorModel.processDigitButtonAction(((DirectionDragButton) v).getText().toString());
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void functionsButtonClickHandler(@NotNull View v) {
		CalculatorActivityLauncher.showFunctions(this);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void operatorsButtonClickHandler(@NotNull View v) {
		CalculatorActivityLauncher.showOperators(this);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void varsButtonClickHandler(@NotNull View v) {
		CalculatorActivityLauncher.showVars(this);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void donateButtonClickHandler(@NotNull View v) {
		CalculatorApplication.showDonationDialog(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (useBackAsPrev) {
				calculatorModel.doHistoryAction(HistoryAction.undo);
				return true;
			}
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
				CalculatorActivityLauncher.showSettings(this);
				result = true;
				break;
			case R.id.main_menu_item_history:
				CalculatorActivityLauncher.showHistory(this);
				result = true;
				break;
			case R.id.main_menu_item_about:
				CalculatorActivityLauncher.showAbout(this);
				result = true;
				break;
			case R.id.main_menu_item_help:
				CalculatorActivityLauncher.showHelp(this);
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

	@Override
	protected void onResume() {
		super.onResume();

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		final String newLayoutName = preferences.getString(getString(R.string.p_calc_layout_key), getString(R.string.p_calc_layout));
		final Theme newTheme = Preferences.theme.getPreference(preferences);
		if (!theme.equals(newTheme) || !layoutName.equals(newLayoutName)) {
			AndroidUtils.restartActivity(this);
		}

		calculatorModel = CalculatorModel.instance.init(this, preferences, CalculatorEngine.instance);
		calculatorModel.evaluate(calculatorModel.getDisplay().getJsclOperation());
	}

	@Override
	protected void onDestroy() {
		if ( adView != null ) {
			adView.destroy();
		}

		if (billingObserver !=  null) {
			BillingController.unregisterObserver(billingObserver);
		}

		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, @Nullable String key) {
		if (key != null && key.startsWith("org.solovyev.android.calculator.DragButtonCalibrationActivity")) {
			dpclRegister.announce().onDragPreferencesChange(SimpleOnDragListener.getPreferences(preferences, this));
		}

		if (CalculatorEngine.Preferences.getPreferenceKeys().contains(key)) {
			CalculatorEngine.instance.softReset(this, preferences);
			this.calculatorModel.evaluate();
		}

		if ( USE_BACK_AS_PREV_P_KEY.equals(key) ) {
			useBackAsPrev = preferences.getBoolean(USE_BACK_AS_PREV_P_KEY, USE_BACK_AS_PREV_DEFAULT);
		}

		if ( CalculatorEngine.Preferences.multiplicationSign.getKey().equals(key) ) {
			initMultiplicationButton();
		}
	}

	private void initMultiplicationButton() {
		final View multiplicationButton = findViewById(R.id.multiplicationButton);
		if ( multiplicationButton instanceof Button) {
			((Button) multiplicationButton).setText(CalculatorEngine.instance.getMultiplicationSign());
		}
	}

	private static class RoundBracketsDragProcessor implements SimpleOnDragListener.DragProcessor {
		@Override
		public boolean processDragEvent(@NotNull DragDirection dragDirection, @NotNull DragButton dragButton, @NotNull Point2d startPoint2d, @NotNull MotionEvent motionEvent) {
			boolean result = false;
			if ( dragDirection == DragDirection.left ) {
				CalculatorModel.instance.doTextOperation(new CalculatorModel.TextOperation() {
					@Override
					public void doOperation(@NotNull EditText editor) {
						final int cursorPosition = editor.getSelectionStart();
						final StringBuilder text = new StringBuilder("(");
						final String oldText = editor.getText().toString();
						text.append(oldText.substring(0, cursorPosition));
						text.append(")");
						text.append(oldText.substring(cursorPosition));
						editor.setText(text);
						editor.setSelection(cursorPosition + 2);
					}
				});
				result = true;
			} else {
				result = new DigitButtonDragProcessor(CalculatorModel.instance).processDragEvent(dragDirection, dragButton, startPoint2d, motionEvent);
			}
			return result;
		}
	}
}