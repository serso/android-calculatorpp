/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view.widgets;

import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * User: serso
 * Date: 10/9/11
 * Time: 4:27 PM
 */
public class SoftKeyboardDisabler implements View.OnTouchListener {

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean result;
		Log.d(this.getClass().getName(), "org.solovyev.android.view.widgets.SoftKeyboardDisabler.onTouch(): action=" + event.getAction() + ", event=" + event);

		if (v instanceof EditText) {
			final EditText editText = (EditText) v;
			int inputType = editText.getInputType();
			int selectionStart = editText.getSelectionStart();
			int selectionEnd = editText.getSelectionEnd();

			// disable soft input
			editText.setInputType(InputType.TYPE_NULL);
			editText.onTouchEvent(event);

			// restore input type
			editText.setInputType(inputType);
			editText.setSelection(selectionStart, selectionEnd);

			result = true;
		} else {
			result = false;
		}

		return result;
	}


}
