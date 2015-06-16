/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.solovyev.android.Android;
import org.solovyev.android.calculator.history.AndroidCalculatorHistory;
import org.solovyev.android.calculator.language.Language;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;
import org.solovyev.android.calculator.onscreen.CalculatorOnscreenStartActivity;
import org.solovyev.android.calculator.plot.AndroidCalculatorPlotter;
import org.solovyev.android.calculator.plot.CalculatorPlotterImpl;
import org.solovyev.android.calculator.view.EditorTextProcessor;
import org.solovyev.android.calculator.wizard.CalculatorWizards;
import org.solovyev.android.wizard.Wizards;
import org.solovyev.common.msg.MessageType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

@ReportsCrashes(formKey = "",
		formUri = "https://serso.cloudant.com/acra-cpp/_design/acra-storage/_update/report",
		reportType = org.acra.sender.HttpSender.Type.JSON,
		httpMethod = org.acra.sender.HttpSender.Method.PUT,
		formUriBasicAuthLogin="timbeenterumisideffecird",
		formUriBasicAuthPassword="ECL65PO2TH5quIFNAK4hQ5Ng",
		mode = ReportingInteractionMode.TOAST,
		resToastText = R.string.crashed)
public class CalculatorApplication extends android.app.Application implements SharedPreferences.OnSharedPreferenceChangeListener {

	/*
	**********************************************************************
	*
	*                           CONSTANTS
	*
	**********************************************************************
	*/

	@Nonnull
	static final String MAIL = "se.solovyev@gmail.com";

	private static final String TAG = "C++";

	public static final String AD_FREE_PRODUCT_ID = "ad_free";
	public static final String AD_FREE_P_KEY = "org.solovyev.android.calculator_ad_free";
	public static final String ADMOB = "ca-app-pub-2228934497384784/2916398892";

	@Nonnull
	private static CalculatorApplication instance;

	/*
	**********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/

	@Nonnull
	private final List<CalculatorEventListener> listeners = new ArrayList<CalculatorEventListener>();

	@Nonnull
	protected final Handler uiHandler = new Handler();

	@Nonnull
	private final Wizards wizards = new CalculatorWizards(this);

	private Typeface typeFace;

	/*
	**********************************************************************
	*
	*                           CONSTRUCTORS
	*
	**********************************************************************
	*/

	public CalculatorApplication() {
		instance = this;
	}

	/*
	**********************************************************************
	*
	*                           METHODS
	*
	**********************************************************************
	*/

	@Override
	public void onCreate() {
		if (!BuildConfig.DEBUG) {
			ACRA.init(this);
		} else {
			LeakCanary.install(this);
		}

		if (!App.isInitialized()) {
			App.init(this);
		}

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		Preferences.setDefaultValues(preferences);

		preferences.registerOnSharedPreferenceChangeListener(this);

		setTheme(preferences);
		setLanguageInitially();

		super.onCreate();
		App.getLanguages().updateLanguage(this, true);

		if (!Preferences.Ga.initialReportDone.getPreference(preferences)) {
			App.getGa().reportInitially(preferences);
			Preferences.Ga.initialReportDone.putPreference(preferences, true);
		}

		final AndroidCalculator calculator = new AndroidCalculator(this);

		final EditorTextProcessor editorTextProcessor = new EditorTextProcessor();

		Locator.getInstance().init(calculator,
				new AndroidCalculatorEngine(this),
				new AndroidCalculatorClipboard(this),
				new AndroidCalculatorNotifier(this),
				new AndroidCalculatorHistory(this, calculator),
				new AndroidCalculatorLogger(),
				new AndroidCalculatorPreferenceService(this),
				new AndroidCalculatorKeyboard(this, new CalculatorKeyboardImpl(calculator)),
				new AndroidCalculatorPlotter(this, new CalculatorPlotterImpl(calculator)),
				editorTextProcessor);

		editorTextProcessor.init(this);

		listeners.add(new CalculatorActivityLauncher());
		for (CalculatorEventListener listener : listeners) {
			calculator.addCalculatorEventListener(listener);
		}

		calculator.addCalculatorEventListener(App.getBroadcaster());

		Locator.getInstance().getCalculator().init();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// prepare engine
					Locator.getInstance().getEngine().getMathEngine0().evaluate("1+1");
					Locator.getInstance().getEngine().getMathEngine0().evaluate("1*1");
				} catch (Throwable e) {
					Log.e(TAG, e.getMessage(), e);
				}

			}
		}).start();

		Locator.getInstance().getLogger().debug(TAG, "Application started!");
		Locator.getInstance().getNotifier().showDebugMessage(TAG, "Application started!");

		App.getUiThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				// we must update the widget when app starts
				App.getBroadcaster().sendEditorStateChangedIntent();
			}
		}, 100, TimeUnit.MILLISECONDS);
	}

	private void setLanguageInitially() {
		// should be called before onCreate()
		final Language language = App.getLanguages().getCurrent();
		if (!language.isSystem() && !language.locale.equals(Locale.getDefault())) {
			Locale.setDefault(language.locale);
		}
	}

	private void setTheme(@Nonnull SharedPreferences preferences) {
		final Preferences.Gui.Theme theme = Preferences.Gui.getTheme(preferences);
		setTheme(theme.getThemeId());
	}

	@Nonnull
	public FragmentUi createFragmentHelper(int layoutId) {
		return new FragmentUi(layoutId);
	}

	@Nonnull
	public FragmentUi createFragmentHelper(int layoutId, int titleResId) {
		return new FragmentUi(layoutId, titleResId);
	}

	@Nonnull
	public FragmentUi createFragmentHelper(int layoutId, int titleResId, boolean listenersOnCreate) {
		return new FragmentUi(layoutId, titleResId, listenersOnCreate);
	}

	@Nonnull
	public Handler getUiHandler() {
		return uiHandler;
	}

	@Nonnull
	public Wizards getWizards() {
		return wizards;
	}

	@Nonnull
	public Typeface getTypeFace() {
		if (typeFace == null) {
			typeFace = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
		}
		return typeFace;
	}

	/*
	**********************************************************************
	*
	*                           STATIC
	*
	**********************************************************************
	*/

	@Nonnull
	public static CalculatorApplication getInstance() {
		return instance;
	}

	@Nonnull
	public static SharedPreferences getPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(getInstance());
	}

	public static boolean isMonkeyRunner(@Nonnull Context context) {
		// NOTE: this code is only for monkeyrunner
		return context.checkCallingOrSelfPermission(android.Manifest.permission.DISABLE_KEYGUARD) == PackageManager.PERMISSION_GRANTED;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if (Preferences.Onscreen.showAppIcon.getKey().equals(key)) {
			boolean showAppIcon = Preferences.Onscreen.showAppIcon.getPreference(prefs);
			Android.toggleComponent(this, CalculatorOnscreenStartActivity.class, showAppIcon);
			Locator.getInstance().getNotifier().showMessage(R.string.cpp_this_change_may_require_reboot, MessageType.info);
		}
	}
}
