/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import jscl.AngleUnit;
import jscl.NumeralBase;
import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.IBillingObserver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils;
import org.solovyev.android.FontSizeAdjuster;
import org.solovyev.android.LocalBinder;
import org.solovyev.android.calculator.about.CalculatorReleaseNotesActivity;
import org.solovyev.android.calculator.history.AndroidCalculatorHistoryImpl;
import org.solovyev.android.calculator.history.CalculatorHistoryState;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.calculator.view.AngleUnitsButton;
import org.solovyev.android.calculator.view.CalculatorAdditionalTitle;
import org.solovyev.android.calculator.view.NumeralBasesButton;
import org.solovyev.android.calculator.view.OnDragListenerVibrator;
import org.solovyev.android.history.HistoryDragProcessor;
import org.solovyev.android.menu.ActivityMenu;
import org.solovyev.android.menu.LayoutActivityMenu;
import org.solovyev.android.prefs.Preference;
import org.solovyev.android.view.ColorButton;
import org.solovyev.android.view.drag.*;
import org.solovyev.common.Announcer;
import org.solovyev.common.equals.EqualsTool;
import org.solovyev.common.math.Point2d;
import org.solovyev.common.text.StringUtils;
import org.solovyev.common.history.HistoryAction;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class CalculatorActivity extends Activity implements FontSizeAdjuster, SharedPreferences.OnSharedPreferenceChangeListener, ServiceConnection {

    @NotNull
    public static final String TAG = "Calculator++";

	private static final int HVGA_WIDTH_PIXELS = 320;

	@Nullable
	private IBillingObserver billingObserver;

    @Nullable
    private ICalculationService calculationService;

    @NotNull
	private final Announcer<DragPreferencesChangeListener> dpclRegister = new Announcer<DragPreferencesChangeListener>(DragPreferencesChangeListener.class);

	@NotNull
	private CalculatorModel calculatorModel;

	private volatile boolean initialized;

	@NotNull
	private CalculatorPreferences.Gui.Theme theme;

	@NotNull
	private CalculatorPreferences.Gui.Layout layout;

	@Nullable
	private Vibrator vibrator;

	private boolean useBackAsPrev;

    @NotNull
    private NumeralBaseButtons numeralBaseButtons = new NumeralBaseButtons();

    @NotNull
    private ActivityMenu menu = LayoutActivityMenu.newInstance(R.menu.main_menu, CalculatorMenu.class);

    /**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {

		CalculatorApplication.registerOnRemoteStackTrace();

		final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		CalculatorPreferences.setDefaultValues(preferences);

		setTheme(preferences);
		super.onCreate(savedInstanceState);
		setLayout(preferences);

        bindService(new Intent(this, CalculationServiceImpl.class), this, Context.BIND_AUTO_CREATE);

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

		billingObserver = new CalculatorBillingObserver(this);
		BillingController.registerObserver(billingObserver);

		firstTimeInit(preferences);

		// init billing controller
		BillingController.checkBillingSupported(this);

		vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

		AndroidCalculatorHistoryImpl.instance.load(this, preferences);
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

		final NumeralBasesButton clearButton = (NumeralBasesButton) findViewById(R.id.clearButton);
		if (clearButton != null) {
			clearButton.setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new NumeralBasesChanger(), dragPreferences), vibrator, preferences));
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

        fixThemeParameters(true);

        if (layout == CalculatorPreferences.Gui.Layout.simple) {
			toggleButtonDirectionText(R.id.oneDigitButton, false, DragDirection.up, DragDirection.down);
			toggleButtonDirectionText(R.id.twoDigitButton, false, DragDirection.up, DragDirection.down);
			toggleButtonDirectionText(R.id.threeDigitButton, false, DragDirection.up, DragDirection.down);

			toggleButtonDirectionText(R.id.sixDigitButton, false, DragDirection.up, DragDirection.down);
			toggleButtonDirectionText(R.id.sevenDigitButton, false, DragDirection.left, DragDirection.up, DragDirection.down);
			toggleButtonDirectionText(R.id.eightDigitButton, false, DragDirection.left, DragDirection.up, DragDirection.down);

			toggleButtonDirectionText(R.id.clearButton, false, DragDirection.left, DragDirection.up, DragDirection.down);

			toggleButtonDirectionText(R.id.fourDigitButton, false,  DragDirection.down);
			toggleButtonDirectionText(R.id.fiveDigitButton, false,  DragDirection.down);

			toggleButtonDirectionText(R.id.nineDigitButton, false, DragDirection.left);

			toggleButtonDirectionText(R.id.multiplicationButton, false, DragDirection.left);
			toggleButtonDirectionText(R.id.plusButton, false, DragDirection.down, DragDirection.up);
		}

        numeralBaseButtons.toggleNumericDigits(this, preferences);

        toggleOrientationChange(preferences);

        toggleEqualsButton(preferences);

        preferences.registerOnSharedPreferenceChangeListener(this);
	}

    private void fixThemeParameters(boolean fixMagicFlames) {
        if (theme.getThemeType() == CalculatorPreferences.Gui.ThemeType.metro) {

            if (fixMagicFlames) {
                // for metro themes we should turn off magic flames
                AndroidUtils.processViewsOfType(this.getWindow().getDecorView(), ColorButton.class, new AndroidUtils.ViewProcessor<ColorButton>() {
                    @Override
                    public void process(@NotNull ColorButton colorButton) {
                        colorButton.setDrawMagicFlame(false);
                    }
                });
            }

            fixMargins(2, 2);
        } else {
            fixMargins(1, 1);
        }
    }

    private void toggleButtonDirectionText(int id, boolean showDirectionText, @NotNull DragDirection... dragDirections) {
		final View v = findViewById(id);
		if (v instanceof DirectionDragButton ) {
			final DirectionDragButton button = (DirectionDragButton)v;
			for (DragDirection dragDirection : dragDirections) {
				button.showDirectionText(showDirectionText, dragDirection);
			}
		}
	}

	private void fixMargins(int marginLeft, int marginBottom) {
		// sad but true

		final View equalsButton = findViewById(R.id.equalsButton);
		final View rightButton = findViewById(R.id.rightButton);
		final View leftButton = findViewById(R.id.leftButton);
		final View clearButton = findViewById(R.id.clearButton);
		final View eraseButton = findViewById(R.id.eraseButton);

		int orientation = AndroidUtils.getScreenOrientation(this);
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			setMarginsForView(equalsButton, marginLeft, marginBottom);
			setMarginsForView(calculatorModel.getDisplay(), marginLeft, marginBottom);
		} else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setMarginsForView(leftButton, marginLeft, marginBottom);
			setMarginsForView(eraseButton, marginLeft, marginBottom);
			setMarginsForView(clearButton, marginLeft, marginBottom);
			setMarginsForView(rightButton, marginLeft, marginBottom);
			// magic magic magic
			setMarginsForView(calculatorModel.getDisplay(), 3 * marginLeft, marginBottom);
		}
	}

	private void setMarginsForView(@NotNull View view, int marginLeft, int marginBottom) {
		// IMPORTANT: this is workaround for probably android bug
		// currently margin values set in styles are not applied for some reasons to the views (using include tag) => set them manually

		final DisplayMetrics dm = getResources().getDisplayMetrics();
		if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
			final LinearLayout.LayoutParams oldParams = (LinearLayout.LayoutParams) view.getLayoutParams();
			final LinearLayout.LayoutParams newParams = new LinearLayout.LayoutParams(oldParams.width, oldParams.height, oldParams.weight);
			newParams.setMargins(AndroidUtils.toPixels(dm, marginLeft), 0, 0, AndroidUtils.toPixels(dm, marginBottom));
			view.setLayoutParams(newParams);
		}
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

							Toast.makeText(CalculatorActivity.this, CalculatorActivity.this.getString(R.string.c_angle_units_changed_to, angleUnits.name()), Toast.LENGTH_LONG).show();

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

						Toast.makeText(CalculatorActivity.this, CalculatorActivity.this.getString(R.string.c_numeral_base_changed_to, numeralBase.name()), Toast.LENGTH_LONG).show();

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

    private synchronized void setOnDragListeners(@NotNull SimpleOnDragListener.Preferences dragPreferences, @NotNull SharedPreferences preferences) {
		final OnDragListener onDragListener = new OnDragListenerVibrator(newOnDragListener(new DigitButtonDragProcessor(calculatorModel), dragPreferences), vibrator, preferences);

        final List<Integer> dragButtonIds = new ArrayList<Integer>();
        final List<Integer> buttonIds = new ArrayList<Integer>();

        for (Field field : R.id.class.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
                try {
                    int viewId = field.getInt(R.id.class);
                    final View view = this.findViewById(viewId);
                    if (view instanceof DragButton) {
                        dragButtonIds.add(viewId);
                    }
                    if (view instanceof Button) {
                        buttonIds.add(viewId);
                    }
                } catch (IllegalAccessException e) {
                    Log.e(R.id.class.getName(), e.getMessage());
                }
            }
        }

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

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder binder) {
        if (binder instanceof LocalBinder) {
            calculationService = (ICalculationService)((LocalBinder) binder).getService();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }


	private synchronized void setLayout(@NotNull SharedPreferences preferences) {
        layout = CalculatorPreferences.Gui.layout.getPreferenceNoError(preferences);

        setContentView(layout.getLayoutId());
	}

	private synchronized void setTheme(@NotNull SharedPreferences preferences) {
		theme = CalculatorPreferences.Gui.theme.getPreferenceNoError(preferences);

		setTheme(theme.getThemeId());
	}

	private synchronized void firstTimeInit(@NotNull SharedPreferences preferences) {
		if (!initialized) {
            this.useBackAsPrev = CalculatorPreferences.Gui.usePrevAsBack.getPreference(preferences);

            final Integer appOpenedCounter = CalculatorPreferences.appOpenedCounter.getPreference(preferences);
			if (appOpenedCounter != null) {
				CalculatorPreferences.appOpenedCounter.putPreference(preferences, appOpenedCounter + 1);
			}

			final Integer savedVersion = CalculatorPreferences.appVersion.getPreference(preferences);

			final int appVersion = AndroidUtils.getAppVersionCode(this, CalculatorActivity.class.getPackage().getName());

			CalculatorPreferences.appVersion.putPreference(preferences, appVersion);

			boolean dialogShown = false;
			if (EqualsTool.areEqual(savedVersion, CalculatorPreferences.appVersion.getDefaultValue())) {
				// new start
				final AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage(R.string.c_first_start_text);
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setTitle(R.string.c_first_start_text_title);
				builder.create().show();
				dialogShown = true;
			} else {
				if (savedVersion < appVersion) {
					final boolean showReleaseNotes = CalculatorPreferences.Gui.showReleaseNotes.getPreference(preferences);
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
                    dialogShown = showSpecialWindow(preferences, CalculatorPreferences.Gui.feedbackWindowShown, R.layout.feedback, R.id.feedbackText);
				}
			}

            if ( !dialogShown ) {
                dialogShown = showSpecialWindow(preferences, CalculatorPreferences.Gui.notesppAnnounceShown, R.layout.notespp_announce, R.id.notespp_announce);
            }

			initialized = true;
		}
	}

    private boolean showSpecialWindow(@NotNull SharedPreferences preferences, @NotNull Preference<Boolean> specialWindowShownPref, int layoutId, int textViewId) {
        boolean result = false;

        final Boolean specialWindowShown = specialWindowShownPref.getPreference(preferences);
        if ( specialWindowShown != null && !specialWindowShown ) {
            final LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            final View view = layoutInflater.inflate(layoutId, null);

            final TextView feedbackTextView = (TextView) view.findViewById(textViewId);
            feedbackTextView.setMovementMethod(LinkMovementMethod.getInstance());

            final AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(view);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.create().show();

            result = true;
            specialWindowShownPref.putPreference(preferences, true);
        }

        return result;
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
        if (((ColorButton) v).isShowText()) {
            calculatorModel.processDigitButtonAction(((ColorButton) v).getText().toString());
        }
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
        return this.menu.onCreateOptionsMenu(this, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return menu.onOptionsItemSelected(this, item);
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

		final CalculatorPreferences.Gui.Layout newLayout = CalculatorPreferences.Gui.layout.getPreference(preferences);
		final CalculatorPreferences.Gui.Theme newTheme = CalculatorPreferences.Gui.theme.getPreference(preferences);
		if (!theme.equals(newTheme) || !layout.equals(newLayout)) {
			AndroidUtils.restartActivity(this);
		}

		calculatorModel = CalculatorModel.instance.init(this, preferences, CalculatorEngine.instance);
		calculatorModel.evaluate(calculatorModel.getDisplay().getJsclOperation());
	}

	@Override
	protected void onDestroy() {
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

			// reevaluate in order to update values (in case of preferences changed from the main window, like numeral bases and angle units)
			this.calculatorModel.evaluate();
		}

		if ( CalculatorPreferences.Gui.usePrevAsBack.getKey().equals(key) ) {
			useBackAsPrev = CalculatorPreferences.Gui.usePrevAsBack.getPreference(preferences);
		}

        if (CalculatorEngine.Preferences.numeralBase.getKey().equals(key)) {
            numeralBaseButtons.toggleNumericDigits(this, preferences);
        }

		if ( CalculatorEngine.Preferences.multiplicationSign.getKey().equals(key) ) {
			initMultiplicationButton();
		}

        if ( CalculatorPreferences.Gui.autoOrientation.getKey().equals(key) ) {
            toggleOrientationChange(preferences);
        }

        if ( CalculatorPreferences.Gui.showEqualsButton.getKey().equals(key) ) {
            toggleEqualsButton(preferences);
        }
	}

    private void toggleEqualsButton(@Nullable SharedPreferences preferences) {
        preferences = preferences == null ? PreferenceManager.getDefaultSharedPreferences(this) : preferences;


        if (AndroidUtils.getScreenOrientation(this) == Configuration.ORIENTATION_PORTRAIT || !CalculatorPreferences.Gui.autoOrientation.getPreference(preferences)) {
            final Display display = this.getWindowManager().getDefaultDisplay();

            final DragButton button = (DragButton)findViewById(R.id.equalsButton);
            if (CalculatorPreferences.Gui.showEqualsButton.getPreference(preferences)) {
                button.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.FILL_PARENT, 1f));
                if (display.getWidth() <= 480) {
                    // mobile phones
                    calculatorModel.getDisplay().setBackgroundDrawable(null);
                }
            } else {
                button.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.FILL_PARENT, 0f));
                if (display.getWidth() <= 480) {
                    // mobile phones
                    calculatorModel.getDisplay().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.equals9));
                }
            }
            fixThemeParameters(false);
        }
    }

    private void toggleOrientationChange(@Nullable SharedPreferences preferences) {
        preferences = preferences == null ? PreferenceManager.getDefaultSharedPreferences(this) : preferences;
        if (CalculatorPreferences.Gui.autoOrientation.getPreference(preferences)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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