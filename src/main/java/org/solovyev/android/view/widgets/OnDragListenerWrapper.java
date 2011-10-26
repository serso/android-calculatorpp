/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view.widgets;

import org.jetbrains.annotations.NotNull;

/**
* User: serso
* Date: 10/26/11
* Time: 10:37 PM
*/
public class OnDragListenerWrapper implements OnDragListener {

	@NotNull
	private final OnDragListener onDragListener;

	public OnDragListenerWrapper(@NotNull OnDragListener onDragListener) {
		this.onDragListener = onDragListener;
	}

	@Override
	public boolean isSuppressOnClickEvent() {
		return this.onDragListener.isSuppressOnClickEvent();
	}

	@Override
	public boolean onDrag(@NotNull DragButton dragButton, @NotNull DragEvent event) {
		return this.onDragListener.onDrag(dragButton, event);
	}
}
