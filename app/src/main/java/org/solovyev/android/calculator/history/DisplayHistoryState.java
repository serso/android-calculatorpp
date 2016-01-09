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

package org.solovyev.android.calculator.history;

import jscl.math.Generic;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Transient;
import org.solovyev.android.calculator.Display;
import org.solovyev.android.calculator.DisplayState;
import org.solovyev.android.calculator.EditorState;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Root
public class DisplayHistoryState implements Cloneable {

    @Transient
    private boolean valid = true;

    @Transient
    @Nullable
    private String errorMessage = null;

    @Element
    @Nonnull
    private EditorHistoryState editorState;

    @Element
    @Nonnull
    private JsclOperation jsclOperation;

    @Transient
    @Nullable
    private Generic genericResult;

    private DisplayHistoryState() {
        // for xml
    }

    @Nonnull
    public static DisplayHistoryState newInstance(@Nonnull DisplayState viewState) {
        final DisplayHistoryState result = new DisplayHistoryState();

        result.editorState = EditorHistoryState.create(viewState);

        result.valid = viewState.isValid();
        result.jsclOperation = viewState.getOperation();
        result.genericResult = viewState.getResult();
        result.errorMessage = viewState.getErrorMessage();

        return result;
    }

    public void setValuesFromHistory(@Nonnull Display display) {
        if (this.isValid()) {
            display.setState(DisplayState.createValid(this.getJsclOperation(), this.getGenericResult(), Strings.getNotEmpty(this.getEditorState().getText(), ""), this.getEditorState().getCursorPosition(), EditorState.NO_SEQUENCE));
        } else {
            display.setState(DisplayState.createError(this.getJsclOperation(), Strings.getNotEmpty(this.getErrorMessage(), ""), EditorState.NO_SEQUENCE));
        }
    }


    public boolean isValid() {
        return valid;
    }

    @Nonnull
    public EditorHistoryState getEditorState() {
        return editorState;
    }

    @Nonnull
    public JsclOperation getJsclOperation() {
        return jsclOperation;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    @Nullable
    public Generic getGenericResult() {
        return genericResult;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DisplayHistoryState that = (DisplayHistoryState) o;

        if (!editorState.equals(that.editorState)) return false;
        if (jsclOperation != that.jsclOperation) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = editorState.hashCode();
        result = 31 * result + jsclOperation.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CalculatorDisplayHistoryState{" +
                "valid=" + valid +
                ", errorMessage='" + errorMessage + '\'' +
                ", editorHistoryState=" + editorState +
                ", jsclOperation=" + jsclOperation +
                '}';
    }

    @Override
    protected DisplayHistoryState clone() {
        try {
            final DisplayHistoryState clone = (DisplayHistoryState) super.clone();

            clone.editorState = this.editorState.clone();

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
