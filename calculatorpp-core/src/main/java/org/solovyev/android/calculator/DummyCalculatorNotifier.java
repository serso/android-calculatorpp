package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageType;

import java.util.List;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 2:00 PM
 */
public class DummyCalculatorNotifier implements CalculatorNotifier {

    @Override
    public void showMessage(@NotNull Message message) {
    }

    @Override
    public void showMessage(@NotNull Integer messageCode, @NotNull MessageType messageType, @NotNull List<Object> parameters) {
    }

    @Override
    public void showMessage(@NotNull Integer messageCode, @NotNull MessageType messageType, @Nullable Object... parameters) {
    }
}
