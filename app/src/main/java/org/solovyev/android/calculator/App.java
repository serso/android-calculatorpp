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
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.squareup.otto.Bus;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.floating.FloatingCalculatorService;
import org.solovyev.android.calculator.ga.Ga;
import org.solovyev.android.calculator.language.Languages;
import org.solovyev.android.calculator.view.ScreenMetrics;
import org.solovyev.android.calculator.wizard.CalculatorWizards;
import org.solovyev.android.wizard.Wizards;
import org.solovyev.common.JPredicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class aggregates several useful in any Android application interfaces and provides access to {@link android.app.Application} object from a static context.
 * NOTE: use this class only if you don't use and dependency injection library (if you use any you can directly set interfaces through it). <br/>
 * <p/>
 * Before first usage this class must be initialized by calling {@link App#init(android.app.Application)} method (for example, from {@link android.app.Application#onCreate()})
 */
public final class App {

    public static final String TAG = "C++";

    @Nonnull
    private static Languages languages;
    @Nonnull
    private static volatile Application application;
    @Nonnull
    private static SharedPreferences preferences;
    @Nonnull
    private static volatile Ga ga;
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

    @Nonnull
    public static Wizards getWizards() {
        return wizards;
    }

    @Nonnull
    public static Ga getGa() {
        return ga;
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
        if (isFloatingCalculator(context)) {
            final SharedPreferences p = getPreferences();
            final Preferences.SimpleTheme onscreenTheme = Preferences.Onscreen.getTheme(p);
            final Preferences.Gui.Theme appTheme = Preferences.Gui.getTheme(p);
            return onscreenTheme.resolveThemeFor(appTheme).getAppTheme();
        } else {
            return App.getTheme();
        }
    }

    @NonNull
    private static Context unwrap(@NonNull Context context) {
        if (context instanceof ContextThemeWrapper) {
            return unwrap(((ContextThemeWrapper) context).getBaseContext());
        }
        return context;
    }

    @Nonnull
    public static Languages getLanguages() {
        return languages;
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

    static void addHelpInfo(@Nonnull Activity activity, @Nonnull View root) {
        if (!isMonkeyRunner(activity)) {
            return;
        }
        if (!(root instanceof ViewGroup)) {
            return;
        }
        final TextView helperTextView = new TextView(activity);

        final DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        helperTextView.setTextSize(15);
        helperTextView.setTextColor(Color.WHITE);

        final Configuration c = activity.getResources().getConfiguration();

        final StringBuilder helpText = new StringBuilder();
        helpText.append("Size: ");
        if (isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_XLARGE, c)) {
            helpText.append("xlarge");
        } else if (isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_LARGE, c)) {
            helpText.append("large");
        } else if (isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_NORMAL, c)) {
            helpText.append("normal");
        } else if (isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_SMALL, c)) {
            helpText.append("small");
        } else {
            helpText.append("unknown");
        }

        helpText.append(" (").append(dm.widthPixels).append("x").append(dm.heightPixels).append(")");

        helpText.append(" Density: ");
        switch (dm.densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                helpText.append("ldpi");
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                helpText.append("mdpi");
                break;
            case DisplayMetrics.DENSITY_HIGH:
                helpText.append("hdpi");
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                helpText.append("xhdpi");
                break;
            case DisplayMetrics.DENSITY_TV:
                helpText.append("tv");
                break;
        }

        helpText.append(" (").append(dm.densityDpi).append(")");

        helperTextView.setText(helpText);

        ((ViewGroup) root).addView(helperTextView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    static boolean isFloatingCalculator(@NonNull Context context) {
        return unwrap(context) instanceof FloatingCalculatorService;
    }

    public static int getAppVersionCode(@Nonnull Context context) {
        try {
            return getAppVersionCode(context, context.getPackageName());
        } catch (PackageManager.NameNotFoundException e) {
            Check.shouldNotHappen();
        }
        return 0;
    }

    public static int getAppVersionCode(@Nonnull Context context, @Nonnull String appPackageName) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(appPackageName, 0).versionCode;
    }
    public static boolean isUiThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static boolean isLayoutSizeAtLeast(int size, @Nonnull Configuration configuration) {
        int cur = configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if (cur == Configuration.SCREENLAYOUT_SIZE_UNDEFINED) return false;
        return cur >= size;
    }

    public static void restartActivity(@Nonnull Activity activity) {
        final Intent intent = activity.getIntent();
        activity.finish();
        activity.startActivity(intent);
    }

    /**
     * Method runs through view and all it's children recursively and process them via viewProcessor
     *
     * @param view          parent view to be processed, if view is ViewGroup then all it's children will be processed
     * @param viewProcessor object which processes views
     */
    public static void processViews(@Nonnull View view, @Nonnull ViewProcessor<View> viewProcessor) {
        processViewsOfType0(view, null, viewProcessor);
    }

    static <T> void processViewsOfType0(@Nonnull View view, @Nullable Class<T> viewClass, @Nonnull ViewProcessor<T> viewProcessor) {
        if (view instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) view;

            if (viewClass == null || viewClass.isAssignableFrom(ViewGroup.class)) {
                //noinspection unchecked
                viewProcessor.process((T) viewGroup);
            }

            for (int index = 0; index < viewGroup.getChildCount(); index++) {
                processViewsOfType0(viewGroup.getChildAt(index), viewClass, viewProcessor);
            }
        } else if (viewClass == null || viewClass.isAssignableFrom(view.getClass())) {
            //noinspection unchecked
            viewProcessor.process((T) view);
        }
    }

    public static <T> void processViewsOfType(@Nonnull View view, @Nonnull Class<T> viewClass, @Nonnull ViewProcessor<T> viewProcessor) {
        processViewsOfType0(view, viewClass, viewProcessor);
    }

    public interface ViewProcessor<V> {
        void process(@Nonnull V view);
    }

    @SuppressWarnings("deprecation")
    public static int getScreenOrientation(@Nonnull Activity activity) {
        final android.view.Display display = activity.getWindowManager().getDefaultDisplay();
        if (display.getWidth() <= display.getHeight()) {
            return Configuration.ORIENTATION_PORTRAIT;
        } else {
            return Configuration.ORIENTATION_LANDSCAPE;
        }
    }

    public static void addIntentFlags(@Nonnull Intent intent, boolean detached, @Nonnull Context context) {
        int flags = 0;
        if (!(context instanceof Activity)) {
            flags = flags | Intent.FLAG_ACTIVITY_NEW_TASK;
        }
        if (detached) {
            flags = flags | Intent.FLAG_ACTIVITY_NO_HISTORY;
        }
        intent.setFlags(flags);
    }

    public static void enableComponent(@Nonnull Context context,
                                       @Nonnull Class<?> componentClass,
                                       boolean enable) {
        final PackageManager pm = context.getPackageManager();

        final int componentState;
        if (enable) {
            componentState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        } else {
            componentState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        }

        pm.setComponentEnabledSetting(new ComponentName(context, componentClass), componentState, PackageManager.DONT_KILL_APP);
    }

    public static boolean isComponentEnabled(@Nonnull Context context,
                                             @Nonnull Class<? extends Context> componentClass) {
        final PackageManager pm = context.getPackageManager();

        int componentEnabledSetting = pm.getComponentEnabledSetting(new ComponentName(context, componentClass));
        return componentEnabledSetting == PackageManager.COMPONENT_ENABLED_STATE_ENABLED || componentEnabledSetting == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;
    }

    public static int toPixels(@Nonnull DisplayMetrics dm, float dps) {
        final float scale = dm.density;
        return (int) (dps * scale + 0.5f);
    }
}