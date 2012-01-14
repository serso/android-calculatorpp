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
import org.solovyev.android.LastTabSaver;
import org.solovyev.android.calculator.R;

/**
 * User: serso
 * Date: 12/18/11
 * Time: 7:37 PM
 */
public class CalculatorHistoryTabActivity extends TabActivity {

	@Nullable
	private LastTabSaver lastTabSaver;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tabs);

		final TabHost tabHost = getTabHost();

		AndroidUtils.addTab(this, tabHost, "saved_history", R.string.c_saved_history, SavedHistoryActivityTab.class);
		AndroidUtils.addTab(this, tabHost, "history", R.string.c_history, HistoryActivityTab.class);

		this.lastTabSaver = new LastTabSaver(this, "saved_history");

        AndroidUtils.centerAndWrapTabsFor(tabHost);
	}

	@Override
	protected void onDestroy() {
		if ( this.lastTabSaver != null ) {
			this.lastTabSaver.destroy();
		}
		super.onDestroy();
	}
}
