/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 12/18/11
 * Time: 1:30 PM
 */
public interface AMenu<T extends AMenuItem> {
	@Nullable
	T itemAt(int i);

	@NotNull
	CharSequence[] getMenuCaptions();
}
