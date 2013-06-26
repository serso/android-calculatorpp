/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.solovyev.common.msg.AbstractMessage;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageType;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 8:06 PM
 */
public class CalculatorMessage extends AbstractMessage {

	public CalculatorMessage(@Nonnull String messageCode, @Nonnull MessageType messageType, @Nullable Object... parameters) {
		super(messageCode, messageType, parameters);
	}

	public CalculatorMessage(@Nonnull String messageCode, @Nonnull MessageType messageType, @Nonnull List<?> parameters) {
		super(messageCode, messageType, parameters);
	}

	@Nonnull
	public static Message newInfoMessage(@Nonnull String messageCode, @Nullable Object... parameters) {
		return new CalculatorMessage(messageCode, MessageType.info, parameters);
	}

	@Nonnull
	public static Message newWarningMessage(@Nonnull String messageCode, @Nullable Object... parameters) {
		return new CalculatorMessage(messageCode, MessageType.warning, parameters);
	}

	@Nonnull
	public static Message newErrorMessage(@Nonnull String messageCode, @Nullable Object... parameters) {
		return new CalculatorMessage(messageCode, MessageType.error, parameters);
	}

	@Override
	protected String getMessagePattern(@Nonnull Locale locale) {
		final ResourceBundle rb = CalculatorMessages.getBundle(locale);
		return rb.getString(getMessageCode());
	}
}
