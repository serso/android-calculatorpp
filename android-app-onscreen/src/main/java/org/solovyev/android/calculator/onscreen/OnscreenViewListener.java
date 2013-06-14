package org.solovyev.android.calculator.onscreen;

/**
 * User: serso
 * Date: 11/21/12
 * Time: 9:45 PM
 */
public interface OnscreenViewListener {

	// view minimized == view is in the action bar
	void onViewMinimized();

	// view hidden == view closed
	void onViewHidden();
}
