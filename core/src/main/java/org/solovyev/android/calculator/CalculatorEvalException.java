/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.solovyev.common.exceptions.SersoException;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageType;

import java.util.List;
import java.util.Locale;

/**
 * User: serso
 * Date: 12/8/11
 * Time: 1:27 AM
 */
public class CalculatorEvalException extends SersoException implements Message {

	@NotNull
	private final Message message;

	@NotNull
	private final String expression;

	public CalculatorEvalException(@NotNull Message message, @NotNull Throwable cause, String expression) {
		super(cause);
		this.message = message;
		this.expression = expression;
	}


	@NotNull
	public String getExpression() {
		return expression;
	}

	@NotNull
	@Override
	public String getMessageCode() {
		return this.message.getMessageCode();
	}

	@NotNull
	@Override
	public List<Object> getParameters() {
		return this.message.getParameters();
	}

	@NotNull
	@Override
	public MessageType getMessageType() {
		return this.message.getMessageType();
	}

	@Override
	@NotNull
	public String getLocalizedMessage() {
		return this.message.getLocalizedMessage(Locale.getDefault());
	}

	@NotNull
	@Override
	public String getLocalizedMessage(@NotNull Locale locale) {
		return this.message.getLocalizedMessage(locale);
	}
}

