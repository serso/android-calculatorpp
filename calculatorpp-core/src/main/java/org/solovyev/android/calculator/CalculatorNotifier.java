package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageType;

import java.util.List;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 1:52 PM
 */
public interface CalculatorNotifier {

    void showMessage(@NotNull Message message);

    void showMessage(@NotNull Integer messageCode, @NotNull MessageType messageType, @NotNull List<Object> parameters);

    void showMessage(@NotNull Integer messageCode, @NotNull MessageType messageType, @Nullable Object... parameters);
}
