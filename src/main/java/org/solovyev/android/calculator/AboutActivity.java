/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 9/16/11
 * Time: 11:52 PM
 */
public class AboutActivity extends Activity {

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		final TextView about = (TextView) findViewById(R.id.aboutTextView);
		about.setMovementMethod(LinkMovementMethod.getInstance());

	}
}
