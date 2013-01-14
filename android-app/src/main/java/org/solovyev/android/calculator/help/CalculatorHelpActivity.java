/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.help;

import android.os.Bundle;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorFragmentActivity;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.R;

/**
 * User: serso
 * Date: 11/19/11
 * Time: 11:35 AM
 */
public class CalculatorHelpActivity extends CalculatorFragmentActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityHelper().addTab(this, CalculatorFragmentType.faq, null, R.id.main_layout);
        getActivityHelper().addTab(this, CalculatorFragmentType.hints, null, R.id.main_layout);
        getActivityHelper().addTab(this, CalculatorFragmentType.screens, null, R.id.main_layout);
    }
}
