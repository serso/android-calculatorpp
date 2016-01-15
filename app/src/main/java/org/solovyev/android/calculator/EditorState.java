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

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EditorState implements Parcelable {

    public static final long NO_SEQUENCE = -1;
    public static final Creator<EditorState> CREATOR = new Creator<EditorState>() {
        @Override
        public EditorState createFromParcel(Parcel in) {
            return new EditorState(in);
        }

        @Override
        public EditorState[] newArray(int size) {
            return new EditorState[size];
        }
    };
    private static final String JSON_TEXT = "t";
    private static final String JSON_SELECTION = "s";
    private static AtomicLong counter = new AtomicLong(NO_SEQUENCE + 1);
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
        this.sequence = counter.getAndIncrement();
        this.text = text;
        this.selection = selection;
    }

    private EditorState(@Nonnull JSONObject json) {
        this(json.optString(JSON_TEXT), json.optInt(JSON_SELECTION));
    }

    private EditorState(Parcel in) {
        sequence = NO_SEQUENCE;
        selection = in.readInt();
        textString = in.readString();
        text = textString;
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

    @Override
    public String toString() {
        return "EditorState{" +
                "sequence=" + sequence +
                ", text=" + text +
                ", selection=" + selection +
                '}';
    }

    @Nonnull
    public JSONObject toJson() throws JSONException {
        final JSONObject json = new JSONObject();
        json.put(JSON_TEXT, getTextString());
        json.put(JSON_SELECTION, selection);
        return json;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(selection);
        dest.writeString(textString);
    }
}
