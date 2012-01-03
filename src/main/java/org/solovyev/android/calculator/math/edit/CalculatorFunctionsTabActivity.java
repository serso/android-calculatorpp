/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.math.edit;

import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.model.AndroidFunctionsMathRegistry;

/**
 * User: serso
 * Date: 12/21/11
 * Time: 10:33 PM
 */
public class CalculatorFunctionsTabActivity extends TabActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tabs);

        final TabHost tabHost = getTabHost();

        for (AndroidFunctionsMathRegistry.Category category : AndroidFunctionsMathRegistry.Category.getCategoriesByTabOrder()) {
            AbstractMathEntityListActivity.createTab(this, tabHost, category.name(), category.name(), category.getCaptionId(), CalculatorFunctionsActivity.class, null);
        }

        AndroidUtils.centerAndWrapTabsFor(tabHost);
    }

}
