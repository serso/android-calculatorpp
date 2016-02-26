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

import static org.solovyev.android.calculator.App.cast;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.solovyev.android.Activities;
import org.solovyev.android.Check;
import org.solovyev.android.Views;
import org.solovyev.android.calculator.history.History;
import org.solovyev.android.calculator.language.Language;
import org.solovyev.android.calculator.language.Languages;
import org.solovyev.android.calculator.view.Tabs;

import butterknife.Bind;
import butterknife.ButterKnife;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

public class ActivityUi {

    @Nonnull
    private final AppCompatActivity activity;
    private final int layoutId;
    @Nonnull
    private final Tabs tabs;
    @Inject
    SharedPreferences preferences;
    @Inject
    Editor editor;
    @Inject
    History history;
    @Inject
    Keyboard keyboard;
    @Inject
    Calculator calculator;
    @Inject
    Typeface typeface;
    @Bind(R.id.main)
    ViewGroup mainView;
    @Nullable
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Nonnull
    private Preferences.Gui.Theme theme = Preferences.Gui.Theme.material_theme;
    @Nonnull
    private Preferences.Gui.Layout layout = Preferences.Gui.Layout.main_calculator;
    @Nonnull
    private Language language = Languages.SYSTEM_LANGUAGE;

    public ActivityUi(@Nonnull AppCompatActivity activity, @LayoutRes int layoutId) {
        this.activity = activity;
        this.layoutId = layoutId;
        tabs = new Tabs(activity);
    }

    public static boolean restartIfThemeChanged(@Nonnull Activity activity, @Nonnull Preferences.Gui.Theme oldTheme) {
        final Preferences.Gui.Theme newTheme = Preferences.Gui.theme.getPreference(App.getPreferences());
        final int themeId = oldTheme.getThemeFor(activity);
        final int newThemeId = newTheme.getThemeFor(activity);
        if (themeId != newThemeId) {
            Activities.restartActivity(activity);
            return true;
        }
        return false;
    }

    public static boolean restartIfLanguageChanged(@Nonnull Activity activity, @Nonnull Language oldLanguage) {
        final Language current = App.getLanguages().getCurrent();
        if (!current.equals(oldLanguage)) {
            Activities.restartActivity(activity);
            return true;
        }
        return false;
    }

    public static void reportActivityStop(@Nonnull Activity activity) {
        App.getGa().getAnalytics().reportActivityStop(activity);
    }

    public static void reportActivityStart(@Nonnull Activity activity) {
        App.getGa().getAnalytics().reportActivityStart(activity);
    }

    public static void setFont(@Nonnull TextView view, @Nonnull Typeface newTypeface) {
        final Typeface oldTypeface = view.getTypeface();
        if (oldTypeface == newTypeface) {
            return;
        }
        final int style = oldTypeface != null ? oldTypeface.getStyle() : Typeface.NORMAL;
        view.setTypeface(newTypeface, style);
    }

    public void onPreCreate(@Nonnull Activity activity) {
        cast(activity.getApplication()).getComponent().inject(this);

        theme = Preferences.Gui.getTheme(preferences);
        activity.setTheme(theme.getThemeFor(activity));

        layout = Preferences.Gui.getLayout(preferences);
        language = App.getLanguages().getCurrent();
    }

    public void onCreate() {
        // let's disable locking of screen for monkeyrunner
        if (App.isMonkeyRunner(activity)) {
            final KeyguardManager km = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
            //noinspection deprecation
            km.newKeyguardLock(activity.getClass().getName()).disableKeyguard();
        }

        App.getLanguages().updateContextLocale(activity, false);

        activity.setContentView(layoutId);
        ButterKnife.bind(this, activity);

        fixFonts(mainView);
        addHelpInfo(activity, mainView);
        initToolbar();
    }

    private void initToolbar() {
        if (toolbar == null) {
            return;
        }
        if (activity instanceof CalculatorActivity) {
            return;
        }
        activity.setSupportActionBar(toolbar);
        final ActionBar actionBar = activity.getSupportActionBar();
        Check.isNotNull(actionBar);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void onPause() {
        tabs.onPause();
    }

    public void onDestroy() {
    }

    @Nonnull
    public Language getLanguage() {
        return language;
    }

    @Nonnull
    public Preferences.Gui.Layout getLayout() {
        return layout;
    }

    @Nonnull
    public Preferences.Gui.Theme getTheme() {
        return theme;
    }

    public void onResume() {
        if (!restartIfThemeChanged(activity, theme)) {
            restartIfLanguageChanged(activity, language);
        }
    }

    private void addHelpInfo(@Nonnull Activity activity, @Nonnull View root) {
        if (!App.isMonkeyRunner(activity)) {
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
        if (Views.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_XLARGE, c)) {
            helpText.append("xlarge");
        } else if (Views.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_LARGE, c)) {
            helpText.append("large");
        } else if (Views.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_NORMAL, c)) {
            helpText.append("normal");
        } else if (Views.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_SMALL, c)) {
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

    public void onStop(@Nonnull Activity activity) {
        reportActivityStop(activity);
    }

    public void onStart(@Nonnull Activity activity) {
        reportActivityStart(activity);
    }

    protected void fixFonts(@Nonnull View root) {
        // some devices ship own fonts which causes issues with rendering. Let's use our own font for all text views
        Views.processViewsOfType(root, TextView.class, new Views.ViewProcessor<TextView>() {
            @Override
            public void process(@Nonnull TextView view) {
                setFont(view, typeface);
            }
        });
    }

    public void onPostCreate() {
        tabs.onCreate();
    }

    @Nonnull
    public Tabs getTabs() {
        return tabs;
    }

    public void withFab(@DrawableRes int icon, @Nonnull View.OnClickListener listener) {
        if (fab == null) {
            Check.shouldNotHappen();
            return;
        }
        fab.setVisibility(View.VISIBLE);
        fab.setImageResource(icon);
        fab.setOnClickListener(listener);
    }
}
