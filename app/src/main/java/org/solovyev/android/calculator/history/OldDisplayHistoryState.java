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

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Transient;
import org.solovyev.android.calculator.jscl.JsclOperation;

import javax.annotation.Nonnull;

@Root(name = "DisplayHistoryState")
class OldDisplayHistoryState implements Cloneable {

    @Transient
    private boolean valid = true;

    @Element
    @Nonnull
    private OldEditorHistoryState editorState;

    @Element
    @Nonnull
    private JsclOperation jsclOperation;

    private OldDisplayHistoryState() {
        // for xml
    }

    public boolean isValid() {
        return valid;
    }

    @Nonnull
    public OldEditorHistoryState getEditorState() {
        return editorState;
    }

    @Nonnull
    public JsclOperation getJsclOperation() {
        return jsclOperation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OldDisplayHistoryState that = (OldDisplayHistoryState) o;

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
                "editorHistoryState=" + editorState +
                ", jsclOperation=" + jsclOperation +
                '}';
    }

    @Override
    protected OldDisplayHistoryState clone() {
        try {
            final OldDisplayHistoryState clone = (OldDisplayHistoryState) super.clone();

            clone.editorState = this.editorState.clone();

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
