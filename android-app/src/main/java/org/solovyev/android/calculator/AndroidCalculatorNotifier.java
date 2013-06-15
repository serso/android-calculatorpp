package org.solovyev.android.calculator;

import android.app.Application;
import android.os.Handler;
import android.widget.Toast;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.android.Threads;
import org.solovyev.android.msg.AndroidMessage;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageType;

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
		assert Threads.isUiThread();

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
