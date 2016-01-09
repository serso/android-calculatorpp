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

import jscl.math.Generic;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DisplayState {

    @Nonnull
    private JsclOperation operation = JsclOperation.numeric;

    @Nullable
    private transient Generic result;

    @Nullable
    private String stringResult = "";

    private boolean valid = true;

    @Nullable
    private String errorMessage;

    private int selection;

    private long sequence;

    private DisplayState() {
    }

    @Nonnull
    public static DisplayState empty() {
        return new DisplayState();
    }

    @Nonnull
    public static DisplayState createError(@Nonnull JsclOperation operation,
                                           @Nonnull String errorMessage,
                                           long sequence) {
        final DisplayState state = new DisplayState();
        state.valid = false;
        state.errorMessage = errorMessage;
        state.operation = operation;
        state.sequence = sequence;
        return state;
    }

    @Nonnull
    public static DisplayState createValid(@Nonnull JsclOperation operation,
                                           @Nullable Generic result,
                                           @Nonnull String stringResult,
                                           int selection,
                                           long sequence) {
        final DisplayState state = new DisplayState();
        state.valid = true;
        state.result = result;
        state.stringResult = stringResult;
        state.operation = operation;
        state.selection = selection;
        state.sequence = sequence;
        return state;
    }

    @Nonnull
    public String getText() {
        return Strings.getNotEmpty(isValid() ? stringResult : errorMessage, "");
    }

    public int getSelection() {
        return selection;
    }

    @Nullable
    public Generic getResult() {
        return this.result;
    }

    public boolean isValid() {
        return this.valid;
    }

    @Nullable
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Nullable
    public String getStringResult() {
        return stringResult;
    }

    @Nonnull
    public JsclOperation getOperation() {
        return this.operation;
    }

    public long getSequence() {
        return sequence;
    }
}
