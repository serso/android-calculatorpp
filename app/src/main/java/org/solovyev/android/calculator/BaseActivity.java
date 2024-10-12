package org.solovyev.android.calculator;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static org.solovyev.android.calculator.App.cast;
import static org.solovyev.android.calculator.Preferences.Gui.keepScreenOn;

import android.content.SharedPreferences;
import android.graphics.Insets;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.ga.Ga;
import org.solovyev.android.calculator.language.Language;
import org.solovyev.android.calculator.language.Languages;
import org.solovyev.android.calculator.view.Tabs;
import org.solovyev.android.views.dragbutton.DirectionDragImageButton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import dagger.Lazy;

public abstract class BaseActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Nonnull
    protected final Tabs tabs;
    private final int layoutId;
    private final int titleId;
    @Inject
    SharedPreferences preferences;
    @Inject
    Languages languages;
    @Inject
    Editor editor;
    @Inject
    Calculator calculator;
    @Inject
    Lazy<Ga> ga;
    @Inject
    Typeface typeface;
    ViewGroup mainView;
    @Nullable
    Toolbar toolbar;
    @Nullable
    FloatingActionButton fab;
    @Nonnull
    private Preferences.Gui.Theme theme = Preferences.Gui.Theme.material_theme;
    @Nonnull
    private Preferences.Gui.Mode mode = Preferences.Gui.Mode.engineer;
    @Nonnull
    private Language language = Languages.SYSTEM_LANGUAGE;
    private boolean paused = true;

    public BaseActivity(@StringRes int titleId) {
        this(R.layout.activity_tabs, titleId);
    }

    public BaseActivity(@LayoutRes int layoutId, @StringRes int titleId) {
        this.layoutId = layoutId;
        this.titleId = titleId;
        this.tabs = new Tabs(this);
    }

    public static void setFont(@Nonnull View view, @Nonnull Typeface newTypeface) {
        if (view instanceof TextView) {
            final TextView textView = (TextView) view;
            final Typeface oldTypeface = textView.getTypeface();
            if (oldTypeface != null && oldTypeface.equals(newTypeface)) {
                return;
            }
            final int style = oldTypeface != null ? oldTypeface.getStyle() : Typeface.NORMAL;
            textView.setTypeface(newTypeface, style);
        } else if (view instanceof DirectionDragImageButton) {
            ((DirectionDragImageButton) view).setTypeface(newTypeface);
        }
    }

    @Nonnull
    public Preferences.Gui.Mode getActivityMode() {
        return mode;
    }

    public boolean restartIfModeChanged() {
        final Preferences.Gui.Mode newMode = Preferences.Gui.mode.getPreference(preferences);
        if (newMode != mode) {
            App.restartActivity(this);
            return true;
        }
        return false;
    }

    public boolean restartIfThemeChanged() {
        final Preferences.Gui.Theme newTheme = Preferences.Gui.theme.getPreferenceNoError(preferences);
        final int themeId = theme.getThemeFor(this);
        final int newThemeId = newTheme.getThemeFor(this);
        if (themeId != newThemeId) {
            App.restartActivity(this);
            return true;
        }
        return false;
    }

    public boolean restartIfLanguageChanged() {
        final Language current = languages.getCurrent();
        if (!current.equals(language)) {
            App.restartActivity(this);
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onPreCreate();
        super.onCreate(savedInstanceState);

        inject(cast(getApplication()).getComponent());

        languages.updateContextLocale(this, false);

        createView();

        updateOrientation();
        updateKeepScreenOn();

        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void createView() {
        setContentView(layoutId);
        View contentView = findViewById(android.R.id.content);
        mainView = contentView.findViewById(R.id.main);
        toolbar = contentView.findViewById(R.id.toolbar);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            mainView.setOnApplyWindowInsetsListener((v, windowInsets) -> {
                final Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
                final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                lp.topMargin = insets.top;
                v.setLayoutParams(lp);
                return WindowInsets.CONSUMED;
            });
        }
        fab = contentView.findViewById(R.id.fab);
        bindViews(contentView);
        // title must be updated as if a non-system language is used the value from AndroidManifest
        // might be cached
        if (titleId != 0) {
            setTitle(titleId);
        }

        fixFonts(mainView, typeface);
        initToolbar();
        populateTabs(tabs);
        tabs.onCreate();
    }

    protected void bindViews(@Nonnull View contentView) {
    }

    private void initToolbar() {
        if (toolbar == null) {
            return;
        }
        if (this instanceof CalculatorActivity) {
            return;
        }
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        Check.isNotNull(actionBar);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (App.isTablet(this)) {
            final ViewGroup.LayoutParams lp = toolbar.getLayoutParams();
            if (lp instanceof AppBarLayout.LayoutParams) {
                ((AppBarLayout.LayoutParams) lp).setScrollFlags(0);
                toolbar.setLayoutParams(lp);
            }
        }
    }

    private void onPreCreate() {
        cast(getApplication()).getComponent().inject(this);

        theme = Preferences.Gui.getTheme(preferences);
        setTheme(theme.getThemeFor(this));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            final int scrimColor = theme.getScrimColorFor(this);
            EdgeToEdge.enable(this, theme.light ? SystemBarStyle.light(scrimColor, scrimColor) : SystemBarStyle.dark(scrimColor));
        }

        mode = Preferences.Gui.getMode(preferences);
        language = languages.getCurrent();
    }

    protected void populateTabs(@Nonnull Tabs tabs) {
    }

    protected void inject(@Nonnull AppComponent component) {
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
            return toggleMenu();
        }
        return super.onKeyUp(keyCode, event);
    }

    protected boolean toggleMenu() {
        if (toolbar == null) {
            return false;
        }
        if (toolbar.isOverflowMenuShowing()) {
            toolbar.hideOverflowMenu();
        } else {
            toolbar.showOverflowMenu();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        paused = false;
        if (!restartIfThemeChanged()) {
            restartIfLanguageChanged();
        }
    }

    private void updateKeepScreenOn() {
        final Window window = getWindow();
        if (window == null) {
            return;
        }
        if (keepScreenOn.getPreference(preferences)) {
            window.addFlags(FLAG_KEEP_SCREEN_ON);
        } else {
            window.clearFlags(FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    protected void onPause() {
        paused = true;
        tabs.onPause();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    public static void fixFonts(@Nonnull View view, @Nonnull Typeface typeface) {
        if (view instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) view;
            for (int index = 0; index < group.getChildCount(); index++) {
                fixFonts(group.getChildAt(index), typeface);
            }
        } else {
            setFont(view, typeface);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (Preferences.Gui.rotateScreen.isSameKey(key)) {
            updateOrientation();
        } else if (Preferences.Gui.keepScreenOn.isSameKey(key)) {
            updateKeepScreenOn();
        }

        if (paused) {
            return;
        }
        if (Preferences.Gui.theme.isSameKey(key)) {
            restartIfThemeChanged();
        } else if (Preferences.Gui.language.isSameKey(key)) {
            restartIfLanguageChanged();
        }
    }

    private void updateOrientation() {
        if (Preferences.Gui.rotateScreen.getPreference(preferences)) {
            setRequestedOrientation(SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}
