package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.solovyev.common.msg.Message;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 1:52 PM
 */
public interface CalculatorNotifier {

    void showMessage(@NotNull Message message);
}
