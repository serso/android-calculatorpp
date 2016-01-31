package org.solovyev.android.calculator;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import org.solovyev.android.calculator.entities.BaseEntitiesFragment;
import org.solovyev.android.calculator.entities.Category;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BaseActivity extends AppCompatActivity {

    @Nonnull
    protected final ActivityUi ui;

    public BaseActivity(@Nonnull ActivityUi ui) {
        this.ui = ui;
    }

    public BaseActivity(@LayoutRes int layout) {
        this(layout, "Activity");
    }

    public BaseActivity(@LayoutRes int layout, @Nonnull String logTag) {
        this.ui = new ActivityUi(layout, logTag);
    }

    @Nonnull
    public ActivityUi getUi() {
        return ui;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ui.onPreCreate(this);
        super.onCreate(savedInstanceState);
        inject(((CalculatorApplication) getApplication()).getComponent());
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

    protected final void addTab(@Nonnull Category category, @Nonnull CalculatorFragmentType type) {
        final CharSequence title = getString(category.title());
        addTab(category, type, title);
    }

    protected final void addTab(@Nonnull Category category, @Nonnull CalculatorFragmentType type, @Nullable CharSequence title) {
        final Bundle arguments = new Bundle(1);
        arguments.putString(BaseEntitiesFragment.ARG_CATEGORY, category.name());
        final String fragmentTag = type.createSubFragmentTag(category.name());
        final Class<? extends Fragment> fragmentClass = type.getFragmentClass();
        ui.addTab(this, fragmentTag, fragmentClass, arguments, title, R.id.main_layout);
    }
}
