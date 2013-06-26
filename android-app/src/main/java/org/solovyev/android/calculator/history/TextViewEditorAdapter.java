/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import android.widget.EditText;
import android.widget.TextView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.solovyev.android.calculator.Editor;

/**
 * User: serso
 * Date: 12/17/11
 * Time: 9:39 PM
 */
public class TextViewEditorAdapter implements Editor {

	@Nonnull
	private final TextView textView;

	public TextViewEditorAdapter(@Nonnull TextView textView) {
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
		if (textView instanceof EditText) {
			((EditText) textView).setSelection(selection);
		}
	}
}
