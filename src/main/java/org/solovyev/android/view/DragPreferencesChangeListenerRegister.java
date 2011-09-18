/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.view;

import org.solovyev.common.utils.Announcer;

/**
 * User: serso
 * Date: 9/18/11
 * Time: 8:53 PM
 */
public class DragPreferencesChangeListenerRegister extends Announcer<DragPreferencesChangeListener> {

	public DragPreferencesChangeListenerRegister() {
		super(DragPreferencesChangeListener.class);
	}
}
