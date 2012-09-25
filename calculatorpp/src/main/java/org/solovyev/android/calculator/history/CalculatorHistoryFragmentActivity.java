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
import org.solovyev.android.calculator.CalculatorActivityHelper;
import org.solovyev.android.calculator.CalculatorApplication;
import org.solovyev.android.calculator.R;

/**
 * User: serso
 * Date: 12/18/11
 * Time: 7:37 PM
 */
public class CalculatorHistoryFragmentActivity extends SherlockFragmentActivity {

    @NotNull
    private final CalculatorActivityHelper activityHelper = CalculatorApplication.getInstance().createCalculatorHistoryHelper(R.layout.history_activity);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityHelper.onCreate(this, savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        activityHelper.onSaveInstanceState(this, outState);
    }
}
