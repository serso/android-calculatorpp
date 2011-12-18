/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

		setContentView(R.layout.history_tabs);

		final TabHost tabHost = getTabHost();

		createTab(tabHost, "saved_history", R.string.c_saved_history, SavedHistoryActivityTab.class);
		createTab(tabHost, "history", R.string.c_history, HistoryActivityTab.class);

		tabHost.setCurrentTab(0);
	}

	private void createTab(@NotNull TabHost tabHost,
						   @NotNull String tabId,
						   int tabCaptionId,
						   @NotNull Class<? extends Activity> activityClass) {

		TabHost.TabSpec spec;

		final Intent intent = new Intent().setClass(this, activityClass);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec(tabId).setIndicator(getString(tabCaptionId)).setContent(intent);

		tabHost.addTab(spec);
	}
}
