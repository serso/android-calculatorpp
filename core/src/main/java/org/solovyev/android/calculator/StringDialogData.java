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

import org.solovyev.common.msg.MessageType;

/**
 * User: serso
 * Date: 1/20/13
 * Time: 1:01 PM
 */
public class StringDialogData implements DialogData {

	@Nonnull
	private final String message;

	@Nonnull
	private final MessageType messageType;

	@Nullable
	private final String title;

	private StringDialogData(@Nonnull String message, @Nonnull MessageType messageType, @Nullable String title) {
		this.message = message;
		this.messageType = messageType;
		this.title = title;
	}

	@Nonnull
	public static DialogData newInstance(@Nonnull String message, @Nonnull MessageType messageType, @Nullable String title) {
		return new StringDialogData(message, messageType, title);
	}

	@Nonnull
	@Override
	public String getMessage() {
		return message;
	}

	@Nonnull
	@Override
	public MessageType getMessageLevel() {
		return messageType;
	}

	@Nullable
	@Override
	public String getTitle() {
		return title;
	}
}
