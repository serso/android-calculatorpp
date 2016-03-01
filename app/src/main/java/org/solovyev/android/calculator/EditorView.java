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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ContextMenu;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.floating.FloatingCalculatorService;
import org.solovyev.android.calculator.view.EditTextCompat;
import org.solovyev.android.views.Adjuster;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EditorView extends EditTextCompat {

    private boolean editorChange;
    @Nullable
    private Editor editor;

    public EditorView(Context context) {
        super(context);
        init();
    }

    public EditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EditorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        if (!isFloatingCalculator()) {
            Adjuster.adjustText(this, 0.22f,
                    getResources().getDimensionPixelSize(R.dimen.cpp_min_editor_text_size));
        }
        addTextChangedListener(new MyTextWatcher());
        dontShowSoftInputOnFocusCompat();
        // the state is controlled by Editor
        setSaveEnabled(false);
    }

    public void setEditor(@Nullable Editor editor) {
        if (this.editor == editor) {
            return;
        }
        this.editor = editor;
        if (editor != null) {
            setState(editor.getState());
        }
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);
        menu.removeItem(android.R.id.selectAll);
    }

    public void setState(@Nonnull final EditorState state) {
        Check.isMainThread();
        // we don't want to be notified about changes we make ourselves
        editorChange = true;
        if (App.getTheme().light && isFloatingCalculator()) {
            // don't need formatting
            setText(state.getTextString());
        } else {
            setText(state.text, BufferType.EDITABLE);
        }
        editorChange = false;
        setSelection(Editor.clamp(state.selection, length()));
    }

    private boolean isFloatingCalculator() {
        return getContext() instanceof FloatingCalculatorService;
    }

    @Override
    protected void onSelectionChanged(int start, int end) {
        Check.isMainThread();
        if (!editorChange) {
            return;
        }
        // external text change => need to notify editor
        super.onSelectionChanged(start, end);

        // only if cursor moving, if selection do nothing
        if (start != end) {
            return;
        }
        if (editor == null || editorChange) {
            return;
        }
        editor.setSelection(start);
    }

    private class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (editor == null || editorChange) {
                return;
            }
            editor.setText(String.valueOf(s));
        }
    }
}
