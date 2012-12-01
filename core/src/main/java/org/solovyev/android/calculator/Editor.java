/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 12/17/11
 * Time: 9:37 PM
 */
public interface Editor {

	@Nullable
	CharSequence getText();

	void setText(@Nullable CharSequence text);

	int  getSelection();

	void setSelection(int selection);

}
