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
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.view.TextHighlighter;
import org.solovyev.android.views.AutoResizeTextView;

import javax.annotation.Nonnull;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static android.util.TypedValue.applyDimension;

public class DisplayView extends AutoResizeTextView {

    @Nullable
    private Engine engine;
    @Nullable
    private TextHighlighter textHighlighter;
    @Nonnull
    private DisplayState state = DisplayState.empty();

    public DisplayView(Context context) {
        super(context);
        init(context);
    }

    public DisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DisplayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @Nullable
    private TextHighlighter getTextHighlighter() {
        if (textHighlighter == null && engine != null) {
            textHighlighter = new TextHighlighter(getTextColors().getDefaultColor(), false, engine);
        }
        return textHighlighter;
    }

    private void init(@Nonnull Context context) {
        final Resources resources = getResources();
        setAddEllipsis(false);
        setMinTextSize(applyDimension(COMPLEX_UNIT_SP, 10, resources.getDisplayMetrics()));
        // make text scrollable if it doesn't fit
        setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Nonnull
    private Preferences.Gui.TextColor getTextColor() {
        final Context context = getContext();
        return App.getThemeFor(context).getTextColorFor(context);
    }

    @Nonnull
    public DisplayState getState() {
        Check.isMainThread();
        return state;
    }

    public void setState(@Nonnull final DisplayState newState) {
        Check.isMainThread();

        state = newState;
        if (state.valid) {
            setText(highlightText(state));
            setTextColor(getTextColor().normal);
        } else {
            setText(App.unspan(getText()));
            setTextColor(getTextColor().error);
        }
    }

    public void setEngine(@Nullable Engine engine) {
        this.engine = engine;
    }

    @NonNull
    private CharSequence highlightText(@Nonnull DisplayState state) {
        final String text = state.text;
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        final TextHighlighter textHighlighter = getTextHighlighter();
        if (textHighlighter == null) {
            return text;
        }
        try {
            return textHighlighter.process(text).getCharSequence();
        } catch (ParseException e) {
            return text;
        }
    }
}
