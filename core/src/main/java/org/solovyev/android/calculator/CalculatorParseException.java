/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageLevel;

import java.util.List;
import java.util.Locale;

/**
 * User: serso
 * Date: 10/6/11
 * Time: 9:25 PM
 */
public class CalculatorParseException extends Exception implements Message {

	@Nonnull
	private final Message message;

	@Nonnull
	private final String expression;

	@Nullable
	private final Integer position;

	public CalculatorParseException(@Nonnull jscl.text.ParseException jsclParseException) {
		this.message = jsclParseException;
		this.expression = jsclParseException.getExpression();
		this.position = jsclParseException.getPosition();
	}

	public CalculatorParseException(@Nullable Integer position,
									@Nonnull String expression,
									@Nonnull Message message) {
		this.message = message;
		this.expression = expression;
		this.position = position;
	}

	public CalculatorParseException(@Nonnull String expression,
									@Nonnull Message message) {
		this(null, expression, message);
	}

	@Nonnull
	public String getExpression() {
		return expression;
	}

	@Nullable
	public Integer getPosition() {
		return position;
	}

	@Nonnull
	@Override
	public String getMessageCode() {
		return this.message.getMessageCode();
	}

	@Nonnull
	@Override
	public List<Object> getParameters() {
		return this.message.getParameters();
	}

	@Nonnull
	@Override
	public MessageLevel getMessageLevel() {
		return this.message.getMessageLevel();
	}

	@Override
	@Nonnull
	public String getLocalizedMessage() {
		return this.message.getLocalizedMessage(Locale.getDefault());
	}

	@Nonnull
	@Override
	public String getLocalizedMessage(@Nonnull Locale locale) {
		return this.message.getLocalizedMessage(locale);
	}
}
