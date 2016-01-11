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

import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.Check;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EditorState {

    public static final long NO_SEQUENCE = -1;
    @Nonnull
    private static final String JSON_TEXT = "t";
    @Nonnull
    private static final String JSON_SELECTION = "s";
    private static long counter = NO_SEQUENCE + 1;

    public final long sequence;
    @Nonnull
    public final CharSequence text;
    public final int selection;
    @Nullable
    private String textString;

    private EditorState() {
        this("", 0);
    }

    private EditorState(@Nonnull CharSequence text, int selection) {
        Check.isMainThread();
        this.sequence = counter++;
        this.text = text;
        this.selection = selection;
    }

    private EditorState(@Nonnull JSONObject json) {
        this(json.optString(JSON_TEXT), json.optInt(JSON_SELECTION));
    }

    @Nonnull
    public static EditorState empty() {
        return new EditorState();
    }

    @Nonnull
    public static EditorState forNewSelection(@Nonnull EditorState state, int selection) {
        return new EditorState(state.text, selection);
    }

    @Nonnull
    public static EditorState create(@Nonnull CharSequence text, int selection) {
        return new EditorState(text, selection);
    }

    @Nonnull
    public static EditorState create(@Nonnull JSONObject json) {
        return new EditorState(json);
    }

    @Nonnull
    public String getTextString() {
        if (textString == null) {
            textString = text.toString();
        }
        return textString;
    }

    public boolean same(@Nonnull EditorState that) {
        return TextUtils.equals(text, that.text) && selection == that.selection;
    }

    @Nonnull
    public String toJson() throws JSONException {
        final JSONObject json = new JSONObject();
        json.put(JSON_TEXT, getTextString());
        json.put(JSON_SELECTION, selection);
        return json.toString();
    }
}
