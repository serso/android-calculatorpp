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
import org.solovyev.android.calculator.DisplayState;
import org.solovyev.android.calculator.Editor;
import org.solovyev.android.calculator.EditorState;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Root
public class EditorHistoryState implements Cloneable {

    @Element
    private int cursorPosition;

    @Element(required = false)
    @Nullable
    private String text = "";

    private EditorHistoryState() {
        // for xml
    }

    @Nonnull
    public static EditorHistoryState create(@Nonnull EditorState state) {
        final EditorHistoryState result = new EditorHistoryState();

        result.text = state.getTextString();
        result.cursorPosition = state.selection;

        return result;
    }

    @Nonnull
    public static EditorHistoryState create(@Nonnull DisplayState viewState) {
        final EditorHistoryState result = new EditorHistoryState();

        result.text = viewState.getText();
        result.cursorPosition = viewState.getSelection();

        return result;
    }

    public void setValuesFromHistory(@Nonnull Editor editor) {
        editor.setText(Strings.getNotEmpty(this.getText(), ""));
        editor.setSelection(this.getCursorPosition());
    }

    @Nullable
    public String getText() {
        return text;
    }

    public int getCursorPosition() {
        return cursorPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EditorHistoryState)) return false;

        EditorHistoryState that = (EditorHistoryState) o;

        if (cursorPosition != that.cursorPosition) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = cursorPosition;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EditorHistoryState{" +
                "cursorPosition=" + cursorPosition +
                ", text='" + text + '\'' +
                '}';
    }

    @Override
    protected EditorHistoryState clone() {
        try {
            return (EditorHistoryState) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
