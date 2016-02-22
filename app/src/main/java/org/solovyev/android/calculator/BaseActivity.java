package org.solovyev.android.calculator;

import static org.solovyev.android.calculator.App.cast;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;

import org.solovyev.android.calculator.view.Tabs;

import javax.annotation.Nonnull;

public class BaseActivity extends AppCompatActivity {

    @Nonnull
    protected final ActivityUi ui;

    public BaseActivity() {
        this(R.layout.activity_tabs);
    }

    public BaseActivity(@LayoutRes int layout) {
        this.ui = new ActivityUi(this, layout);
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
        ui.onCreate();
        populateTabs(ui.getTabs());
        ui.onPostCreate();
    }

    protected void populateTabs(@Nonnull Tabs tabs) {
    }

    protected void inject(@Nonnull AppComponent component) {
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
        ui.onResume();
    }

    @Override
    protected void onPause() {
        this.ui.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ui.onDestroy();
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
}
