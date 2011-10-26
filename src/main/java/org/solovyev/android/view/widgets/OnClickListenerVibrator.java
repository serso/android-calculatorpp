/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view.widgets;

import android.content.SharedPreferences;
import android.os.Vibrator;
import android.view.View;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
* User: serso
* Date: 10/26/11
* Time: 11:25 PM
*/
public class OnClickListenerVibrator implements View.OnClickListener {

	private static final long VIBRATION_TIME = 100;

	@NotNull
   	private VibratorContainer vibrator;

	public OnClickListenerVibrator(@Nullable Vibrator vibrator,
								   @NotNull SharedPreferences preferences) {
		this.vibrator = new VibratorContainer(vibrator, preferences, VIBRATION_TIME);
	}

	@Override
	public void onClick(View v) {
		vibrator.vibrate();
	}
}
