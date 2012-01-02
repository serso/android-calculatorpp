/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils;
import org.solovyev.android.calculator.R;

/**
 * User: serso
 * Date: 12/18/11
 * Time: 7:37 PM
 */
public class CalculatorHistoryActivity extends TabActivity {

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tabs);

		final TabHost tabHost = getTabHost();

		AndroidUtils.addTab(this, tabHost, "saved_history", R.string.c_saved_history, SavedHistoryActivityTab.class);
		AndroidUtils.addTab(this, tabHost, "history", R.string.c_history, HistoryActivityTab.class);

		tabHost.setCurrentTab(0);

        AndroidUtils.centerAndWrapTabsFor(tabHost);
	}

}
