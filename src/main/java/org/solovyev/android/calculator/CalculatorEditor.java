/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.widget.EditText;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.calculator.model.CalculatorParseException;
import org.solovyev.android.calculator.model.TextProcessor;
import org.solovyev.android.calculator.view.TextHighlighter;
import org.solovyev.common.utils.CollectionsUtils;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 12:25 AM
 */
public class CalculatorEditor extends EditText implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String CALC_COLOR_DISPLAY_KEY = "org.solovyev.android.calculator.CalculatorModel_color_display";
	private static final boolean CALC_COLOR_DISPLAY_DEFAULT = true;

	private boolean highlightText = true;

	@NotNull
	private final static TextProcessor<TextHighlighter.Result, String> textHighlighter = new TextHighlighter(Color.WHITE, true, CalculatorEngine.instance.getEngine());

	public CalculatorEditor(Context context) {
		super(context);
	}

	public CalculatorEditor(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CalculatorEditor(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onCheckIsTextEditor() {
		// Main goal of this implementation is to hide android soft keyboard from appearing when working with text input

		// todo serso: refactor
		// NOTE: do not copy or reuse code below, it's completely SHIT!!!

		if ( Build.VERSION.SDK_INT >= 11 ) {
			// fix for missing cursor in android 3 and higher
			for (StackTraceElement stackTraceElement : CollectionsUtils.asList(Thread.currentThread().getStackTrace())) {
				if ( "isCursorVisible".equals(stackTraceElement.getMethodName()) ) {
					return true;
				}
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
		menu.removeItem(android.R.id.startSelectingText);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);
	}

	public synchronized void redraw() {
		String text = getText().toString();

		int selectionStart = getSelectionStart();
		int selectionEnd = getSelectionEnd();

		if (highlightText) {

			Log.d(this.getClass().getName(), text);

			try {
				final TextHighlighter.Result result = textHighlighter.process(text);
				selectionStart += result.getOffset();
				selectionEnd += result.getOffset();
				text = result.toString();
			} catch (CalculatorParseException e) {
				Log.e(this.getClass().getName(), e.getMessage(), e);
			}

			Log.d(this.getClass().getName(), text);
			super.setText(Html.fromHtml(text), BufferType.EDITABLE);
		} else {
			super.setText(text, BufferType.EDITABLE);
		}

		Log.d(this.getClass().getName(), getText().toString());

		int length = getText().length();
		setSelection(Math.max(Math.min(length, selectionStart), 0), Math.max(Math.min(length, selectionEnd), 0));
	}

	public boolean isHighlightText() {
		return highlightText;
	}

	public void setHighlightText(boolean highlightText) {
		this.highlightText = highlightText;
		redraw();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
		if (CALC_COLOR_DISPLAY_KEY.equals(key)) {
			this.setHighlightText(preferences.getBoolean(CALC_COLOR_DISPLAY_KEY, CALC_COLOR_DISPLAY_DEFAULT));
		}
	}

	public void init(@NotNull SharedPreferences preferences) {
		onSharedPreferenceChanged(preferences, CALC_COLOR_DISPLAY_KEY);
	}
}
