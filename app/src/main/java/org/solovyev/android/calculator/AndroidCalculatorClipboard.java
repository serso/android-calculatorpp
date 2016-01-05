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

import android.app.Application;
import android.content.Context;
import android.text.ClipboardManager;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 1:35 PM
 */
public class AndroidCalculatorClipboard implements CalculatorClipboard {

    @Nonnull
    private final Context context;

    public AndroidCalculatorClipboard(@Nonnull Application application) {
        this.context = application;
    }

    @Override
    public String getText() {
        final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasText()) {
            return String.valueOf(clipboard.getText());
        } else {
            return null;
        }
    }

    @Override
    public void setText(@Nonnull CharSequence text) {
        final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setText(text);
    }

    @Override
    public void setText(@Nonnull String text) {
        final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setText(text);
    }
}
