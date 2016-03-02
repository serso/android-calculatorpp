package org.solovyev.android.calculator;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.history.History;
import org.solovyev.android.calculator.language.Language;
import org.solovyev.android.calculator.language.Languages;
import org.solovyev.android.calculator.view.Tabs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static org.solovyev.android.calculator.App.cast;

public class BaseActivity extends AppCompatActivity {

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

    public BaseActivity(@StringRes int titleId) {
        this(R.layout.activity_tabs, titleId);
    }

    public BaseActivity(@LayoutRes int layoutId, @StringRes int titleId) {
        this.layoutId = layoutId;
        this.titleId = titleId;
        this.tabs = new Tabs(this);
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

    public boolean restartIfLayoutChanged() {
        final Preferences.Gui.Layout newLayout = Preferences.Gui.layout.getPreference(preferences);
        if (newLayout != layout) {
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

        disableKeyguard();
        languages.updateContextLocale(this, false);

        createView();
    }

    private void createView() {
        setContentView(layoutId);
        // title must be updated as if a non-system language is used the value from AndroidManifest
        // might be cached
        if (titleId != 0) {
            setTitle(titleId);
        }
        ButterKnife.bind(this, this);

        fixFonts(mainView);
        initToolbar();
        populateTabs(tabs);
        tabs.onCreate();
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
    }


    @SuppressWarnings({"ResourceType", "deprecation"})
    private void disableKeyguard() {
        if (App.isMonkeyRunner(this)) {
            final KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            km.newKeyguardLock(getClass().getName()).disableKeyguard();
        }
    }

    private void onPreCreate() {
        cast(getApplication()).getComponent().inject(this);

        theme = Preferences.Gui.getTheme(preferences);
        setTheme(theme.getThemeFor(this));

        layout = Preferences.Gui.getLayout(preferences);
        language = languages.getCurrent();
    }

    protected void populateTabs(@Nonnull Tabs tabs) {
    }

    protected void inject(@Nonnull AppComponent component) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        reportActivityStop(this);
        super.onStop();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0 && toolbar != null) {
            if (toolbar.isOverflowMenuShowing()) {
                toolbar.hideOverflowMenu();
            } else {
                toolbar.showOverflowMenu();
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!restartIfThemeChanged()) {
            restartIfLanguageChanged();
        }
    }

    @Override
    protected void onPause() {
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

    protected void fixFonts(@Nonnull View root) {
        // some devices ship own fonts which causes issues with rendering. Let's use our own font for all text views
        App.processViewsOfType(root, TextView.class, new App.ViewProcessor<TextView>() {
            @Override
            public void process(@Nonnull TextView view) {
                setFont(view, typeface);
            }
        });
    }


}
