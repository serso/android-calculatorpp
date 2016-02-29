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
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.FrameLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import org.solovyev.android.Activities;
import org.solovyev.android.calculator.converter.ConverterFragment;
import org.solovyev.android.calculator.history.History;
import org.solovyev.android.calculator.keyboard.PartialKeyboardUi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static org.solovyev.android.calculator.Preferences.Gui.keepScreenOn;

public class CalculatorActivity extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener, Toolbar.OnMenuItemClickListener {

    @Inject
    PreferredPreferences preferredPreferences;
    @Inject
    SharedPreferences preferences;
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
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.editor)
    FrameLayout editor;
    @Nullable
    @Bind(R.id.card)
    CardView card;
    private boolean useBackAsPrev;

    public CalculatorActivity() {
        super(R.layout.activity_main);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

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

        toolbar.inflateMenu(R.menu.main);
        toolbar.setOnMenuItemClickListener(this);

        useBackAsPrev = Preferences.Gui.useBackAsPrevious.getPreference(preferences);
        if (savedInstanceState == null) {
            startupHelper.onMainActivityOpened(this);
        }

        toggleOrientationChange();
        prepareCardAndToolbar();

        preferences.registerOnSharedPreferenceChangeListener(this);
        preferredPreferences.check(this, false);

        if (App.isMonkeyRunner(this)) {
            keyboard.buttonPressed("123");
            keyboard.buttonPressed("+");
            keyboard.buttonPressed("321");
        }
    }

    @Override
    protected void inject(@Nonnull AppComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (useBackAsPrev) {
                history.undo();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        launcher.setActivity(this);

        final Preferences.Gui.Layout newLayout = Preferences.Gui.layout.getPreference(preferences);
        if (newLayout != ui.getLayout()) {
            Activities.restartActivity(this);
        }

        final Window window = getWindow();
        if (keepScreenOn.getPreference(preferences)) {
            window.addFlags(FLAG_KEEP_SCREEN_ON);
        } else {
            window.clearFlags(FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    protected void onPause() {
        launcher.clearActivity(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        if (partialKeyboard != null) {
            partialKeyboardUi.onDestroyView();
        }
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, @Nullable String key) {
        if (Preferences.Gui.useBackAsPrevious.getKey().equals(key)) {
            useBackAsPrev = Preferences.Gui.useBackAsPrevious.getPreference(preferences);
        }

        if (Preferences.Gui.rotateScreen.getKey().equals(key)) {
            toggleOrientationChange();
        }
    }

    private void toggleOrientationChange() {
        if (Preferences.Gui.rotateScreen.getPreference(preferences)) {
            setRequestedOrientation(SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
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
        }
        return false;
    }

    private void prepareCardAndToolbar() {
        if (card == null) {
            return;
        }
        final Resources resources = getResources();
        final int cardTopMargin = resources.getDimensionPixelSize(R.dimen.cpp_card_margin);
        final int preLollipopCardTopPadding = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ? card.getPaddingTop() : 0;

        {
            final ViewGroup.LayoutParams lp = toolbar.getLayoutParams();
            lp.height += cardTopMargin + preLollipopCardTopPadding;
            toolbar.setLayoutParams(lp);
            // center icons in toolbar
            toolbar.setPadding(toolbar.getPaddingLeft(), toolbar.getPaddingTop() + cardTopMargin / 2 + preLollipopCardTopPadding, toolbar.getPaddingRight(), toolbar.getPaddingBottom() + cardTopMargin / 2);
        }
        final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
        final int actionWidth = resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) + 2 * resources.getDimensionPixelSize(R.dimen.abc_action_bar_overflow_padding_start_material);
        lp.leftMargin = actionWidth + 2 * toolbar.getPaddingLeft();
        lp.rightMargin = actionWidth + 2 * toolbar.getPaddingRight();
        card.setLayoutParams(lp);
    }
}