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

import com.squareup.leakcanary.LeakCanary;
import com.squareup.otto.Bus;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.acra.sender.HttpSender;
import org.solovyev.android.Android;
import org.solovyev.android.calculator.history.History;
import org.solovyev.android.calculator.language.Language;
import org.solovyev.android.calculator.language.Languages;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;
import org.solovyev.android.calculator.onscreen.CalculatorOnscreenStartActivity;
import org.solovyev.android.calculator.plot.AndroidCalculatorPlotter;
import org.solovyev.android.calculator.plot.CalculatorPlotterImpl;
import org.solovyev.common.msg.MessageType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

public class CalculatorApplication extends android.app.Application implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    @Named(AppModule.THREAD_INIT)
    Executor initThread;

    @Inject
    Handler handler;

    @Nonnull
    private final List<CalculatorEventListener> listeners = new ArrayList<>();

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
    Keyboard keyboard;

    @Inject
    @Named(AppModule.THREAD_UI)
    Executor uiThread;

    @Inject
    History history;

    @Inject
    Broadcaster broadcaster;

    @Override
    public void onCreate() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final Languages languages = new Languages(preferences);

        onPreCreate(preferences, languages);
        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        component.inject(this);

        super.onCreate();
        onPostCreate(preferences, languages);
    }

    private void onPostCreate(@Nonnull SharedPreferences preferences, @Nonnull Languages languages) {
        App.init(this, languages);

        preferences.registerOnSharedPreferenceChangeListener(this);
        languages.updateContextLocale(this, true);
        App.getGa().reportInitially(preferences);

        Locator.getInstance().init(calculator,
                new AndroidCalculatorEngine(this),
                new AndroidCalculatorClipboard(this),
                new AndroidCalculatorNotifier(this),
                new AndroidCalculatorLogger(),
                new AndroidCalculatorPreferenceService(this),
                keyboard,
                new AndroidCalculatorPlotter(this, new CalculatorPlotterImpl(calculator))
        );

        listeners.add(new CalculatorActivityLauncher());
        for (CalculatorEventListener listener : listeners) {
            calculator.addCalculatorEventListener(listener);
        }

        Locator.getInstance().getCalculator().init();

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
            CalculatorMathEngine mathEngine = Locator.getInstance().getEngine().getMathEngine();
            mathEngine.evaluate("1+1");
            mathEngine.evaluate("1*1");
        } catch (Throwable e) {
            Log.e(App.TAG, e.getMessage(), e);
        }
    }

    private void onPreCreate(@Nonnull SharedPreferences preferences, @Nonnull Languages languages) {
        // first we need to setup crash handler and memory leak analyzer
        if (!BuildConfig.DEBUG) {
            ACRA.init(this, new ACRAConfiguration()
                    .setFormUri("https://serso.cloudant.com/acra-cpp/_design/acra-storage/_update/report")
                    .setReportType(HttpSender.Type.JSON)
                    .setHttpMethod(HttpSender.Method.PUT)
                    .setFormUriBasicAuthLogin("timbeenterumisideffecird")
                    .setFormUriBasicAuthPassword("ECL65PO2TH5quIFNAK4hQ5Ng"));
        } else {
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
            Locator.getInstance().getNotifier().showMessage(R.string.cpp_this_change_may_require_reboot, MessageType.info);
        }
    }

    @Nonnull
    public AppComponent getComponent() {
        return component;
    }
}
