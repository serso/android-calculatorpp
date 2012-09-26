/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.*;

/**
 * User: serso
 * Date: 12/18/11
 * Time: 7:37 PM
 */
public class CalculatorHistoryFragmentActivity extends SherlockFragmentActivity implements CalculatorEventListener {

    @NotNull
    private final CalculatorActivityHelper activityHelper = CalculatorApplication.getInstance().createActivityHelper(R.layout.main_empty, CalculatorHistoryFragmentActivity.class.getSimpleName());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityHelper.onCreate(this, savedInstanceState);

        activityHelper.addTab(this, "history", CalculatorHistoryFragment.class, null, R.string.c_history, R.id.main_layout);
        activityHelper.addTab(this, "saved_history", CalculatorSavedHistoryFragment.class, null, R.string.c_saved_history, R.id.main_layout);

        CalculatorLocatorImpl.getInstance().getCalculator().addCalculatorEventListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        activityHelper.onSaveInstanceState(this, outState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        activityHelper.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        CalculatorLocatorImpl.getInstance().getCalculator().removeCalculatorEventListener(this);
    }

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
        if ( calculatorEventType == CalculatorEventType.use_history_state ) {
            this.finish();
        }
    }
}
