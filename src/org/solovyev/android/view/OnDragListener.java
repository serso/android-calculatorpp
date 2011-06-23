package org.solovyev.android.view;

import org.jetbrains.annotations.NotNull;


public interface OnDragListener {
	
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
