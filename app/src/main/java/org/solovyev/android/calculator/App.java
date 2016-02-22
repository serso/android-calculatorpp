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

import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.squareup.otto.Bus;

import org.solovyev.android.Check;
import org.solovyev.android.Views;
import org.solovyev.android.calculator.floating.FloatingCalculatorService;
import org.solovyev.android.calculator.ga.Ga;
import org.solovyev.android.calculator.language.Languages;
import org.solovyev.android.calculator.view.ScreenMetrics;
import org.solovyev.android.calculator.wizard.CalculatorWizards;
import org.solovyev.android.wizard.Wizards;
import org.solovyev.common.JPredicate;

import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class aggregates several useful in any Android application interfaces and provides access to {@link android.app.Application} object from a static context.
 * NOTE: use this class only if you don't use and dependency injection library (if you use any you can directly set interfaces through it). <br/>
 * <p/>
 * Before first usage this class must be initialized by calling {@link App#init(android.app.Application)} method (for example, from {@link android.app.Application#onCreate()})
 */
public final class App {

    public static final String TAG = "C++";

    @Nonnull
    public static String subTag(@Nonnull String subTag) {
        return sub(TAG, subTag);
    }

    @NonNull
    public static String sub(@Nonnull String tag, @Nonnull String subTag) {
        return tag + "/" + subTag;
    }

    @Nonnull
    private static Languages languages;
    @Nonnull
    private static volatile Application application;
    @Nonnull
    private static Executor uiThreadExecutor;
    @Nonnull
    private static SharedPreferences preferences;
    @Nonnull
    private static volatile Ga ga;
    @Nullable
    private static Boolean lg = null;
    @Nonnull
    private static volatile ScreenMetrics screenMetrics;
    @Nonnull
    private static Wizards wizards;
    @Nonnull
    private static Editor editor;
    @Nonnull
    private static Bus bus;
    @Nonnull
    private static Display display;

    private App() {
        throw new AssertionError();
    }

    public static void init(@Nonnull CalculatorApplication application,
                            @Nonnull Languages languages) {
        App.application = application;
        App.preferences = PreferenceManager.getDefaultSharedPreferences(application);
        App.uiThreadExecutor = application.uiThread;
        App.ga = new Ga(application, preferences);
        App.screenMetrics = new ScreenMetrics(application);
        App.languages = languages;
        App.languages.init();
        App.wizards = new CalculatorWizards(application);
        App.editor = application.editor;
        App.display = application.display;
        App.bus = application.bus;
    }

    /**
     * @param <A> real type of application
     * @return application instance which was provided in {@link App#init} method
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public static <A extends Application> A getApplication() {
        return (A) application;
    }

    /**
     * Method returns executor which runs on Main Application's thread. It's safe to do all UI work on this executor
     *
     * @return UI thread executor
     */
    @Nonnull
    public static Executor getUiThreadExecutor() {
        return uiThreadExecutor;
    }

    @Nonnull
    public static Wizards getWizards() {
        return wizards;
    }

    @Nonnull
    public static Ga getGa() {
        return ga;
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
    public static Preferences.Gui.Theme getThemeFor(@Nonnull Context context) {
        if (context instanceof FloatingCalculatorService) {
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
    public static SpannableString colorString(@Nonnull String s, int color) {
        final SpannableString spannable = new SpannableString(s);
        spannable.setSpan(new ForegroundColorSpan(color), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public static boolean isMonkeyRunner(@Nonnull Context context) {
        // NOTE: this code is only for monkeyrunner
        return context.checkCallingOrSelfPermission(android.Manifest.permission.DISABLE_KEYGUARD) == PackageManager.PERMISSION_GRANTED;
    }

    @NonNull
    public static String unspan(@Nonnull CharSequence spannable) {
        return spannable.toString();
    }

    @Nonnull
    public static Editor getEditor() {
        return editor;
    }

    @Nonnull
    public static Display getDisplay() {
        return display;
    }

    @Nonnull
    public static Bus getBus() {
        return bus;
    }

    private static final AtomicInteger sNextViewId = new AtomicInteger(1);

    public static int generateViewId() {
        Check.isMainThread();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return View.generateViewId();
        } else {
            // Backwards compatible version, as given by fantouchx@gmail.com in
            // http://stackoverflow.com/questions/6790623/#21000252
            while (true) {
                final int result = sNextViewId.get();
                // aapt-generated IDs have the high byte non-zero. Clamp to the
                // range below that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF) {
                    newValue = 1;
                }
                if (sNextViewId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        }
    }

    public static <T> T find(@Nullable List<T> list, @Nonnull JPredicate<T> finder) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            final T t = list.get(i);
            if (finder.apply(t)) {
                return t;
            }
        }
        return null;
    }

    public static <T> T find(@Nullable Collection<T> collection, @Nonnull JPredicate<T> finder) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        for (T t : collection) {
            if (finder.apply(t)) {
                return t;
            }
        }
        return null;
    }

    @Nullable
    public static String find(@Nonnull List<String> tokens, @Nonnull String text, int position) {
        for (int i = 0; i < tokens.size(); i++) {
            final String token = tokens.get(i);
            if (text.startsWith(token, position)) {
                return token;
            }
        }
        return null;
    }

    @NonNull
    public static CalculatorApplication cast(@NonNull Fragment fragment) {
        return cast(fragment.getActivity());
    }

    @NonNull
    public static CalculatorApplication cast(@NonNull Context context) {
        if (context instanceof CalculatorApplication) {
            return (CalculatorApplication) context;
        }
        final Context appContext = context.getApplicationContext();
        if (appContext instanceof CalculatorApplication) {
            return (CalculatorApplication) appContext;
        }
        Check.shouldNotHappen();
        return (CalculatorApplication) application;
    }

    public static void hideIme(@NonNull View view) {
        final IBinder token = view.getWindowToken();
        if (token != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(token, 0);
        }
    }

    public static void showSystemPermissionSettings(@NonNull Activity activity,
            @NonNull String action) {
        try {
            final Intent intent = new Intent(action);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG,
                    "Failed to show permission settings for " + action, e);
        }
    }

    public static boolean isTablet(@NonNull Context context) {
        return context.getResources().getBoolean(R.bool.cpp_tablet);
    }
}