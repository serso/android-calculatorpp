/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import android.content.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.view.prefs.ResourceCache;
import org.solovyev.common.exceptions.SersoException;
import org.solovyev.common.utils.CollectionsUtils;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: serso
 * Date: 10/6/11
 * Time: 9:25 PM
 */
public class ParseException extends SersoException {

	@NotNull
	private final String messageId;

	@NotNull
	private final List<Object> parameters;

	@NotNull
	private final String expression;

	@Nullable
	private final Integer position;

	public ParseException(@NotNull jscl.text.ParseException jsclParseException) {
		this.messageId = "jscl_" + jsclParseException.getMessageId();
		this.expression = jsclParseException.getExpression();
		this.position = jsclParseException.getPosition();
		this.parameters = jsclParseException.getParameters();
	}

	public ParseException(@NotNull String messageId, @Nullable Integer position, @NotNull String expression, Object... parameters) {
		this.messageId = messageId;
		this.expression = expression;
		this.position = position;

		if (CollectionsUtils.isEmpty(parameters)) {
			this.parameters = Collections.emptyList();
		} else {
			this.parameters = Arrays.asList(parameters);
		}
	}

	public ParseException(@NotNull String messageId, @NotNull String expression, Object... parameters) {
		this(messageId, null, expression, parameters);
	}

	@NotNull
	public String getMessageId() {
		return messageId;
	}

	@NotNull
	public List<Object> getParameters() {
		return parameters;
	}

	@NotNull
	public String getExpression() {
		return expression;
	}

	@Nullable
	public Integer getPosition() {
		return position;
	}

	@Override
	@Nullable
	public String getLocalizedMessage() {
		String result = null;

		final String message = ResourceCache.instance.getCaption("msg_" + getMessageId());
		if (message != null) {
			if ( parameters.size() > 0 ) {
				result = MessageFormat.format(message, parameters.toArray(new Object[parameters.size()]));
			} else {
				result = message;
			}
		}

		return result;
	}
}
