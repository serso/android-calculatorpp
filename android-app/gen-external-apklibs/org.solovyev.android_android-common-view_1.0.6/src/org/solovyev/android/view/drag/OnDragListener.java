/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.view.drag;

import org.jetbrains.annotations.NotNull;

import java.util.EventListener;


public interface OnDragListener extends EventListener{
	
	/**
	 * 
	 * @return 'true': if drag event has taken place (i.e. onDrag() method returned true) then click action will be suppresed
	 */
	boolean isSuppressOnClickEvent();
	
	/**
	 * @param dragButton drag button object for which onDrag listener was set
	 * @param event drag event
	 *
	 * @return 'true' if drag event occurred, 'false' otherwise
	 */
	boolean onDrag(@NotNull DragButton dragButton, @NotNull DragEvent event);

}
