/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.math.edit;

import android.content.Intent;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.history.CalculatorHistoryFragment;
import org.solovyev.android.calculator.history.CalculatorHistoryFragmentActivity;
import org.solovyev.android.calculator.model.VarCategory;

/**
 * User: serso
 * Date: 12/21/11
 * Time: 11:05 PM
 */
public class CalculatorVarsFragmentActivity extends SherlockFragmentActivity implements CalculatorEventListener {

    @NotNull
    private final CalculatorActivityHelper activityHelper = CalculatorApplication.getInstance().createActivityHelper(R.layout.main_empty, CalculatorHistoryFragmentActivity.class.getSimpleName());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityHelper.onCreate(this, savedInstanceState);

        final Bundle bundle;

        final Intent intent = getIntent();
        if (intent != null) {
            bundle = intent.getExtras();
        } else {
            bundle = null;
        }

        for (VarCategory category : VarCategory.getCategoriesByTabOrder()) {
            if (category == VarCategory.my) {
                activityHelper.addTab(this, category.name(), CalculatorHistoryFragment.class, bundle, category.getCaptionId(), R.id.main_layout);
            } else {
                activityHelper.addTab(this, category.name(), CalculatorHistoryFragment.class, null, category.getCaptionId(), R.id.main_layout);
            }
        }

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
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
