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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TimingLogger;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.otto.Bus;
import jscl.MathEngine;
import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.acra.sender.HttpSender;
import org.solovyev.android.Android;
import org.solovyev.android.calculator.history.History;
import org.solovyev.android.calculator.language.Language;
import org.solovyev.android.calculator.language.Languages;
import org.solovyev.android.calculator.onscreen.CalculatorOnscreenStartActivity;
import org.solovyev.android.calculator.plot.AndroidCalculatorPlotter;
import org.solovyev.android.calculator.plot.CalculatorPlotterImpl;
import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Locale;
import java.util.concurrent.Executor;

public class CalculatorApplication extends android.app.Application implements SharedPreferences.OnSharedPreferenceChangeListener {

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
    Notifier notifier;

    @Inject
    PreferredPreferences preferredPreferences;

    @Inject
    ActivityLauncher launcher;

    @Nonnull
    private final TimingLogger timer = new TimingLogger("App", "onCreate");

    @Override
    public void onCreate() {
        timer.reset();

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final Languages languages = new Languages(preferences);
        timer.addSplit("languages");

        onPreCreate(preferences, languages);
        timer.addSplit("onPreCreate");

        super.onCreate();
        timer.addSplit("super.onCreate");

        initDagger();
        timer.addSplit("initDagger");

        onPostCreate(preferences, languages);
        timer.addSplit("onPostCreate");
        timer.dumpToLog();
    }

    private void initDagger() {
        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        component.inject(this);
        editor.init();
        history.init(initThread);
    }

    private void onPostCreate(@Nonnull SharedPreferences preferences, @Nonnull Languages languages) {
        App.init(this, languages);

        preferences.registerOnSharedPreferenceChangeListener(this);
        languages.updateContextLocale(this, true);
        App.getGa().reportInitially(preferences);

        Locator.getInstance().init(calculator,
                engine,
                keyboard,
                new AndroidCalculatorPlotter(this, new CalculatorPlotterImpl(calculator))
        );

        calculator.init(initThread);

        initThread.execute(new Runnable() {
            @Override
            public void run() {
                warmUpEngine();
            }
        });
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

    private void onPreCreate(@Nonnull SharedPreferences preferences, @Nonnull Languages languages) {
        // first we need to setup crash handler and memory leak analyzer
        if (AcraErrorReporter.ENABLED) {
            ACRA.init(this, new ACRAConfiguration()
                    .setFormUri("https://serso.cloudant.com/acra-cpp/_design/acra-storage/_update/report")
                    .setReportType(HttpSender.Type.JSON)
                    .setHttpMethod(HttpSender.Method.PUT)
                    .setFormUriBasicAuthLogin("timbeenterumisideffecird")
                    .setFormUriBasicAuthPassword("ECL65PO2TH5quIFNAK4hQ5Ng"));
        }
        if (BuildConfig.DEBUG) {
            LeakCanary.install(this);
        }

        // then we should set default preferences
        Preferences.setDefaultValues(preferences);

        // and change application's theme/language is needed
        final Preferences.Gui.Theme theme = Preferences.Gui.getTheme(preferences);
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
            Android.enableComponent(this, CalculatorOnscreenStartActivity.class, showAppIcon);
            notifier.showMessage(R.string.cpp_this_change_may_require_reboot, MessageType.info);
        }
    }

    @Nonnull
    public AppComponent getComponent() {
        return component;
    }
}
