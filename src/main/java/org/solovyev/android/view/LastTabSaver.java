/*
 * Copyright (c) 2009-2012. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view;

import android.app.TabActivity;
import android.preference.PreferenceManager;
import android.widget.TabHost;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.view.prefs.StringPreference;

/**
 * User: serso
 * Date: 1/9/12
 * Time: 6:17 PM
 */
public class LastTabSaver implements TabHost.OnTabChangeListener {

	private static final String LAST_OPENED_TAB_P_KEY = "last_opened_tab_";

	@NotNull
	private final StringPreference<String> preference;

	@NotNull
	private final TabActivity tabActivity;

	public LastTabSaver(@NotNull TabActivity tabActivity, @NotNull String defaultTabId) {
		this.tabActivity = tabActivity;
		this.preference = StringPreference.newInstance(getPreferenceKey(), defaultTabId);

		final TabHost tabHost = tabActivity.getTabHost();
		tabHost.setCurrentTabByTag(this.getLastOpenedTabId());
		tabHost.setOnTabChangedListener(this);
	}

	public void destroy() {
		final TabHost tabHost = tabActivity.getTabHost();
		tabHost.setOnTabChangedListener(null);
	}

	@Override
	public void onTabChanged(String tabId) {
		preference.putPreference(PreferenceManager.getDefaultSharedPreferences(tabActivity), tabId);
	}

	@NotNull
	public String getLastOpenedTabId() {
		return preference.getPreference(PreferenceManager.getDefaultSharedPreferences(tabActivity));
	}

	@NotNull
	private String getPreferenceKey() {
		return LAST_OPENED_TAB_P_KEY + tabActivity.getClass().getName();
	}
}
