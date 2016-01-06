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
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import org.solovyev.android.UiThreadExecutor;
import org.solovyev.android.Views;
import org.solovyev.android.calculator.ga.Ga;
import org.solovyev.android.calculator.language.Languages;
import org.solovyev.android.calculator.onscreen.CalculatorOnscreenService;
import org.solovyev.android.calculator.view.ScreenMetrics;
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

    public static final String TAG = "Calculator++";

    @Nonnull
    public static String subTag(@Nonnull String subTag) {
        return sub(TAG, subTag);
    }

    @NonNull
    public static String sub(@Nonnull String tag, @Nonnull String subTag) {
        return tag + "/" + subTag;
    }

    @Nonnull
    private static final Products products = Products.create().add(ProductTypes.IN_APP, Arrays.asList("ad_free"));
    @Nonnull
    private static final Languages languages = new Languages();
    @Nonnull
    private static volatile Application application;
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
    @Nullable
    private static Boolean lg = null;
    @Nonnull
    private static volatile Vibrator vibrator;
    @Nonnull
    private static volatile ScreenMetrics screenMetrics;

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

    public static void init(@Nonnull Application application) {
        init(application, new UiThreadExecutor(), Listeners.newEventBus());
    }

    public static void init(@Nonnull Application application,
                            @Nonnull UiThreadExecutor uiThreadExecutor,
                            @Nonnull JEventListeners<JEventListener<? extends JEvent>, JEvent> eventBus) {
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
            App.broadcaster = new CalculatorBroadcaster(application, preferences);
            App.vibrator = new Vibrator(application, preferences);
            App.screenMetrics = new ScreenMetrics(application);
            App.languages.init(App.preferences);

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
    @SuppressWarnings("unchecked")
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

    @Nonnull
    public static Preferences.Gui.Theme getTheme() {
        return Preferences.Gui.getTheme(getPreferences());
    }

    @Nonnull
    public static Preferences.SimpleTheme getWidgetTheme() {
        return Preferences.Widget.getTheme(getPreferences());
    }

    @Nonnull
    public static Preferences.Gui.Theme getThemeIn(@Nonnull Context context) {
        if (context instanceof CalculatorOnscreenService) {
            final SharedPreferences p = getPreferences();
            final Preferences.SimpleTheme onscreenTheme = Preferences.Onscreen.getTheme(p);
            final Preferences.Gui.Theme appTheme = Preferences.Gui.getTheme(p);
            return onscreenTheme.resolveThemeFor(appTheme).getAppTheme();
        } else {
            return App.getTheme();
        }
    }

    @Nonnull
    public static Languages getLanguages() {
        return languages;
    }

    public static boolean isLg() {
        if (lg == null) {
            lg = "lge".equalsIgnoreCase(Build.BRAND) || "lge".equalsIgnoreCase(Build.MANUFACTURER);
        }
        return lg;
    }

    // see https://code.google.com/p/android/issues/detail?id=78154
    // and http://developer.lge.com/community/forums/RetrieveForumContent.dev?detailContsId=FC29190703
    public static boolean shouldOpenMenuManually() {
        return isLg() && Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN;
    }

    @Nonnull
    public static Vibrator getVibrator() {
        return vibrator;
    }

    @Nonnull
    public static ScreenMetrics getScreenMetrics() {
        return screenMetrics;
    }

    public static void showDialog(@Nonnull DialogFragment dialogFragment,
                                  @Nonnull String fragmentTag,
                                  @Nonnull FragmentManager fm) {
        final FragmentTransaction ft = fm.beginTransaction();

        Fragment prev = fm.findFragmentByTag(fragmentTag);
        if (prev != null) {
            ft.remove(prev);
        }

        // Create and show the dialog.
        dialogFragment.show(ft, fragmentTag);
    }

    @NonNull
    public static String toColorString(@ColorInt int color) {
        return Integer.toHexString(color).substring(2);
    }
}
