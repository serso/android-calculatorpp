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
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import jscl.AngleUnit;
import jscl.NumeralBase;
import org.solovyev.android.calculator.converter.ConverterFragment;
import org.solovyev.android.calculator.databinding.ActivityMainBinding;
import org.solovyev.android.calculator.history.History;
import org.solovyev.android.calculator.keyboard.PartialKeyboardUi;
import org.solovyev.android.widget.menu.CustomPopupMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

public class CalculatorActivity extends BaseActivity implements View.OnClickListener {

    @Nonnull
    private final MainMenu mainMenu = new MainMenu();
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
    View partialKeyboard;
    FrameLayout editor;
    View mainMenuButton;
    private boolean useBackAsPrevious;

    public CalculatorActivity() {
        super(R.layout.activity_main, R.string.cpp_app_name);
    }

    @Override
    protected void bindViews(@Nonnull View contentView) {
        ActivityMainBinding binding = ActivityMainBinding.bind(contentView.findViewById(R.id.main));
        partialKeyboard = binding.partialKeyboard;
        editor = binding.editorContainer.editor;
        mainMenuButton = binding.editorContainer.mainMenu;
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
        mainMenu.onSharedPreferenceChanged(key);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.main_menu) {
            mainMenu.toggle();
        }
    }

    @Override
    protected boolean toggleMenu() {
        if (!super.toggleMenu()) {
            mainMenu.toggle();
        }
        return true;
    }

    final class MainMenu implements PopupMenu.OnMenuItemClickListener {

        @Nullable
        private CustomPopupMenu popup;

        public void toggle() {
            if (popup == null) {
                popup = new CustomPopupMenu(CalculatorActivity.this, mainMenuButton, GravityCompat.END, android.R.attr.actionOverflowMenuStyle, 0);
                popup.inflate(R.menu.main);
                popup.setOnMenuItemClickListener(this);
                popup.setKeepOnSubMenu(true);
                popup.setForceShowIcon(true);
            }
            if (popup.isShowing()) {
                popup.dismiss();
            } else {
                updateMode();
                updateAngleUnits();
                updateNumeralBase();
                popup.show();
            }
        }

        private void updateMode() {
            if (popup == null) {
                return;
            }
            final Menu menu = popup.getMenu();
            final MenuItem menuItem = menu.findItem(R.id.menu_mode);
            menuItem.setTitle(makeTitle(R.string.cpp_mode, getActivityMode().name));
        }

        @Nonnull
        private CharSequence makeTitle(@StringRes int prefix, @StringRes int suffix) {
            final String p = getString(prefix);
            final String s = getString(suffix);
            final SpannableString title = new SpannableString(p + ": " + s);
            title.setSpan(new StyleSpan(Typeface.BOLD), 0, p.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            return title;
        }

        private void updateAngleUnits() {
            if (popup == null) {
                return;
            }
            final Menu menu = popup.getMenu();
            final MenuItem menuItem = menu.findItem(R.id.menu_angle_units);
            final AngleUnit angles = Engine.Preferences.angleUnit.getPreference(preferences);
            menuItem.setTitle(makeTitle(R.string.cpp_angles, Engine.Preferences.angleUnitName(angles)));
        }

        private void updateNumeralBase() {
            if (popup == null) {
                return;
            }
            final Menu menu = popup.getMenu();
            final MenuItem menuItem = menu.findItem(R.id.menu_numeral_base);
            final NumeralBase numeralBase = Engine.Preferences.numeralBase.getPreference(preferences);
            menuItem.setTitle(makeTitle(R.string.cpp_radix, Engine.Preferences.numeralBaseName(numeralBase)));
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_settings) {
                launcher.showSettings();
                return true;
            } else if (itemId == R.id.menu_history) {
                launcher.showHistory();
                return true;
            } else if (itemId == R.id.menu_plotter) {
                launcher.showPlotter();
                return true;
            } else if (itemId == R.id.menu_conversion_tool) {
                ConverterFragment.show(CalculatorActivity.this);
                return true;
            } else if (itemId == R.id.menu_about) {
                launcher.showAbout();
                return true;
            } else if (itemId == R.id.menu_mode_engineer) {
                Preferences.Gui.mode.putPreference(preferences, Preferences.Gui.Mode.engineer);
                restartIfModeChanged();
                return true;
            } else if (itemId == R.id.menu_mode_simple) {
                Preferences.Gui.mode.putPreference(preferences, Preferences.Gui.Mode.simple);
                restartIfModeChanged();
                return true;
            } else if (itemId == R.id.menu_au_deg) {
                Engine.Preferences.angleUnit.putPreference(preferences, AngleUnit.deg);
                return true;
            } else if (itemId == R.id.menu_au_rad) {
                Engine.Preferences.angleUnit.putPreference(preferences, AngleUnit.rad);
                return true;
            } else if (itemId == R.id.menu_nb_bin) {
                Engine.Preferences.numeralBase.putPreference(preferences, NumeralBase.bin);
                return true;
            } else if (itemId == R.id.menu_nb_dec) {
                Engine.Preferences.numeralBase.putPreference(preferences, NumeralBase.dec);
                return true;
            } else if (itemId == R.id.menu_nb_hex) {
                Engine.Preferences.numeralBase.putPreference(preferences, NumeralBase.hex);
                return true;
            }
            return false;
        }

        public void onSharedPreferenceChanged(String key) {
            if (Preferences.Gui.mode.isSameKey(key)) {
                updateMode();
            } else if (Engine.Preferences.angleUnit.isSameKey(key)) {
                updateAngleUnits();
            } else if (Engine.Preferences.numeralBase.isSameKey(key)) {
                updateNumeralBase();
            }
        }
    }
}
