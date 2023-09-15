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

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import android.util.Log;
import android.util.TimingLogger;

import com.squareup.otto.Bus;

import org.solovyev.android.calculator.floating.FloatingCalculatorActivity;
import org.solovyev.android.calculator.ga.Ga;
import org.solovyev.android.calculator.history.History;
import org.solovyev.android.calculator.language.Language;
import org.solovyev.android.calculator.language.Languages;

import java.util.Locale;
import java.util.concurrent.Executor;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import dagger.Lazy;
import jscl.MathEngine;

public class CalculatorApplication extends android.app.Application implements SharedPreferences.OnSharedPreferenceChangeListener {

    // delayed GA reporting in order to avoid initialization of GA on the main
    // application thread and to postpone it as much as possible
    private class GaInitializer extends AsyncTask<Void, Void, Ga> {
        @NonNull
        private final SharedPreferences prefs;

        GaInitializer(@NonNull SharedPreferences prefs) {
            this.prefs = prefs;
        }

        @Override
        protected Ga doInBackground(Void... params) {
            return ga.get();
        }

        @Override
        protected void onPostExecute(@NonNull Ga ga) {
            ga.reportInitially(prefs);
        }
    }

    @Inject
    @Named(AppModule.THREAD_INIT)
    Executor initThread;

    @Inject
    @Named(AppModule.THREAD_UI)
    Executor uiThread;

    @Inject
    Handler handler;

    private AppComponent component;

    @Inject
    Editor editor;

    @Inject
    Display display;

    @Inject
    Bus bus;

    @Inject
    Calculator calculator;

    @Inject
    Engine engine;

    @Inject
    Keyboard keyboard;

    @Inject
    History history;

    @Inject
    Broadcaster broadcaster;

    @Inject
    ErrorReporter errorReporter;

    @Inject
    ActivityLauncher launcher;

    @Inject
    Lazy<Ga> ga;

    @Nonnull
    private final TimingLogger timer = new TimingLogger("App", "onCreate");

    @Override
    public void onCreate() {
        timer.reset();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // start loading UI preferences as soon as possible in order for them to be loaded by the
        // time they are queried
        final SharedPreferences uiPrefs = AppModule.provideUiPreferences(this);

        final Languages languages = new Languages(this, prefs);
        timer.addSplit("languages");

        onPreCreate(prefs, uiPrefs, languages);
        timer.addSplit("onPreCreate");

        super.onCreate();
        timer.addSplit("super.onCreate");

        initDagger(languages);
        timer.addSplit("initDagger");

        onPostCreate(prefs, languages);
        timer.addSplit("onPostCreate");
        timer.dumpToLog();
    }

    private void initDagger(@NonNull Languages languages) {
        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this, languages))
                .build();
        component.inject(this);
        editor.init();
        history.init(initThread);
    }

    private void onPostCreate(@Nonnull final SharedPreferences prefs, @Nonnull Languages languages) {
        languages.init();

        prefs.registerOnSharedPreferenceChangeListener(this);
        languages.updateContextLocale(this, true);

        calculator.init(initThread);

        initThread.execute(new Runnable() {
            @Override
            public void run() {
                warmUpEngine();
            }
        });
        new GaInitializer(prefs).executeOnExecutor(initThread);
    }

    private void warmUpEngine() {
        try {
            // warm-up engine
            final MathEngine mathEngine = engine.getMathEngine();
            mathEngine.evaluate("1+1");
            mathEngine.evaluate("1*1");
        } catch (Throwable e) {
            Log.e(App.TAG, e.getMessage(), e);
        }
    }

    private void onPreCreate(@Nonnull SharedPreferences prefs, @NonNull SharedPreferences uiPrefs,
            @Nonnull Languages languages) {
        // initializing App before #onCreate as FloatingCalculatorService might be created in
        // #onCreate
        App.init(this, prefs);

        // then we should set default preferences
        Preferences.init(this, prefs);
        UiPreferences.init(prefs, uiPrefs);

        // and change application's theme/language is needed
        final Preferences.Gui.Theme theme = Preferences.Gui.getTheme(prefs);
        setTheme(theme.theme);

        final Language language = languages.getCurrent();
        if (!language.isSystem() && !language.locale.equals(Locale.getDefault())) {
            Locale.setDefault(language.locale);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (Preferences.Onscreen.showAppIcon.getKey().equals(key)) {
            boolean showAppIcon = Preferences.Onscreen.showAppIcon.getPreference(prefs);
            App.enableComponent(this, FloatingCalculatorActivity.class, showAppIcon);
        }
    }

    @Nonnull
    public AppComponent getComponent() {
        return component;
    }
}
