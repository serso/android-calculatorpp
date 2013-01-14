/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.about;

import android.os.Bundle;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorFragmentActivity;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.R;

/**
 * User: serso
 * Date: 9/16/11
 * Time: 11:52 PM
 */
public class CalculatorAboutActivity extends CalculatorFragmentActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityHelper().addTab(this, CalculatorFragmentType.about, null, R.id.main_layout);
        getActivityHelper().addTab(this, CalculatorFragmentType.release_notes, null, R.id.main_layout);
    }
}
