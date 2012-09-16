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
import org.solovyev.common.collections.CollectionsUtils;

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
        init();
	}

	public CalculatorEditor(Context context, AttributeSet attrs) {
		super(context, attrs);
        init();
    }

    private void init() {
        // NOTE: in this solution cursor is missing

        /*this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final TextView textView = (TextView)v;
                // backup the input type
                int inputType = textView.getInputType();

                // disable soft input
                textView.setInputType(InputType.TYPE_NULL);

                // call native handler
                textView.onTouchEvent(event);

                // restore input type
                textView.setInputType(inputType);

                // consume touch even
                return true;
            }
        });*/
    }


    @Override
    public boolean onCheckIsTextEditor() {
        // NOTE: code below can be used carefully and should not be copied without special intention
        // The main purpose of code is to disable soft input (virtual keyboard) but leave all the TextEdit functionality, like cursor, scrolling, copy/paste menu etc

        if ( Build.VERSION.SDK_INT >= 11 ) {
            // fix for missing cursor in android 3 and higher
            try {
                // IDEA: return false always except if method was called from TextView.isCursorVisible() method
                for (StackTraceElement stackTraceElement : CollectionsUtils.asList(Thread.currentThread().getStackTrace())) {
                    if ( "isCursorVisible".equals(stackTraceElement.getMethodName()) ) {
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

    public CalculatorEditor(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        init();
    }

	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		super.onCreateContextMenu(menu);

		menu.removeItem(android.R.id.selectAll);
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
