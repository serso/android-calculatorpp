/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.msg;

import org.jetbrains.annotations.NotNull;
import org.solovyev.common.msg.AbstractMessage;
import org.solovyev.common.msg.MessageType;

import java.util.List;

/**
 * User: serso
 * Date: 10/18/11
 * Time: 11:57 PM
 */
public class AndroidMessage extends AbstractMessage<Integer> {

	public AndroidMessage(@NotNull Integer messageCode,
							 @NotNull MessageType messageType,
							 @org.jetbrains.annotations.Nullable Object... arguments) {
		super(messageCode, messageType, arguments);
	}

	public AndroidMessage(@NotNull Integer messageCode,
							 @NotNull MessageType messageType,
							 @org.jetbrains.annotations.Nullable List<?> arguments) {
		super(messageCode, messageType, arguments);
	}
}
