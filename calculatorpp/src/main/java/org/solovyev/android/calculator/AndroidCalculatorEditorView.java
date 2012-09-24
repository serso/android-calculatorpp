/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.widget.EditText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.android.calculator.view.TextHighlighter;
import org.solovyev.common.collections.CollectionsUtils;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 12:25 AM
 */
public class AndroidCalculatorEditorView extends EditText implements SharedPreferences.OnSharedPreferenceChangeListener, CalculatorEditorView {

    private static final String CALC_COLOR_DISPLAY_KEY = "org.solovyev.android.calculator.CalculatorModel_color_display";
    private static final boolean CALC_COLOR_DISPLAY_DEFAULT = true;

    private boolean highlightText = true;

    @NotNull
    private final static TextProcessor<TextHighlighter.Result, String> textHighlighter = new TextHighlighter(Color.WHITE, false);

    @NotNull
    private volatile CalculatorEditorViewState viewState = CalculatorEditorViewStateImpl.newDefaultInstance();

    private volatile boolean viewStateChange = false;

    // NOTE: static because super constructor calls some overridden methods (like onSelectionChanged and current lock is not yet created)
    @NotNull
    private static final Object lock = new Object();

    @NotNull
    private final Handler handler = new Handler();

    public AndroidCalculatorEditorView(Context context) {
        super(context);
        this.addTextChangedListener(new TextWatcherImpl());
    }

    public AndroidCalculatorEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.addTextChangedListener(new TextWatcherImpl());
    }

    public AndroidCalculatorEditorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.addTextChangedListener(new TextWatcherImpl());
    }


    @Override
    public boolean onCheckIsTextEditor() {
        // NOTE: code below can be used carefully and should not be copied without special intention
        // The main purpose of code is to disable soft input (virtual keyboard) but leave all the TextEdit functionality, like cursor, scrolling, copy/paste menu etc

        if (Build.VERSION.SDK_INT >= 11) {
            // fix for missing cursor in android 3 and higher
            try {
                // IDEA: return false always except if method was called from TextView.isCursorVisible() method
                for (StackTraceElement stackTraceElement : CollectionsUtils.asList(Thread.currentThread().getStackTrace())) {
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

    @Nullable
    private CharSequence prepareText(@NotNull String text, boolean highlightText) {
        CharSequence result;

         if (highlightText) {

             try {
                 final TextHighlighter.Result processesText = textHighlighter.process(text);

                 assert processesText.getOffset() == 0;

                 result = Html.fromHtml(processesText.toString());
             } catch (CalculatorParseException e) {
                 // set raw text
                 result = text;

                 Log.e(this.getClass().getName(), e.getMessage(), e);
             }
         } else {
             result = text;
         }

        return result;
     }

    public boolean isHighlightText() {
        return highlightText;
    }

    public void setHighlightText(boolean highlightText) {
        this.highlightText = highlightText;
        CalculatorLocatorImpl.getInstance().getEditor().updateViewState();
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

    @Override
    public void setState(@NotNull final CalculatorEditorViewState viewState) {

        final CharSequence text = prepareText(viewState.getText(), highlightText);

        handler.post(new Runnable() {
            @Override
            public void run() {
                final AndroidCalculatorEditorView editorView = AndroidCalculatorEditorView.this;
                synchronized (lock) {
                    try {
                        editorView.viewStateChange = true;
                        editorView.viewState = viewState;
                        editorView.setText(text, BufferType.EDITABLE);
                        editorView.setSelection(viewState.getSelection());
                    } finally {
                        editorView.viewStateChange = false;
                    }
                }
            }
        });
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        synchronized (lock) {
            if (!viewStateChange) {
                // external text change => need to notify editor
                super.onSelectionChanged(selStart, selEnd);
                CalculatorLocatorImpl.getInstance().getEditor().setSelection(selStart);
            }
        }
    }

    public void handleTextChange(Editable s) {
        synchronized (lock) {
            if (!viewStateChange) {
                // external text change => need to notify editor
                CalculatorLocatorImpl.getInstance().getEditor().setText(String.valueOf(s));
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
