package org.solovyev.android.calculator;

import android.app.Application;
import android.os.Handler;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AThreads;
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

    @NotNull
    private final Application application;

    @NotNull
    private final Handler uiHandler = new Handler();

    private final boolean showDebugMessages;

    public AndroidCalculatorNotifier(@NotNull Application application) {
        this(application, false);
    }

    public AndroidCalculatorNotifier(@NotNull Application application, boolean showDebugMessages) {
        assert AThreads.isUiThread();

        this.application = application;
        this.showDebugMessages = showDebugMessages;
    }

    @Override
    public void showMessage(@NotNull Message message) {
        showMessageInUiThread(message.getLocalizedMessage());
    }

    @Override
    public void showMessage(@NotNull Integer messageCode, @NotNull MessageType messageType, @NotNull List<Object> parameters) {
        showMessage(new AndroidMessage(messageCode, messageType, application, parameters));
    }

    @Override
    public void showMessage(@NotNull Integer messageCode, @NotNull MessageType messageType, @Nullable Object... parameters) {
        showMessage(new AndroidMessage(messageCode, messageType, application, parameters));
    }

    @Override
    public void showDebugMessage(@Nullable final String tag, @NotNull final String message) {
        if (showDebugMessages) {
            showMessageInUiThread(tag == null ? message : tag + ": " + message);
        }
    }

    private void showMessageInUiThread(@NotNull final String message) {
        if (AThreads.isUiThread()) {
            Toast.makeText(application, message, Toast.LENGTH_SHORT).show();
        } else {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(application,message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
