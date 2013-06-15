/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageLevel;

import java.util.List;
import java.util.Locale;

/**
 * User: serso
 * Date: 12/8/11
 * Time: 1:27 AM
 */
public class CalculatorEvalException extends Exception implements Message {

	@Nonnull
	private final Message message;

	@Nonnull
	private final String expression;

	public CalculatorEvalException(@Nonnull Message message, @Nonnull Throwable cause, String expression) {
		super(cause);
		this.message = message;
		this.expression = expression;
	}


	@Nonnull
	public String getExpression() {
		return expression;
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

