/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.AttributeSet;
import android.util.TypedValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.core.R;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.android.calculator.view.TextHighlighter;
import org.solovyev.android.view.AutoResizeTextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private final Handler uiHandler = new Handler();

    @NotNull
    private final ExecutorService bgExecutor = Executors.newSingleThreadExecutor();

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

        uiHandler.post(new Runnable() {
            @Override
            public void run() {

                synchronized (lock) {
                    try {
                        viewStateChange = true;

                        final CharSequence text = prepareText(state.getStringResult(), state.isValid());

                        AndroidCalculatorDisplayView.this.state = state;
                        if (state.isValid()) {
                            setTextColor(getResources().getColor(R.color.cpp_default_text_color));
                            setText(text);

                            adjustTextSize();

                        } else {
                            // update text in order to get rid of HTML tags
                            setText(getText().toString());
                            setTextColor(getResources().getColor(R.color.cpp_display_error_text_color));

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
		this.init(context, true);
    }

	public synchronized void init(@NotNull Context context, boolean fromApp) {
		if (!initialized) {
			if (fromApp) {
				final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
				final CalculatorPreferences.Gui.Layout layout = CalculatorPreferences.Gui.getLayout(preferences);

				if ( layout == CalculatorPreferences.Gui.Layout.main_calculator_mobile ) {
					setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.cpp_display_text_size_mobile));
				}

				if (context instanceof FragmentActivity) {
					this.setOnClickListener(new CalculatorDisplayOnClickListener((FragmentActivity) context));
				} else {
					throw new IllegalArgumentException("Must be fragment activity, got " + context.getClass());
				}
			}

			this.initialized = true;
		}
	}
}
