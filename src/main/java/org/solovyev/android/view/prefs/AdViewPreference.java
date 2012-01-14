/*
 * Copyright (c) 2009-2012. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view.prefs;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.google.ads.AdView;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorApplication;

/**
 * User: serso
 * Date: 1/14/12
 * Time: 6:47 PM
 */
public class AdViewPreference extends android.preference.Preference {

	@Nullable
	private AdView adView;

	public AdViewPreference(Context context) {
		super(context, null);
	}

	public AdViewPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		// this will create the linear layout defined in ads_layout.xml
		View view = super.onCreateView(parent);

		if (view instanceof ViewGroup) {
			adView = CalculatorApplication.inflateAd((Activity) getContext(), ((ViewGroup) view), 0);
		}

		return view;
	}
}