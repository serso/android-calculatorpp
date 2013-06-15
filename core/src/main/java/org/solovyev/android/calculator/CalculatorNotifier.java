package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageType;

import java.util.List;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 1:52 PM
 */
public interface CalculatorNotifier {

	void showMessage(@Nonnull Message message);

	void showMessage(@Nonnull Integer messageCode, @Nonnull MessageType messageType, @Nonnull List<Object> parameters);

	void showMessage(@Nonnull Integer messageCode, @Nonnull MessageType messageType, @Nullable Object... parameters);

	void showDebugMessage(@Nullable String tag, @Nonnull String message);
}
