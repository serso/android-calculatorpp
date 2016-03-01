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
import android.view.ContextMenu;

import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.calculator.jscl.JsclOperation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.math.Generic;

public class DisplayState implements Parcelable, ContextMenu.ContextMenuInfo {

    public static final Creator<DisplayState> CREATOR = new Creator<DisplayState>() {
        @Override
        public DisplayState createFromParcel(Parcel in) {
            return new DisplayState(in);
        }

        @Override
        public DisplayState[] newArray(int size) {
            return new DisplayState[size];
        }
    };
    private static final String JSON_TEXT = "t";
    @Nonnull
    public final String text;
    public final boolean valid;
    public final long sequence;
    @Nonnull
    private transient JsclOperation operation = JsclOperation.numeric;
    @Nullable
    private transient Generic result;

    private DisplayState(@Nonnull String text, boolean valid, long sequence) {
        this.text = text;
        this.valid = valid;
        this.sequence = sequence;
    }

    DisplayState(@Nonnull JSONObject json) {
        this(json.optString(JSON_TEXT), true, Calculator.NO_SEQUENCE);
    }

    private DisplayState(Parcel in) {
        text = in.readString();
        valid = in.readByte() != 0;
        sequence = Calculator.NO_SEQUENCE;
    }

    @Nonnull
    public static DisplayState empty() {
        return new DisplayState("", true, Calculator.NO_SEQUENCE);
    }

    @Nonnull
    public static DisplayState create(@Nonnull JSONObject json) {
        return new DisplayState(json);
    }

    @Nonnull
    public static DisplayState createError(@Nonnull JsclOperation operation,
                                           @Nonnull String errorMessage,
                                           long sequence) {
        final DisplayState state = new DisplayState(errorMessage, false, sequence);
        state.operation = operation;
        return state;
    }

    @Nonnull
    public static DisplayState createValid(@Nonnull JsclOperation operation,
                                           @Nullable Generic result,
                                           @Nonnull String stringResult,
                                           long sequence) {
        final DisplayState state = new DisplayState(stringResult, true, sequence);
        state.result = result;
        state.operation = operation;
        return state;
    }

    @Nullable
    public Generic getResult() {
        return this.result;
    }

    @Nonnull
    public JsclOperation getOperation() {
        return this.operation;
    }

    public boolean same(@Nonnull DisplayState that) {
        return TextUtils.equals(text, that.text) && operation == that.operation;
    }

    @Nonnull
    public JSONObject toJson() throws JSONException {
        final JSONObject json = new JSONObject();
        json.put(JSON_TEXT, text);
        return json;
    }

    @Override
    public String toString() {
        return "DisplayState{" +
                "valid=" + valid +
                ", sequence=" + sequence +
                ", operation=" + operation +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeByte((byte) (valid ? 1 : 0));
    }

    public boolean isEmpty() {
        return valid && TextUtils.isEmpty(text);
    }
}
