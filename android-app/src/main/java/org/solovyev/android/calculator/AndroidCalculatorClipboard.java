package org.solovyev.android.calculator;

import android.app.Application;
import android.content.Context;
import android.text.ClipboardManager;
import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 1:35 PM
 */
public class AndroidCalculatorClipboard implements CalculatorClipboard {

	@Nonnull
	private final Context context;

	public AndroidCalculatorClipboard(@Nonnull Application application) {
		this.context = application;
	}

	@Override
	public String getText() {
		final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		if (clipboard.hasText()) {
			return String.valueOf(clipboard.getText());
		} else {
			return null;
		}
	}

	@Override
	public void setText(@Nonnull String text) {
		final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		clipboard.setText(text);
	}

	@Override
	public void setText(@Nonnull CharSequence text) {
		final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		clipboard.setText(text);
	}
}
