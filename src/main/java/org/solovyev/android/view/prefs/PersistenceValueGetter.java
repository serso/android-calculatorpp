/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view.prefs;

import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 9/20/11
 * Time: 10:15 PM
 */
public interface PersistenceValueGetter<T> {

	@Nullable
	T getPersistedValue(@Nullable T defaultValue);
}
