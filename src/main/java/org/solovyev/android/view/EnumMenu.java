/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 12/18/11
 * Time: 1:34 PM
 */
public class EnumMenu<T extends Enum & AMenuItem> implements AMenu<T> {

	@NotNull
	private final AMenu<T> menu;

	public EnumMenu(Class<T> enumClass) {
		this.menu = new MenuImpl<T>(enumClass.getEnumConstants());
	}

	@Override
	public T itemAt(int i) {
		return this.menu.itemAt(i);
	}

	@NotNull
	@Override
	public CharSequence[] getMenuCaptions() {
		return this.menu.getMenuCaptions();
	}
}
