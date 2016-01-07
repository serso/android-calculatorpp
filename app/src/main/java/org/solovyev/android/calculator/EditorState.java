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

import javax.annotation.Nonnull;
import java.io.Serializable;

public class EditorState implements Serializable {

    @Nonnull
    private CharSequence text = "";

    private int selection = 0;

    private EditorState() {
    }

    public EditorState(@Nonnull EditorState state) {
        this.text = state.getText();
        this.selection = state.getSelection();
    }

    @Nonnull
    public static EditorState empty() {
        return new EditorState();
    }

    @Nonnull
    public static EditorState newSelection(@Nonnull EditorState state, int selection) {
        final EditorState result = new EditorState(state);
        result.selection = selection;
        return result;
    }

    @Nonnull
    public static EditorState create(@Nonnull CharSequence text, int selection) {
        final EditorState result = new EditorState();
        result.text = text;
        result.selection = selection;
        return result;
    }

    @Nonnull
    public String getText() {
        return this.text.toString();
    }

    @Nonnull
    public CharSequence getTextAsCharSequence() {
        return this.text;
    }

    public int getSelection() {
        return this.selection;
    }
}
