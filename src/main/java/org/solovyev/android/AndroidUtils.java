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
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
		if (allowCenterAndWrappingTabs()) {
			int tabCount = tabHost.getTabWidget().getTabCount();
			for (int i = 0; i < tabCount; i++) {
				final View view = tabHost.getTabWidget().getChildTabViewAt(i);
				if (view != null) {
					if (view.getLayoutParams().height > 0) {
						// reduce height of the tab
						view.getLayoutParams().height *= 0.8;
					}

					//  get title text view
					final View textView = view.findViewById(android.R.id.title);
					if (textView instanceof TextView) {
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
	}

	private static boolean allowCenterAndWrappingTabs() {
  		boolean result = true;

		String deviceModel = Build.MODEL;
		if (deviceModel != null) {
			deviceModel = deviceModel.toUpperCase();
			if (deviceModel.contains("M1") || deviceModel.contains("MIONE") || deviceModel.contains("MI-ONE")) {
				// Xiaomi Phone MiOne => do not allow to center and wrap tabs
				result = false;
				Log.i(AndroidUtils.class.getName(), "Device model doesn't support center and wrap of tabs: " + Build.MODEL);
			}
		}

		return result;
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
	 * @param context		context
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

	@NotNull
	public static AdView createAndInflateAdView(@NotNull Activity activity,
												@NotNull String admobAccountId,
												int layoutId,
												@NotNull List<String> keywords) {
		// Create the adView
		final AdView adView = new AdView(activity, AdSize.BANNER, admobAccountId);

		final ViewGroup layout = (ViewGroup) activity.findViewById(layoutId);

		// Add the adView to it
		layout.addView(adView);

		// Initiate a generic request to load it with an ad
		final AdRequest adRequest = new AdRequest();

		// todo serso: revert - only for tests
		//adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
		//adRequest.addTestDevice("DB3C2F605A1296971898F0E60224A927");

		for (String keyword : keywords) {
			adRequest.addKeyword(keyword);
		}
		adView.loadAd(adRequest);

		return adView;
	}

}

