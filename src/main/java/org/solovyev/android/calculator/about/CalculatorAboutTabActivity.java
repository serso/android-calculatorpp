/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.about;

import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils;
import org.solovyev.android.calculator.R;

/**
 * User: serso
 * Date: 9/16/11
 * Time: 11:52 PM
 */
public class CalculatorAboutTabActivity extends TabActivity {

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tabs);

		final TabHost tabHost = getTabHost();

		AndroidUtils.addTab(this, tabHost, "about", R.string.c_about, CalculatorAboutActivity.class);
		AndroidUtils.addTab(this, tabHost, "release_notes", R.string.c_release_notes, CalculatorReleaseNotesActivity.class);

		tabHost.setCurrentTab(0);
		AndroidUtils.centerAndWrapTabsFor(tabHost);
	}
}
