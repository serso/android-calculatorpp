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
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.floating.FloatingCalculatorService;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class App {

    public static final String TAG = "C++";
    @Nonnull
    private static Application application;
    @Nonnull
    private static SharedPreferences preferences;

    private App() {
        throw new AssertionError();
    }

    public static void init(@Nonnull CalculatorApplication application, @NonNull SharedPreferences prefs) {
        App.application = application;
        App.preferences = prefs;
    }

    @Nonnull
    public static Preferences.Gui.Theme getTheme() {
        return Preferences.Gui.getTheme(preferences);
    }

    @Nonnull
    public static Preferences.SimpleTheme getWidgetTheme() {
        return Preferences.Widget.getTheme(preferences);
    }

    @Nonnull
    public static Preferences.Gui.Theme getThemeFor(@Nonnull Context context) {
        if (isFloatingCalculator(context)) {
            final SharedPreferences p = preferences;
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
    public static SpannableString colorString(@Nonnull String s, @ColorInt int color) {
        final SpannableString spannable = new SpannableString(s);
        spannable.setSpan(new ForegroundColorSpan(color), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    @NonNull
    public static String unspan(@Nonnull CharSequence spannable) {
        return spannable.toString();
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

    public static void hideIme(@NonNull DialogFragment fragment) {
        final Dialog dialog = fragment.getDialog();
        if (dialog == null) {
            return;
        }
        final View focusedView = dialog.getCurrentFocus();
        if (focusedView == null) {
            return;
        }
        hideIme(focusedView);
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

    @Nullable
    public static <T> T find(@Nonnull View view, @Nonnull Class<T> viewClass) {
        return find0(view, viewClass);
    }

    private static <T> T find0(View view, Class<T> viewClass) {
        if (view instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) view;

            if (viewClass.isAssignableFrom(ViewGroup.class)) {
                //noinspection unchecked
                return (T) view;
            }

            for (int index = 0; index < viewGroup.getChildCount(); index++) {
                final T child = find0(viewGroup.getChildAt(index), viewClass);
                if (child != null) {
                    return child;
                }
            }
        } else if (viewClass.isAssignableFrom(view.getClass())) {
            //noinspection unchecked
            return (T) view;
        }
        return null;
    }

    @Nonnull
    public static <T> ArrayAdapter<T> makeSimpleSpinnerAdapter(@NonNull Context context) {
        return new ArrayAdapter<>(context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
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

    public static int toPixels(@Nonnull DisplayMetrics dm, float dps) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps, dm);
    }

    public static int toPixels(@Nonnull Context context, float dps) {
        return toPixels(context.getResources().getDisplayMetrics(), dps);
    }

    public static int toPixels(@Nonnull View view, float dps) {
        return toPixels(view.getContext(), dps);
    }
}
