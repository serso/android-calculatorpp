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

package org.solovyev.android.calculator.buttons;

import org.solovyev.android.Check;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

// see values/text_glyphs.xml for glyph constants
public enum CppSpecialButton {

    history("history"),
    history_undo("↶", '\ue007'),
    history_redo("↷", '\ue008'),
    cursor_right("▷"),
    cursor_to_end(">>"),
    cursor_left("◁"),
    cursor_to_start("<<"),
    settings("settings"),
    settings_widget("settings_widget"),
    like("like"),
    memory("memory"),
    memory_plus("M+"),
    memory_minus("M-"),
    memory_clear("MC"),
    erase("erase"),
    paste("paste", '\uE000'),
    copy("copy", '\uE001'),
    brackets_wrap("(…)"),
    equals("="),
    clear("clear"),
    functions("functions"),
    function_add("+ƒ"),
    var_add("+π"),
    plot_add("+plot", '\uE009'),
    open_app("open_app"),
    vars("vars"),
    operators("operators"),
    simplify("≡");

    @Nonnull
    private static final Map<String, CppSpecialButton> buttonsByActions = new HashMap<>();
    @Nonnull
    private static final CppSpecialButton[] buttonsByGlyphs = new CppSpecialButton[values().length];
    private static final char FIRST_GLYPH = '\uE000';

    @Nonnull
    public final String action;
    public final char glyph;

    CppSpecialButton(@Nonnull String action) {
        this(action, (char) 0);
    }

    CppSpecialButton(@Nonnull String action, char glyph) {
        this.action = action;
        this.glyph = glyph;
    }

    @Nullable
    public static CppSpecialButton getByAction(@Nonnull String action) {
        initButtonsByActions();
        return buttonsByActions.get(action);
    }

    private static void initButtonsByActions() {
        Check.isMainThread();
        if (!buttonsByActions.isEmpty()) {
            return;
        }
        for (CppSpecialButton button : values()) {
            buttonsByActions.put(button.action, button);
        }
    }

    @Nullable
    public static CppSpecialButton getByGlyph(char glyph) {
        initButtonsByGlyphs();
        final int position = glyphToPosition(glyph);
        if (position < 0 || position >= buttonsByGlyphs.length) {
            return null;
        }
        return buttonsByGlyphs[position];
    }

    private static int glyphToPosition(char glyph) {
        return glyph - FIRST_GLYPH;
    }

    private static void initButtonsByGlyphs() {
        Check.isMainThread();
        if (buttonsByGlyphs[0] != null) {
            return;
        }
        for (CppSpecialButton button : values()) {
            if(button.glyph == 0) {
                continue;
            }
            final int position = glyphToPosition(button.glyph);
            Check.isNull(buttonsByGlyphs[position], "Glyph is already taken, glyph=" + button.glyph);
            buttonsByGlyphs[position] = button;
        }
    }

    @Nonnull
    public String getAction() {
        return action;
    }
}
