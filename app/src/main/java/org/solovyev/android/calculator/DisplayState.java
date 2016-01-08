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

import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.text.Strings;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.math.Generic;

public class DisplayState implements Serializable {

    @Nonnull
    private JsclOperation operation = JsclOperation.numeric;

    @Nullable
    private transient Generic result;

    @Nullable
    private String stringResult = "";

    private boolean valid = true;

    @Nullable
    private String errorMessage;

    private int selection = 0;

    private DisplayState() {
    }

    @Nonnull
    public static DisplayState empty() {
        return new DisplayState();
    }

    @Nonnull
    public static DisplayState createError(@Nonnull JsclOperation operation,
                                           @Nonnull String errorMessage) {
        final DisplayState calculatorDisplayState = new DisplayState();
        calculatorDisplayState.valid = false;
        calculatorDisplayState.errorMessage = errorMessage;
        calculatorDisplayState.operation = operation;
        return calculatorDisplayState;
    }

    @Nonnull
    public static DisplayState createValid(@Nonnull JsclOperation operation,
                                           @Nullable Generic result,
                                           @Nonnull String stringResult,
                                           int selection) {
        final DisplayState calculatorDisplayState = new DisplayState();
        calculatorDisplayState.valid = true;
        calculatorDisplayState.result = result;
        calculatorDisplayState.stringResult = stringResult;
        calculatorDisplayState.operation = operation;
        calculatorDisplayState.selection = selection;

        return calculatorDisplayState;
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
}
