/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view.widgets;

import android.view.View;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 10/26/11
 * Time: 10:55 PM
 */
public class OnClickListenerWrapper implements View.OnClickListener{

	@NotNull
	private final View.OnClickListener onClickListener;

	public OnClickListenerWrapper(@NotNull View.OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	@Override
	public void onClick(View v) {
		this.onClick(v);
	}
}
