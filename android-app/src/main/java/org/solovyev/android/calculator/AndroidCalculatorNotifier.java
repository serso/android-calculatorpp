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

import android.app.Application;
import android.os.Handler;
import android.widget.Toast;
import org.solovyev.android.Threads;
import org.solovyev.android.msg.AndroidMessage;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 2:00 PM
 */
public class AndroidCalculatorNotifier implements CalculatorNotifier {

	@Nonnull
	private final Application application;

	@Nonnull
	private final Handler uiHandler = new Handler();

	private final boolean showDebugMessages;

	public AndroidCalculatorNotifier(@Nonnull Application application) {
		this(application, false);
	}

	public AndroidCalculatorNotifier(@Nonnull Application application, boolean showDebugMessages) {
		if (!Threads.isUiThread()) throw new AssertionError();

		this.application = application;
		this.showDebugMessages = showDebugMessages;
	}

	@Override
	public void showMessage(@Nonnull Message message) {
		showMessageInUiThread(message.getLocalizedMessage());
	}

	@Override
	public void showMessage(@Nonnull Integer messageCode, @Nonnull MessageType messageType, @Nonnull List<Object> parameters) {
		showMessage(new AndroidMessage(messageCode, messageType, application, parameters));
	}

	@Override
	public void showMessage(@Nonnull Integer messageCode, @Nonnull MessageType messageType, @Nullable Object... parameters) {
		showMessage(new AndroidMessage(messageCode, messageType, application, parameters));
	}

	@Override
	public void showDebugMessage(@Nullable final String tag, @Nonnull final String message) {
		if (showDebugMessages) {
			showMessageInUiThread(tag == null ? message : tag + ": " + message);
		}
	}

	private void showMessageInUiThread(@Nonnull final String message) {
		if (Threads.isUiThread()) {
			Toast.makeText(application, message, Toast.LENGTH_SHORT).show();
		} else {
			uiHandler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(application, message, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

}
