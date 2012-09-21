/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import android.widget.EditText;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.Editor;

/**
 * User: serso
 * Date: 12/17/11
 * Time: 9:39 PM
 */
public class TextViewEditorAdapter implements Editor {

	@NotNull
	private final TextView textView;

	public TextViewEditorAdapter(@NotNull TextView textView) {
		this.textView = textView;
	}

	@Override
	public CharSequence getText() {
		return textView.getText().toString();
	}

	@Override
	public void setText(@Nullable CharSequence text) {
		textView.setText(text);
	}

	@Override
	public int getSelection() {
		return textView.getSelectionStart();
	}

	@Override
	public void setSelection(int selection) {
		if ( textView instanceof EditText ) {
			((EditText) textView).setSelection(selection);
		}
	}
}
