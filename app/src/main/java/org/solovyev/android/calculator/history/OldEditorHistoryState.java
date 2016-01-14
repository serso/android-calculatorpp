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
import org.solovyev.android.calculator.EditorState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Root(name = "EditorHistoryState")
class OldEditorHistoryState implements Cloneable {

    @Element
    private int cursorPosition;

    @Element(required = false)
    @Nullable
    private String text = "";

    private OldEditorHistoryState() {
        // for xml
    }

    @Nonnull
    public static OldEditorHistoryState create(@Nonnull EditorState state) {
        final OldEditorHistoryState result = new OldEditorHistoryState();

        result.text = state.getTextString();
        result.cursorPosition = state.selection;

        return result;
    }

    @Nullable
    public String getText() {
        return text;
    }

    public int getCursorPosition() {
        return cursorPosition;
    }
}
