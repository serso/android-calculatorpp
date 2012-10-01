package org.solovyev.android.calculator;

import android.app.Application;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    public AndroidCalculatorNotifier(@NotNull Application application) {
        this.application = application;
    }

    @Override
    public void showMessage(@NotNull Message message) {
        Toast.makeText(application, message.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(@NotNull Integer messageCode, @NotNull MessageType messageType, @NotNull List<Object> parameters) {
        showMessage(new AndroidMessage(messageCode, messageType, application, parameters));
    }

    @Override
    public void showMessage(@NotNull Integer messageCode, @NotNull MessageType messageType, @Nullable Object... parameters) {
        showMessage(new AndroidMessage(messageCode, messageType, application, parameters));
    }
}
