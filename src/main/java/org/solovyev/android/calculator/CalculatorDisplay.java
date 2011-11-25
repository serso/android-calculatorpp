/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.model.ParseException;
import org.solovyev.android.calculator.model.TextProcessor;
import org.solovyev.android.view.AutoResizeTextView;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 10:58 PM
 */
public class CalculatorDisplay extends AutoResizeTextView {

	private boolean valid = true;

	@Nullable
	private String errorMessage;

	@NotNull
	private JsclOperation jsclOperation = JsclOperation.numeric;

	@NotNull
	private final static TextProcessor<TextHighlighter.Result> textHighlighter = new TextHighlighter(Color.WHITE, true);

	public CalculatorDisplay(Context context) {
		super(context);
	}

	public CalculatorDisplay(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CalculatorDisplay(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
		if (valid) {
			errorMessage = null;
		}
	}

	@Nullable
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(@Nullable String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setJsclOperation(@NotNull JsclOperation jsclOperation) {
		this.jsclOperation = jsclOperation;
	}

	@NotNull
	public JsclOperation getJsclOperation() {
		return jsclOperation;
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);

		setValid(true);
	}

	public synchronized void redraw() {
		if (isValid()) {
			String text = getText().toString();

			Log.d(this.getClass().getName(), text);

			try {
				TextHighlighter.Result result = textHighlighter.process(text);
				text = result.toString();
			} catch (ParseException e) {
				Log.e(this.getClass().getName(), e.getMessage(), e);
			}

			Log.d(this.getClass().getName(), text);
			super.setText(Html.fromHtml(text), BufferType.EDITABLE);
		}

		// todo serso: think where to move it (keep in mind org.solovyev.android.view.AutoResizeTextView.resetTextSize())
		setAddEllipsis(false);
		setMinTextSize(10);
		resizeText();
	}

}
