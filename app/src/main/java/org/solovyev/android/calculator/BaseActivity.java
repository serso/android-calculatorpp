package org.solovyev.android.calculator;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import org.solovyev.android.calculator.entities.BaseEntitiesFragment;
import org.solovyev.android.calculator.entities.Category;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.solovyev.android.calculator.App.cast;

public class BaseActivity extends AppCompatActivity {

    @Nonnull
    protected final ActivityUi ui;

    public BaseActivity() {
        this(R.layout.main_empty);
    }

    public BaseActivity(@LayoutRes int layout) {
        this.ui = new ActivityUi(layout, getClass().getSimpleName());
    }

    @Nonnull
    public ActivityUi getUi() {
        return ui;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ui.onPreCreate(this);
        super.onCreate(savedInstanceState);
        inject(cast(getApplication()).getComponent());
        ui.onCreate(this);
    }

    protected void inject(@Nonnull AppComponent component) {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ui.onSaveInstanceState(this, outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ui.onStart(this);
    }

    @Override
    protected void onStop() {
        ui.onStop(this);
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (App.shouldOpenMenuManually() && keyCode == KeyEvent.KEYCODE_MENU) {
            openOptionsMenu();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ui.onResume(this);
    }

    @Override
    protected void onPause() {
        this.ui.onPause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ui.onDestroy(this);
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

    protected final void addTab(@Nonnull Category category, @Nonnull FragmentTab tab) {
        final CharSequence title = getString(category.title());
        addTab(category, tab, title);
    }

    protected final void addTab(@Nonnull Category category, @Nonnull FragmentTab tab, @Nullable CharSequence title) {
        final Bundle arguments = new Bundle(1);
        arguments.putString(BaseEntitiesFragment.ARG_CATEGORY, category.name());
        final String fragmentTag = tab.subTag(category.name());
        ui.addTab(this, fragmentTag, tab.type, arguments, title, R.id.main);
    }
}
