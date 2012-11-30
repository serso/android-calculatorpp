/*
 * Copyright (c) 2009-2012. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android;

import android.app.TabActivity;
import android.preference.PreferenceManager;
import android.widget.TabHost;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.prefs.StringPreference;

/**
 * User: serso
 * Date: 1/9/12
 * Time: 6:17 PM
 */

/**
 * This class saves the last opened tab in the tabHost to the SharedPreferences in order to reopen it after closing
 * The tab is defined by it's tab id and tab activity where it is hold => you can use the same tab ids in different tab activities
 */
public class LastTabSaver implements TabHost.OnTabChangeListener {

	// prefix of preference's key
	private static final String LAST_OPENED_TAB_P_KEY = "last_opened_tab_";

	// preference object
	@NotNull
	private final StringPreference<String> preference;

	// activity that holds tab host
	@NotNull
	private final TabActivity tabActivity;

	/**
	 * Constructor applies saved preference value on tabHost returned by  android.app.TabActivity#getTabHost() method
	 * and registers as onTabChangeListener
	 *
	 * @param tabActivity tab activity
	 * @param defaultTabId default tab (if no preference value is not defined)
	 */
	public LastTabSaver(@NotNull TabActivity tabActivity, @NotNull String defaultTabId) {
		this.tabActivity = tabActivity;
		this.preference = StringPreference.newInstance(getPreferenceKey(), defaultTabId);

		final TabHost tabHost = tabActivity.getTabHost();
		tabHost.setCurrentTabByTag(this.getLastOpenedTabId());
		tabHost.setOnTabChangedListener(this);
	}

	/**
	 * Method must be invoked on android.app.Activity#onDestroy() method of tab activity
	 */
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
