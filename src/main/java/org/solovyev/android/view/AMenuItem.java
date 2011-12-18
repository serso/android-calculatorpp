/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view;

import android.content.Context;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 12/18/11
 * Time: 1:29 PM
 */
public interface AMenuItem<T> {

	@NotNull
	String getCaption();

	void doAction(@NotNull T data, @NotNull Context context);
}
