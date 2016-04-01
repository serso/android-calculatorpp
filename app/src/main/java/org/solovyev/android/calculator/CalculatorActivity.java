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
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import butterknife.Bind;
import org.solovyev.android.calculator.converter.ConverterFragment;
import org.solovyev.android.calculator.history.History;
import org.solovyev.android.calculator.keyboard.PartialKeyboardUi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

public class CalculatorActivity extends BaseActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    @Nonnull
    private final MainMenu mainMenu = new MainMenu(this);
    @Inject
    PreferredPreferences preferredPreferences;
    @Inject
    Keyboard keyboard;
    @Inject
    PartialKeyboardUi partialKeyboardUi;
    @Inject
    History history;
    @Inject
    ActivityLauncher launcher;
    @Inject
    StartupHelper startupHelper;
    @Nullable
    @Bind(R.id.partial_keyboard)
    View partialKeyboard;
    @Bind(R.id.editor)
    FrameLayout editor;
    @Bind(R.id.main_menu)
    ImageButton mainMenuButton;
    private boolean useBackAsPrevious;

    public CalculatorActivity() {
        super(R.layout.activity_main, R.string.c_app_name);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            final FragmentManager fm = getSupportFragmentManager();
            final FragmentTransaction t = fm.beginTransaction();
            t.add(R.id.editor, new EditorFragment(), "editor");
            t.add(R.id.display, new DisplayFragment(), "display");
            t.add(R.id.keyboard, new KeyboardFragment(), "keyboard");
            t.commit();
        }

        if (partialKeyboard != null) {
            partialKeyboardUi.onCreateView(this, partialKeyboard);
        }

        mainMenuButton.setOnClickListener(this);

        useBackAsPrevious = Preferences.Gui.useBackAsPrevious.getPreference(preferences);
        if (savedInstanceState == null) {
            startupHelper.onMainActivityOpened(this);
        }

        preferredPreferences.check(this, false);
    }

    @Override
    protected void inject(@Nonnull AppComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && useBackAsPrevious) {
            history.undo();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        launcher.setActivity(this);
        restartIfModeChanged();
    }

    @Override
    protected void onPause() {
        launcher.clearActivity(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (partialKeyboard != null) {
            partialKeyboardUi.onDestroyView();
        }
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, @Nonnull String key) {
        super.onSharedPreferenceChanged(preferences, key);
        if (Preferences.Gui.useBackAsPrevious.isSameKey(key)) {
            useBackAsPrevious = Preferences.Gui.useBackAsPrevious.getPreference(preferences);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                launcher.showSettings();
                return true;
            case R.id.menu_history:
                launcher.showHistory();
                return true;
            case R.id.menu_plotter:
                launcher.showPlotter();
                return true;
            case R.id.menu_conversion_tool:
                ConverterFragment.show(this);
                return true;
            case R.id.menu_about:
                launcher.showAbout();
                return true;
/*            case R.id.menu_mode_engineer:
                Preferences.Gui.mode.putPreference(preferences, Preferences.Gui.Mode.engineer);
                restartIfModeChanged();
                return true;
            case R.id.menu_mode_simple:
                Preferences.Gui.mode.putPreference(preferences, Preferences.Gui.Mode.simple);
                restartIfModeChanged();
                return true;*/
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_menu:
                mainMenu.toggle();
                break;
        }
    }
}