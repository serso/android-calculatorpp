/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.view;

/**
 * User: serso
 * Date: 9/13/11
 * Time: 12:08 AM
 */
public interface CursorControl {

	public void setCursorOnStart();

	public void setCursorOnEnd();

	public void moveCursorLeft();

	public void moveCursorRight();
}
