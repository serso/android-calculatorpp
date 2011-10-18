/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.msg;

import android.content.Context;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.msg.MessageRegistry;

import java.util.Locale;

/**
 * User: serso
 * Date: 10/18/11
 * Time: 11:49 PM
 */
public enum AndroidMessageRegistry implements MessageRegistry<AndroidMessage> {

	instance;

	private Context context;

	public void init(@NotNull Context context) {
		this.context = context;
	}

	@Override
	public void addMessage(@NotNull AndroidMessage message) {
		if (context != null) {
			Toast.makeText(context, formatMessage(message), Toast.LENGTH_SHORT).show();
		}
	}

	@NotNull
	@Override
	public AndroidMessage getMessage() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasMessage() {
		throw new UnsupportedOperationException();
	}

	@NotNull
	public String formatMessage(@NotNull AndroidMessage message) {
		final String messagePattern = context.getString(message.getMessageCode());

		final String result;
		if (messagePattern != null) {
			result = message.formatMessage(messagePattern, Locale.getDefault());
		} else {
			result = "";
		}

		return result;
	}

	public void finish() {
		this.context = null;
	}
}
