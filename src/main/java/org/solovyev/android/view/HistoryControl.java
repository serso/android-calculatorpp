/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.view;

import org.jetbrains.annotations.NotNull;
import org.solovyev.common.utils.history.HistoryAction;

/**
 * User: serso
 * Date: 9/16/11
 * Time: 11:42 PM
 */
public interface HistoryControl<T> {

	void doHistoryAction(@NotNull HistoryAction historyAction);

	void setCurrentHistoryState(@NotNull T editorHistoryState);

	@NotNull
	T getCurrentHistoryState();
}
