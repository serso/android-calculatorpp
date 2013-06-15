package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
	public void showMessage(@Nonnull Message message) {
	}

	@Override
	public void showMessage(@Nonnull Integer messageCode, @Nonnull MessageType messageType, @Nonnull List<Object> parameters) {
	}

	@Override
	public void showMessage(@Nonnull Integer messageCode, @Nonnull MessageType messageType, @Nullable Object... parameters) {
	}

	@Override
	public void showDebugMessage(@Nullable String tag, @Nonnull String message) {
	}
}
