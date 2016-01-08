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
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.TypedValue;

import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.android.calculator.text.TextProcessorEditorResult;
import org.solovyev.android.calculator.view.TextHighlighter;
import org.solovyev.android.view.AutoResizeTextView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    @Nonnull
    private final TextProcessor<TextProcessorEditorResult, String> textHighlighter;

	/*
	**********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/
    @Nonnull
    private final Object lock = new Object();
    @Nonnull
    private final Handler uiHandler = new Handler();
    @Nonnull
    private volatile DisplayState state = DisplayState.empty();
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
        textHighlighter = new TextHighlighter(getTextColors().getDefaultColor(), false);
    }

    public AndroidCalculatorDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textHighlighter = new TextHighlighter(getTextColors().getDefaultColor(), false);
    }

    public AndroidCalculatorDisplayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        textHighlighter = new TextHighlighter(getTextColors().getDefaultColor(), false);
    }

	/*
	**********************************************************************
	*
	*                           METHODS
	*
	**********************************************************************
	*/

    private Preferences.Gui.TextColor getTextColor() {
        final Context context = getContext();
        return App.getThemeIn(context).getTextColor(context);
    }

    @Nonnull
    @Override
    public DisplayState getState() {
        synchronized (lock) {
            return this.state;
        }
    }

    @Override
    public void setState(@Nonnull final DisplayState state) {

        uiHandler.post(new Runnable() {
            @Override
            public void run() {

                synchronized (lock) {

                    final CharSequence text = prepareText(state.getStringResult(), state.isValid());

                    AndroidCalculatorDisplayView.this.state = state;
                    if (state.isValid()) {
                        setTextColor(getTextColor().normal);
                        setText(text);

                        adjustTextSize();

                    } else {
                        // update text in order to get rid of HTML tags
                        setText(getText().toString());
                        setTextColor(getTextColor().error);

                        // error messages are never shown -> just greyed out text (error message will be shown on click)
                        //setText(state.getErrorMessage());
                        //redraw();
                    }
                }
            }
        });
    }

    @Nullable
    private CharSequence prepareText(@Nullable String text, boolean valid) {
        CharSequence result;

        if (valid && text != null) {
            try {
                final TextProcessorEditorResult processedText = textHighlighter.process(text);
                text = processedText.toString();
                result = processedText.getCharSequence();
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

    public synchronized void init(@Nonnull Context context) {
        this.init(context, true);
    }

    public synchronized void init(@Nonnull Context context, boolean fromApp) {
        if (!initialized) {
            if (fromApp) {
                final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                final Preferences.Gui.Layout layout = Preferences.Gui.getLayout(preferences);

                if (!layout.isOptimized()) {
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
