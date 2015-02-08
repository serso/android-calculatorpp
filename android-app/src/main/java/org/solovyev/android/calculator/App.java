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

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import org.solovyev.android.UiThreadExecutor;
import org.solovyev.android.Views;
import org.solovyev.android.calculator.ga.Ga;
import org.solovyev.android.checkout.*;
import org.solovyev.common.listeners.JEvent;
import org.solovyev.common.listeners.JEventListener;
import org.solovyev.common.listeners.JEventListeners;
import org.solovyev.common.listeners.Listeners;
import org.solovyev.common.threads.DelayedExecutor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.concurrent.Executor;

/**
 * User: serso
 * Date: 12/1/12
 * Time: 3:58 PM
 */

/**
 * This class aggregates several useful in any Android application interfaces and provides access to {@link android.app.Application} object from a static context.
 * NOTE: use this class only if you don't use and dependency injection library (if you use any you can directly set interfaces through it). <br/>
 * <p/>
 * Before first usage this class must be initialized by calling {@link App#init(android.app.Application)} method (for example, from {@link android.app.Application#onCreate()})
 */
public final class App {

    /*
	**********************************************************************
    *
    *                           FIELDS
    *
    **********************************************************************
    */

	@Nonnull
	private static volatile Application application;

	@Nonnull
	private static volatile ServiceLocator locator;

	@Nonnull
	private static volatile DelayedExecutor uiThreadExecutor;

	@Nonnull
	private static volatile JEventListeners<JEventListener<? extends JEvent>, JEvent> eventBus;

	private static volatile boolean initialized;

	@Nonnull
	private static CalculatorBroadcaster broadcaster;

	@Nonnull
	private static SharedPreferences preferences;

	@Nonnull
	private static volatile Ga ga;

	@Nonnull
	private static volatile Billing billing;

	@Nonnull
	private static final Products products = Products.create().add(ProductTypes.IN_APP, Arrays.asList("ad_free"));

	private App() {
		throw new AssertionError();
	}

    /*
    **********************************************************************
    *
    *                           METHODS
    *
    **********************************************************************
    */

	public static <A extends Application & ServiceLocator> void init(@Nonnull A application) {
		init(application, new UiThreadExecutor(), Listeners.newEventBus(), application);
	}

	public static void init(@Nonnull Application application, @Nullable ServiceLocator serviceLocator) {
		init(application, new UiThreadExecutor(), Listeners.newEventBus(), serviceLocator);
	}

	public static void init(@Nonnull Application application,
							@Nonnull UiThreadExecutor uiThreadExecutor,
							@Nonnull JEventListeners<JEventListener<? extends JEvent>, JEvent> eventBus,
							@Nullable ServiceLocator serviceLocator) {
		if (!initialized) {
			App.application = application;
			App.preferences = PreferenceManager.getDefaultSharedPreferences(application);
			App.uiThreadExecutor = uiThreadExecutor;
			App.eventBus = eventBus;
			App.ga = new Ga(application, preferences, eventBus);
			App.billing = new Billing(application, new Billing.DefaultConfiguration() {
				@Nonnull
				@Override
				public String getPublicKey() {
					return CalculatorSecurity.getPK();
				}

				@Nullable
				@Override
				public Inventory getFallbackInventory(@Nonnull Checkout checkout, @Nonnull Executor onLoadExecutor) {
					if (RobotmediaDatabase.exists(billing.getContext())) {
						return new RobotmediaInventory(checkout, onLoadExecutor);
					} else {
						return null;
					}
				}
			});
			if (serviceLocator != null) {
				App.locator = serviceLocator;
			} else {
				// empty service locator
				App.locator = new ServiceLocator() {
				};
			}
			App.broadcaster = new CalculatorBroadcaster(application);

			App.initialized = true;
		} else {
			throw new IllegalStateException("Already initialized!");
		}
	}

	private static void checkInit() {
		if (!initialized) {
			throw new IllegalStateException("App should be initialized!");
		}
	}

	/**
	 * @return if App has already been initialized, false otherwise
	 */
	public static boolean isInitialized() {
		return initialized;
	}

	/**
	 * @param <A> real type of application
	 * @return application instance which was provided in {@link App#init(android.app.Application)} method
	 */
	@Nonnull
	public static <A extends Application> A getApplication() {
		checkInit();
		return (A) application;
	}

	/**
	 * Method returns executor which runs on Main Application's thread. It's safe to do all UI work on this executor
	 *
	 * @return UI thread executor
	 */
	@Nonnull
	public static DelayedExecutor getUiThreadExecutor() {
		checkInit();
		return uiThreadExecutor;
	}

	/**
	 * @return application's event bus
	 */
	@Nonnull
	public static JEventListeners<JEventListener<? extends JEvent>, JEvent> getEventBus() {
		checkInit();
		return eventBus;
	}

	@Nonnull
	public static CalculatorBroadcaster getBroadcaster() {
		return broadcaster;
	}

	@Nonnull
	public static Ga getGa() {
		return ga;
	}

	@Nonnull
	public static Billing getBilling() {
		return billing;
	}

	@Nonnull
	public static Products getProducts() {
		return products;
	}

	public static boolean isLargeScreen() {
		return Views.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_LARGE, App.getApplication().getResources().getConfiguration());
	}

	@Nonnull
	public static SharedPreferences getPreferences() {
		return preferences;
	}
}
