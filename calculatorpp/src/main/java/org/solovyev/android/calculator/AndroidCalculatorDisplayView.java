/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import jscl.math.Generic;
import jscl.math.function.Constant;
import jscl.math.function.IConstant;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.android.calculator.view.NumeralBaseConverterDialog;
import org.solovyev.android.calculator.view.TextHighlighter;
import org.solovyev.android.menu.LabeledMenuItem;
import org.solovyev.android.view.AutoResizeTextView;
import org.solovyev.common.collections.CollectionsUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 10:58 PM
 */
public class AndroidCalculatorDisplayView extends AutoResizeTextView implements CalculatorDisplayView {

    /*
    **********************************************************************
    *
    *                           STATIC FIELDS
    *
    **********************************************************************
    */

    @NotNull
    private final static TextProcessor<TextHighlighter.Result, String> textHighlighter = new TextHighlighter(Color.WHITE, false, CalculatorEngine.instance.getEngine());

    /*
    **********************************************************************
    *
    *                           FIELDS
    *
    **********************************************************************
    */

    @NotNull
    private CalculatorDisplayViewState state = CalculatorDisplayViewStateImpl.newDefaultInstance();

    @NotNull
    private final Object lock = new Object();

    @NotNull
    private final Handler handler = new Handler();

    /*
    **********************************************************************
    *
    *                           CONSTRUCTORS
    *
    **********************************************************************
    */

    public AndroidCalculatorDisplayView(Context context) {
        super(context);
    }

    public AndroidCalculatorDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AndroidCalculatorDisplayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*
    **********************************************************************
    *
    *                           METHODS
    *
    **********************************************************************
    */

    public boolean isValid() {
        synchronized (lock) {
            return this.state.isValid();
        }
    }

    @Override
    public void setState(@NotNull final CalculatorDisplayViewState state) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    AndroidCalculatorDisplayView.this.state = state;
                    if ( state.isValid() ) {
                        setTextColor(getResources().getColor(R.color.default_text_color));
                        setText(state.getStringResult());
                        redraw();
                    } else {
                        setTextColor(getResources().getColor(R.color.display_error_text_color));
                        setText(state.getErrorMessage());
                        redraw();
                    }
                }
            }
        }, 1);
    }

    @NotNull
    @Override
    public CalculatorDisplayViewState getState() {
        synchronized (lock) {
            return this.state;
        }
    }

    private synchronized void redraw() {
        if (isValid()) {
            String text = getText().toString();

            Log.d(this.getClass().getName(), text);

            try {
                TextHighlighter.Result result = textHighlighter.process(text);
                text = result.toString();
            } catch (CalculatorParseException e) {
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
