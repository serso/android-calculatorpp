/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 12/21/11
 * Time: 11:54 PM
 */
public final class AndroidUtils {

    // not intended for instantiation
    private AndroidUtils() {
        throw new AssertionError();
    }

    public static void centerAndWrapTabsFor(@NotNull TabHost tabHost) {
        int tabCount = tabHost.getTabWidget().getTabCount();
        for (int i = 0; i < tabCount; i++) {
            final View view = tabHost.getTabWidget().getChildTabViewAt(i);
            if ( view != null ) {
                if (view.getLayoutParams().height > 0) {
                    // reduce height of the tab
                    view.getLayoutParams().height *= 0.8;
                }

                //  get title text view
                final View textView = view.findViewById(android.R.id.title);
                if ( textView instanceof TextView) {
                    // just in case check the type

                    // center text
                    ((TextView) textView).setGravity(Gravity.CENTER);
                    // wrap text
                    ((TextView) textView).setSingleLine(false);

                    // explicitly set layout parameters
                    textView.getLayoutParams().height = ViewGroup.LayoutParams.FILL_PARENT;
                    textView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
            }
        }
    }

	public static void addTab(@NotNull Context context,
							  @NotNull TabHost tabHost,
							  @NotNull String tabId,
							  int tabCaptionId,
							  @NotNull Class<? extends Activity> activityClass) {

		TabHost.TabSpec spec;

		final Intent intent = new Intent().setClass(context, activityClass);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec(tabId).setIndicator(context.getString(tabCaptionId)).setContent(intent);

		tabHost.addTab(spec);
	}


	/**
	 * @param context context
	 * @param appPackageName - full name of the package of an app, 'com.example.app' for example.
	 * @return version number we are currently in
	 */
	public static int getAppVersionCode(@NotNull Context context, @NotNull String appPackageName) {
		try {
			return context.getPackageManager().getPackageInfo(appPackageName, 0).versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			// App not installed!
		}
		return -1;
	}

}

