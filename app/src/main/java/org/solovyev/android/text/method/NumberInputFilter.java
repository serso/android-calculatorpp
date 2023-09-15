package org.solovyev.android.text.method;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

public class NumberInputFilter implements InputFilter {

    private static final int[] CHARS = new int[]{-1, -1, -1};
    private static final int CHAR_SIGN = 0;
    private static final int CHAR_POINT = 1;
    private static final int CHAR_EXP = 2;

    private static final char[] ACCEPTED = {'E', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '+', '.'};
    private static final NumberInputFilter INSTANCE = new NumberInputFilter();

    private static boolean isSignChar(final char c) {
        return c == '-' || c == '+';
    }

    private static boolean isDecimalPointChar(final char c) {
        return c == '.';
    }

    private static boolean isExponentChar(final char c) {
        return c == 'E';
    }

    /**
     * Returns a NumberInputFilter that accepts the digits 0 through 9.
     */
    public static NumberInputFilter getInstance() {
        return INSTANCE;
    }

    private static boolean accepted(char c) {
        for (int i = ACCEPTED.length - 1; i >= 0; i--) {
            if (ACCEPTED[i] == c) {
                return true;
            }
        }

        return false;
    }

    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        final CharSequence out = filterIllegalCharacters(source, start, end);
        if (out != null) {
            source = out;
            start = 0;
            end = out.length();
        }

        CHARS[CHAR_SIGN] = -1;
        CHARS[CHAR_POINT] = -1;
        CHARS[CHAR_EXP] = -1;
        findChars(dest, 0, dstart, 0, CHARS);
        findChars(dest, dend, dest.length(), end - start, CHARS);

        SpannableStringBuilder filtered = null;
        for (int i = start; i < end; i++) {
            final char c = source.charAt(i);

            boolean filter = false;
            if (isSignChar(c)) {
                if (i == start && dstart == 0) {
                    if (CHARS[CHAR_SIGN] >= 0) {
                        filter = true;
                    } else {
                        CHARS[CHAR_SIGN] = i + dstart;
                    }
                } else if (CHARS[CHAR_EXP] == i + dstart - 1) {
                    // allow sign after exponent symbol
                    filter = false;
                } else {
                    filter = true;
                }
            } else if (isDecimalPointChar(c)) {
                if (CHARS[CHAR_POINT] >= 0) {
                    filter = true;
                } else if (CHARS[CHAR_EXP] >= 0 && CHARS[CHAR_EXP] < i + dstart) {
                    // no decimal point after exponent
                    filter = true;
                } else {
                    CHARS[CHAR_POINT] = i + dstart;
                }
            } else if (isExponentChar(c)) {
                if (CHARS[CHAR_EXP] >= 0) {
                    filter = true;
                } else if (CHARS[CHAR_POINT] >= 0 && CHARS[CHAR_POINT] > i + dstart) {
                    // no exponent before decimal point
                    filter = true;
                } else if (i + dstart == 0) {
                    // exponent can't be first
                    filter = true;
                } else {
                    CHARS[CHAR_EXP] = i + dstart;
                }
            }

            if (filter) {
                if (end == start + 1) {
                    return "";  // Only one character, and it was stripped.
                }

                if (filtered == null) {
                    filtered = new SpannableStringBuilder(source, start, end);
                }

                filtered.delete(i - start, i + 1 - start);
            }
        }

        if (filtered != null) {
            return filtered;
        } else if (out != null) {
            return out;
        } else {
            return null;
        }
    }

    private void findChars(@NonNull Spanned s, int start, int end, int offset, int[] out) {
        for (int i = start; i < end; i++) {
            final char c = s.charAt(i);

            if (isSignChar(c)) {
                if (out[CHAR_SIGN] == -1 && out[CHAR_EXP] == -1) {
                    // count in only signs before exponent
                    out[CHAR_SIGN] = i + offset;
                }
            } else if (isDecimalPointChar(c)) {
                if (out[CHAR_POINT] == -1) {
                    out[CHAR_POINT] = i + offset;
                }
            } else if (isExponentChar(c)) {
                if (out[CHAR_EXP] == -1) {
                    out[CHAR_EXP] = i + offset;
                }
            }
        }
    }

    @Nullable
    private CharSequence filterIllegalCharacters(CharSequence source, int start, int end) {
        final int illegal = findIllegalChar(source, start, end);
        if (illegal == end) {
            // all OK
            return null;
        }
        if (end - start == 1) {
            // it was not OK, and there is only one char, so nothing remains.
            return "";
        }

        final SpannableStringBuilder filtered = new SpannableStringBuilder(source, start, end);
        final int newEnd = end - start - 1;
        // only count down to "illegal" because the chars before that were all OK.
        final int newIllegal = illegal - start;
        for (int j = newEnd; j >= newIllegal; j--) {
            if (!accepted(source.charAt(j))) {
                filtered.delete(j, j + 1);
            }
        }
        return filtered;
    }

    private int findIllegalChar(CharSequence s, int start, int end) {
        for (int i = start; i < end; i++) {
            if (!accepted(s.charAt(i))) {
                return i;
            }
        }
        return end;
    }
}
