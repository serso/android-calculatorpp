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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.helper.DefaultBillingObserver;
import net.robotmedia.billing.model.BillingDB;
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.solovyev.android.Android;
import org.solovyev.android.App;
import org.solovyev.android.ServiceLocator;
import org.solovyev.android.ads.AdsController;
import org.solovyev.android.calculator.history.AndroidCalculatorHistory;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;
import org.solovyev.android.calculator.onscreen.CalculatorOnscreenStartActivity;
import org.solovyev.android.calculator.plot.AndroidCalculatorPlotter;
import org.solovyev.android.calculator.plot.CalculatorPlotterImpl;
import org.solovyev.android.calculator.view.EditorTextProcessor;
import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 12/1/11
 * Time: 1:21 PM
 */
@ReportsCrashes(formKey = "",
		mailTo = "calculatorplusplus@gmail.com",
		mode = ReportingInteractionMode.DIALOG,
		resToastText = R.string.crashed,
		resDialogTitle = R.string.crash_dialog_title,
		resDialogText = R.string.crash_dialog_text)
public class CalculatorApplication extends android.app.Application implements SharedPreferences.OnSharedPreferenceChangeListener, ServiceLocator {

	/*
	**********************************************************************
	*
	*                           CONSTANTS
	*
	**********************************************************************
	*/

	private static final String TAG = "Calculator++ Application";

	public static final String AD_FREE_PRODUCT_ID = "ad_free";
	public static final String AD_FREE_P_KEY = "org.solovyev.android.calculator_ad_free";

	public static final String ADMOB_USER_ID = "a14f02cf9c80cbc";

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
	private final CalculatorBroadcaster broadcaster = new CalculatorBroadcaster(this);

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
		ACRA.init(this);

		App.init(this);

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		CalculatorPreferences.setDefaultValues(preferences);

		preferences.registerOnSharedPreferenceChangeListener(this);

		setTheme(preferences);

		super.onCreate();

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

		calculator.addCalculatorEventListener(broadcaster);

		Locator.getInstance().getCalculator().init();

		BillingDB.init(CalculatorApplication.this);

		AdsController.getInstance().init(ADMOB_USER_ID, AD_FREE_PRODUCT_ID, new BillingController.IConfiguration() {

			@Override
			public byte[] getObfuscationSalt() {
				return new byte[]{81, -114, 32, -127, -32, -104, -40, -15, -47, 57, -13, -41, -33, 67, -114, 7, -11, 53, 126, 82};
			}

			@Override
			public String getPublicKey() {
				return CalculatorSecurity.getPK();
			}
		});

		BillingController.registerObserver(new DefaultBillingObserver(this, null));

		// init billing controller
		new Thread(new Runnable() {
			@Override
			public void run() {
				BillingController.checkBillingSupported(CalculatorApplication.this);
				AdsController.getInstance().isAdFree(CalculatorApplication.this);

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
	}

	private void setTheme(@Nonnull SharedPreferences preferences) {
		final CalculatorPreferences.Gui.Theme theme = CalculatorPreferences.Gui.getTheme(preferences);
		setTheme(theme.getThemeId());
	}

	@Nonnull
	public CalculatorActivityHelper createActivityHelper(int layoutResId, @Nonnull String logTag) {
		return new CalculatorActivityHelperImpl(layoutResId, logTag);
	}

	@Nonnull
	public CalculatorFragmentHelper createFragmentHelper(int layoutId) {
		return new CalculatorFragmentHelperImpl(layoutId);
	}

	@Nonnull
	public CalculatorFragmentHelper createFragmentHelper(int layoutId, int titleResId) {
		return new CalculatorFragmentHelperImpl(layoutId, titleResId);
	}

	@Nonnull
	public CalculatorFragmentHelper createFragmentHelper(int layoutId, int titleResId, boolean listenersOnCreate) {
		return new CalculatorFragmentHelperImpl(layoutId, titleResId, listenersOnCreate);
	}

	@Nonnull
	public Handler getUiHandler() {
		return uiHandler;
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
		if (CalculatorPreferences.OnscreenCalculator.showAppIcon.getKey().equals(key)) {
			boolean showAppIcon = CalculatorPreferences.OnscreenCalculator.showAppIcon.getPreference(prefs);
			Android.toggleComponent(this, CalculatorOnscreenStartActivity.class, showAppIcon);
			Locator.getInstance().getNotifier().showMessage(R.string.cpp_this_change_may_require_reboot, MessageType.info);
		}
	}
}
