package org.solovyev.android.calculator;

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
