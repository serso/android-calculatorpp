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
 * Time: 1:34 PM
 */
public class EnumMenu<T extends Enum & AMenuItem<D>, D> implements AMenu<T, D> {

	@NotNull
	private final AMenu<T, D> menu;

	@NotNull
	public static <T extends Enum & AMenuItem<D>, D> AMenu<T, D> newInstance(@NotNull Class<T> enumClass) {
		return new EnumMenu<T, D>(enumClass);
	}

	private EnumMenu(Class<T> enumClass) {
		this.menu = MenuImpl.newInstance(enumClass.getEnumConstants());
	}

	@Override
	public T itemAt(int i) {
		return this.menu.itemAt(i);
	}

	@NotNull
	@Override
	public CharSequence[] getMenuCaptions(@NotNull final Context context) {
		return this.menu.getMenuCaptions(context);
	}
}
