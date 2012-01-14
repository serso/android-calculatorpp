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
import org.solovyev.android.LastTabSaver;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.model.VarCategory;

/**
 * User: serso
 * Date: 12/21/11
 * Time: 11:05 PM
 */
public class CalculatorVarsTabActivity extends TabActivity {

	@Nullable
	private LastTabSaver lastTabSaver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tabs);

        final TabHost tabHost = getTabHost();

        for (VarCategory category : VarCategory.getCategoriesByTabOrder()) {
            if (category == VarCategory.my) {
                AbstractMathEntityListActivity.createTab(this, tabHost, category.name(), category.name(), category.getCaptionId(), CalculatorVarsActivity.class, getIntent());
            } else {
                AbstractMathEntityListActivity.createTab(this, tabHost, category.name(), category.name(), category.getCaptionId(), CalculatorVarsActivity.class, null);
            }
        }

		this.lastTabSaver = new LastTabSaver(this, VarCategory.my.name());

        AndroidUtils.centerAndWrapTabsFor(tabHost);
    }

	@Override
	protected void onDestroy() {
		if (lastTabSaver != null) {
			lastTabSaver.destroy();
		}
		super.onDestroy();
	}
}
