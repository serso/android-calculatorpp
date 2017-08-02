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

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static android.util.TypedValue.applyDimension;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.view.TextHighlighter;
import org.solovyev.android.views.AutoResizeTextView;

import javax.annotation.Nonnull;

public class DisplayView extends AutoResizeTextView {

    private class AsyncHighlighter extends AsyncTask<Void, Void, CharSequence> {

        @NonNull
        private final String text;
        @Nullable
        private final TextHighlighter highlighter;

        AsyncHighlighter(@NonNull String text) {
            this.text = text;
            this.highlighter = getTextHighlighter();
        }

        boolean shouldAsync() {
            return !TextUtils.isEmpty(text) && highlighter != null;
        }

        @NonNull
        @Override
        protected CharSequence doInBackground(Void... params) {
            if (highlighter == null) {
                return text;
            }
            try {
                return highlighter.process(text).getCharSequence();
            } catch (ParseException e) {
                return text;
            }
        }

        @Override
        protected void onPostExecute(@NonNull CharSequence text) {
            if (highlighterTask != this) {
                return;
            }
            setText(text);
            setTextColor(getTextColor().normal);
            highlighterTask = null;
        }
    }

    @Nullable
    private Engine engine;
    @Nullable
    private TextHighlighter textHighlighter;
    @Nonnull
    private DisplayState state = DisplayState.empty();
    @Nullable
    private AsyncHighlighter highlighterTask;

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
            asyncHighlightText(newState);
        } else {
            cancelAsyncHighlightText(false);
            setText(App.unspan(getText()));
            setTextColor(getTextColor().error);
        }
    }

    private void cancelAsyncHighlightText(boolean applyLastState) {
        if (highlighterTask == null) {
            return;
        }
        highlighterTask.cancel(false);
        if (applyLastState) {
            highlighterTask.onPostExecute(highlighterTask.text);
        }
        highlighterTask = null;
    }

    private void asyncHighlightText(@NonNull DisplayState state) {
        cancelAsyncHighlightText(false);
        highlighterTask = new AsyncHighlighter(state.text);
        if (highlighterTask.shouldAsync()) {
            highlighterTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            return;
        }
        highlighterTask.onPostExecute(state.text);
        Check.isNull(highlighterTask);
    }

    public void setEngine(@Nullable Engine engine) {
        this.engine = engine;
    }

    public void onDestroy() {
        cancelAsyncHighlightText(true);
    }
}
