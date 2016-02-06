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
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;
import org.solovyev.android.Views;
import org.solovyev.android.calculator.history.History;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static org.solovyev.android.calculator.App.cast;

public abstract class BaseUi {

    @Nonnull
    protected Preferences.Gui.Layout layout;

    @Nonnull
    protected Preferences.Gui.Theme theme;

    protected BaseUi() {
    }

    protected BaseUi(@Nonnull String logTag) {
    }

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
    PreferredPreferences preferredPreferences;
    @Inject
    ActivityLauncher launcher;
    @Inject
    Typeface typeface;

    protected void onCreate(@Nonnull Activity activity) {
        inject(cast(activity.getApplication()).getComponent());

        layout = Preferences.Gui.layout.getPreferenceNoError(preferences);
        theme = Preferences.Gui.theme.getPreferenceNoError(preferences);

        // let's disable locking of screen for monkeyrunner
        if (App.isMonkeyRunner(activity)) {
            final KeyguardManager km = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
            //noinspection deprecation
            km.newKeyguardLock(activity.getClass().getName()).disableKeyguard();
        }
    }

    protected void inject(@Nonnull AppComponent component) {
        component.inject(this);
    }

    @Nonnull
    public Preferences.Gui.Theme getTheme() {
        return theme;
    }

    public void onDestroy(@Nonnull Activity activity) {
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

    public static void setFont(@Nonnull TextView view, @Nonnull Typeface newTypeface) {
        final Typeface oldTypeface = view.getTypeface();
        if (oldTypeface == newTypeface) {
            return;
        }
        final int style = oldTypeface != null ? oldTypeface.getStyle() : Typeface.NORMAL;
        view.setTypeface(newTypeface, style);
    }
}
