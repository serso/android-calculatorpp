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
import android.util.Log;
import android.view.ContextMenu;
import android.widget.EditText;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.onscreen.CalculatorOnscreenService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;

public class EditorView extends EditText {

    @Nonnull
    private static final String TAG = App.subTag("EditorView");

    private boolean reportChanges;
    @Nullable
    private Method setShowSoftInputOnFocusMethod;

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
        addTextChangedListener(new MyTextWatcher());
        setShowSoftInputOnFocusCompat(false);
        // changes should only be reported after the view has been set up completely, i.e. now
        reportChanges = true;
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);
        menu.removeItem(android.R.id.selectAll);
    }

    public void setState(@Nonnull final EditorState state) {
        Check.isMainThread();
        // we don't want to be notified about changes we make ourselves
        reportChanges = false;
        if (App.getTheme().light && getContext() instanceof CalculatorOnscreenService) {
            // don't need formatting
            setText(state.getTextString());
        } else {
            setText(state.text, BufferType.EDITABLE);
        }
        setSelection(Editor.clamp(state.selection, length()));
        reportChanges = true;
    }

    @Override
    protected void onSelectionChanged(int start, int end) {
        Check.isMainThread();
        if (!reportChanges) {
            return;
        }
        // external text change => need to notify editor
        super.onSelectionChanged(start, end);

        if (start == end) {
            // only if cursor moving, if selection do nothing
            Locator.getInstance().getEditor().setSelection(start);
        }
    }

    public void setShowSoftInputOnFocusCompat(boolean show) {
        Check.isMainThread();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setShowSoftInputOnFocus(show);
        } else {
            setShowSoftInputOnFocusPreLollipop(show);
        }
    }

    private void setShowSoftInputOnFocusPreLollipop(boolean show) {
        try {
            if (setShowSoftInputOnFocusMethod == null) {
                setShowSoftInputOnFocusMethod = EditText.class.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocusMethod.setAccessible(true);
            }
            setShowSoftInputOnFocusMethod.invoke(this, show);
        } catch (Exception e) {
            Log.w(TAG, e.getMessage(), e);
        }
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
            if (!reportChanges) {
                return;
            }
            // external text change => need to notify editor
            Locator.getInstance().getEditor().setText(String.valueOf(s));
        }
    }
}
