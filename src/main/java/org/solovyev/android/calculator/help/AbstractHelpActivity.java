/*
 * Copyright (c) 2009-2012. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.help;

import android.app.Activity;
import android.os.Bundle;
import com.google.ads.AdView;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 1/4/12
 * Time: 12:34 AM
 */
public class AbstractHelpActivity extends Activity {

	private final int layoutId;

	@Nullable
	private AdView adView;

	protected AbstractHelpActivity(int layoutId) {
		this.layoutId = layoutId;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(layoutId);

		// do not inflate ad in help (as some problems were encountered dut to ScrollView - no space for ad banner)
		//adView = CalculatorApplication.inflateAd(this);
	}

	@Override
	protected void onDestroy() {
		if (this.adView != null) {
			this.adView.destroy();
		}
		super.onDestroy();
	}
}
