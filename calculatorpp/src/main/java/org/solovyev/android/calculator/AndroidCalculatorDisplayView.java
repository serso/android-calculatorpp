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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.android.calculator.view.TextHighlighter;
import org.solovyev.android.view.AutoResizeTextView;

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
    private final static TextProcessor<TextHighlighter.Result, String> textHighlighter = new TextHighlighter(Color.WHITE, false);

    /*
    **********************************************************************
    *
    *                           FIELDS
    *
    **********************************************************************
    */

    @NotNull
    private volatile CalculatorDisplayViewState state = CalculatorDisplayViewStateImpl.newDefaultInstance();

    private volatile boolean viewStateChange = false;

    @NotNull
    private final Object lock = new Object();

    @NotNull
    private final Handler handler = new Handler();

    private volatile boolean initialized = false;

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


    @Override
    public void setState(@NotNull final CalculatorDisplayViewState state) {
        final CharSequence text = prepareText(state.getStringResult(), state.isValid());

        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    try {
                        viewStateChange = true;

                        AndroidCalculatorDisplayView.this.state = state;
                        if (state.isValid()) {
                            setTextColor(getResources().getColor(R.color.default_text_color));
                            setText(text);

                            adjustTextSize();

                        } else {
                            // update text in order to get rid of HTML tags
                            setText(getText().toString());
                            setTextColor(getResources().getColor(R.color.display_error_text_color));

                            // error messages are never shown -> just greyed out text (error message will be shown on click)
                            //setText(state.getErrorMessage());
                            //redraw();
                        }
                    } finally {
                        viewStateChange = false;
                    }
                }
            }
        });
    }

    @NotNull
    @Override
    public CalculatorDisplayViewState getState() {
        synchronized (lock) {
            return this.state;
        }
    }

    @Nullable
    private static CharSequence prepareText(@Nullable String text, boolean valid) {
        CharSequence result;

        if (valid && text != null) {

            //Log.d(this.getClass().getName(), text);

            try {
                final TextHighlighter.Result processedText = textHighlighter.process(text);
                text = processedText.toString();
                result = Html.fromHtml(text);
            } catch (CalculatorParseException e) {
                result = text;
            }
        } else {
            result = text;
        }

        return result;
    }

    private void adjustTextSize() {
        // todo serso: think where to move it (keep in mind org.solovyev.android.view.AutoResizeTextView.resetTextSize())
        setAddEllipsis(false);
        setMinTextSize(10);
        resizeText();
    }

    public synchronized void init(@NotNull Context context) {
        if (!initialized) {
            this.setOnClickListener(new CalculatorDisplayOnClickListener(context));

            this.initialized = true;
        }
    }
}
