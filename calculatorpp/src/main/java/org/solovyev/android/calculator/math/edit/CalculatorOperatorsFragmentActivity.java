/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.math.edit;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.history.CalculatorHistoryFragmentActivity;

/**
 * User: serso
 * Date: 12/21/11
 * Time: 10:33 PM
 */
public class CalculatorOperatorsFragmentActivity extends SherlockFragmentActivity implements CalculatorEventListener {

    @NotNull
    private final CalculatorActivityHelper activityHelper = CalculatorApplication.getInstance().createActivityHelper(R.layout.main_empty, CalculatorHistoryFragmentActivity.class.getSimpleName());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityHelper.onCreate(this, savedInstanceState);

        activityHelper.addTab(this, "operators", CalculatorOperatorsFragment.class, null, R.string.c_operators, R.id.main_layout);
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

        this.activityHelper.onDestroy(this);
    }

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
        switch (calculatorEventType) {
            case use_operator:
                this.finish();
                break;
        }
    }
}
