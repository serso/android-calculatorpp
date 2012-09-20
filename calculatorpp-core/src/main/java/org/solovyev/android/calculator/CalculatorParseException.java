/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.exceptions.SersoException;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageType;

import java.util.List;
import java.util.Locale;

/**
 * User: serso
 * Date: 10/6/11
 * Time: 9:25 PM
 */
public class CalculatorParseException extends SersoException implements Message {

	@NotNull
	private final Message message;

	@NotNull
	private final String expression;

	@Nullable
	private final Integer position;

	public CalculatorParseException(@NotNull jscl.text.ParseException jsclParseException) {
		this.message = jsclParseException;
		this.expression = jsclParseException.getExpression();
		this.position = jsclParseException.getPosition();
	}

	public CalculatorParseException(@Nullable Integer position,
                                    @NotNull String expression,
                                    @NotNull Message message) {
		this.message = message;
		this.expression = expression;
		this.position = position;
	}

	public CalculatorParseException(@NotNull String expression,
                                    @NotNull Message message) {
        this(null, expression, message);
	}

	@NotNull
	public String getExpression() {
		return expression;
	}

	@Nullable
	public Integer getPosition() {
		return position;
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
	@Nullable
	public String getLocalizedMessage() {
		return this.message.getLocalizedMessage(Locale.getDefault());
	}

	@NotNull
	@Override
	public String getLocalizedMessage(@NotNull Locale locale) {
		return this.message.getLocalizedMessage(locale);
	}
}
