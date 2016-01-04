package org.solovyev.android.calculator.text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 6/27/13
 * Time: 8:07 PM
 */
public final class TextProcessorEditorResult implements CharSequence {

    @Nonnull
    private final CharSequence charSequence;
    private final int offset;
    @Nullable
    private String string;

    public TextProcessorEditorResult(@Nonnull CharSequence charSequence, int offset) {
        this.charSequence = charSequence;
        this.offset = offset;
    }

    @Override
    public int length() {
        return charSequence.length();
    }

    @Override
    public char charAt(int i) {
        return charSequence.charAt(i);
    }

    @Override
    public CharSequence subSequence(int i, int i1) {
        return charSequence.subSequence(i, i1);
    }

    @Nonnull
    @Override
    public String toString() {
        if (string == null) {
            string = charSequence.toString();
        }
        return string;
    }

    @Nonnull
    public CharSequence getCharSequence() {
        return charSequence;
    }

    public int getOffset() {
        return offset;
    }
}
