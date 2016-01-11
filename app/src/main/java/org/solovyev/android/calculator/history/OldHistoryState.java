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
import org.solovyev.android.calculator.Display;
import org.solovyev.android.calculator.DisplayState;
import org.solovyev.android.calculator.Editor;
import org.solovyev.android.calculator.EditorState;

import javax.annotation.Nonnull;

@Root(name = "HistoryState")
public class OldHistoryState extends OldBaseHistoryState {

    @Element
    @Nonnull
    private OldEditorHistoryState editorState;

    @Element
    @Nonnull
    private OldDisplayHistoryState displayState;

    private OldHistoryState() {
        // for xml
    }

    private OldHistoryState(@Nonnull OldEditorHistoryState editorState,
                            @Nonnull OldDisplayHistoryState displayState) {
        this.editorState = editorState;
        this.displayState = displayState;
    }

    @Nonnull
    public static OldHistoryState create(@Nonnull Editor editor,
                                      @Nonnull Display display) {
        return create(editor.getState(), display.getState());
    }

    @Nonnull
    public static OldHistoryState create(@Nonnull EditorState editorState,
                                      @Nonnull DisplayState displayState) {
        return new OldHistoryState(OldEditorHistoryState.create(editorState), OldDisplayHistoryState.newInstance(displayState));
    }

    @Nonnull
    public OldEditorHistoryState getEditorState() {
        return editorState;
    }

    @Nonnull
    public OldDisplayHistoryState getDisplayState() {
        return displayState;
    }

    @Override
    public String toString() {
        return "HistoryState{" +
                "editorState=" + editorState +
                ", displayState=" + displayState +
                '}';
    }

    @Override
    protected OldHistoryState clone() {
        final OldHistoryState that = (OldHistoryState) super.clone();

        that.editorState = this.editorState.clone();
        that.displayState = this.displayState.clone();

        return that;
    }
}
