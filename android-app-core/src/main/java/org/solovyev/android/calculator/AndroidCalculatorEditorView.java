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

package org.solovyev.android.calculator;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.widget.EditText;
import org.solovyev.common.collections.Collections;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 12:25 AM
 */
public class AndroidCalculatorEditorView extends EditText implements CalculatorEditorView {

	private volatile boolean initialized = false;

	@SuppressWarnings("UnusedDeclaration")
	@Nonnull
	private volatile CalculatorEditorViewState viewState = CalculatorEditorViewStateImpl.newDefaultInstance();

	private volatile boolean viewStateChange = false;

	@Nonnull
	private final Handler uiHandler = new Handler();

	public AndroidCalculatorEditorView(Context context) {
		super(context);
	}

	public AndroidCalculatorEditorView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AndroidCalculatorEditorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onCheckIsTextEditor() {
		// NOTE: code below can be used carefully and should not be copied without special intention
		// The main purpose of code is to disable soft input (virtual keyboard) but leave all the TextEdit functionality, like cursor, scrolling, copy/paste menu etc

		if (Build.VERSION.SDK_INT >= 11) {
			// fix for missing cursor in android 3 and higher
			try {
				// IDEA: return false always except if method was called from TextView.isCursorVisible() method
				for (StackTraceElement stackTraceElement : Collections.asList(Thread.currentThread().getStackTrace())) {
					if ("isCursorVisible".equals(stackTraceElement.getMethodName())) {
						return true;
					}
				}
			} catch (RuntimeException e) {
				// just in case...
			}

			return false;
		} else {
			return false;
		}
	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		super.onCreateContextMenu(menu);

		menu.removeItem(android.R.id.selectAll);
	}

	public void setHighlightText(boolean highlightText) {
		//this.highlightText = highlightText;
		Locator.getInstance().getEditor().updateViewState();
	}

	public synchronized void init() {
		if (!initialized) {
			this.addTextChangedListener(new TextWatcherImpl());

			initialized = true;
		}
	}

	@Override
	public void setState(@Nonnull final CalculatorEditorViewState viewState) {
		synchronized (this) {

			uiHandler.post(new Runnable() {
				@Override
				public void run() {
					final AndroidCalculatorEditorView editorView = AndroidCalculatorEditorView.this;
					synchronized (AndroidCalculatorEditorView.this) {
						try {
							editorView.viewStateChange = true;
							editorView.viewState = viewState;
							editorView.setText(viewState.getTextAsCharSequence(), BufferType.EDITABLE);
							final int selection = CalculatorEditorImpl.correctSelection(viewState.getSelection(), editorView.getText());
							editorView.setSelection(selection);
						} finally {
							editorView.viewStateChange = false;
						}
					}
				}
			});
		}
	}

	@Override
	protected void onSelectionChanged(int selStart, int selEnd) {
		synchronized (this) {
			if (initialized && !viewStateChange) {
				// external text change => need to notify editor
				super.onSelectionChanged(selStart, selEnd);

				if (selStart == selEnd) {
					// only if cursor moving, if selection do nothing
					Locator.getInstance().getEditor().setSelection(selStart);
				}
			}
		}
	}

	public void handleTextChange(Editable s) {
		synchronized (this) {
			if (initialized && !viewStateChange) {
				// external text change => need to notify editor
				Locator.getInstance().getEditor().setText(String.valueOf(s));
			}
		}
	}

	private final class TextWatcherImpl implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			handleTextChange(s);
		}
	}
}
