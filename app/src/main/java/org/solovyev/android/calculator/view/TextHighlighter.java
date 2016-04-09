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

package org.solovyev.android.calculator.view;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import com.google.common.collect.Lists;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.android.calculator.text.TextProcessorEditorResult;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TextHighlighter implements TextProcessor<TextProcessorEditorResult, String> {

    private final int red;
    private final int green;
    private final int blue;
    private final boolean formatNumber;
    private final int dark;
    @Nonnull
    private final Engine engine;

    public TextHighlighter(int color, boolean formatNumber, @Nonnull Engine engine) {
        this.formatNumber = formatNumber;
        this.engine = engine;
        red = red(color);
        green = green(color);
        blue = blue(color);
        dark = isDark(red, green, blue) ? 1 : -1;
    }

    private static int blue(int color) {
        return color & 0xFF;
    }

    private static int green(int color) {
        return (color >> 8) & 0xFF;
    }

    private static int red(int color) {
        return (color >> 16) & 0xFF;
    }

    public static boolean isDark(int color) {
        return isDark(red(color), green(color), color & 0xFF);
    }

    public static boolean isDark(int red, int green, int blue) {
        final float y = 0.2126f * red + 0.7152f * green + 0.0722f * blue;
        return y < 128;
    }

    @Nonnull
    @Override
    public TextProcessorEditorResult process(@Nonnull String text) {
        final SpannableStringBuilder sb = new SpannableStringBuilder();
        final BaseNumberBuilder nb = !formatNumber ? new LiteNumberBuilder(engine) : new NumberBuilder(engine);
        final MathType.Result result = new MathType.Result();

        int offset = 0;
        int groupsCount = 0;
        int openGroupsCount = 0;

        for (int i = 0; i < text.length(); i++) {
            MathType.getType(text, i, nb.isHexMode(), result, engine);

            offset += nb.process(sb, result);

            final String match = result.match;
            switch (result.type) {
                case open_group_symbol:
                    openGroupsCount++;
                    groupsCount = Math.max(groupsCount, openGroupsCount);
                    sb.append(text.charAt(i));
                    break;
                case close_group_symbol:
                    openGroupsCount--;
                    sb.append(text.charAt(i));
                    break;
                case operator:
                    i += append(sb, match);
                    break;
                case function:
                    i += append(sb, match);
                    makeItalic(sb, i + 1 - match.length(), i + 1);
                    break;
                case constant:
                case numeral_base:
                    i += append(sb, match);
                    makeBold(sb, i + 1 - match.length(), i + 1);
                    break;
                default:
                    if (result.type == MathType.text || match.length() <= 1) {
                        sb.append(text.charAt(i));
                    } else {
                        i += append(sb, match);
                    }
            }
        }

        if (nb instanceof NumberBuilder) {
            offset += ((NumberBuilder) nb).processNumber(sb);
        }

        if (groupsCount == 0) {
            return new TextProcessorEditorResult(sb, offset);
        }
        final List<GroupSpan> groupSpans = new ArrayList<>(groupsCount);
        fillGroupSpans(sb, 0, 0, groupsCount, groupSpans);
        for (GroupSpan groupSpan : Lists.reverse(groupSpans)) {
            makeColor(sb, groupSpan.start, groupSpan.end, getColor(groupSpan.group, groupsCount));
        }
        return new TextProcessorEditorResult(sb, offset);
    }

    private int append(SpannableStringBuilder t, String match) {
        t.append(match);
        if (match.length() > 1) {
            return match.length() - 1;
        }
        return 0;
    }

    private static void makeItalic(@Nonnull SpannableStringBuilder t, int start, int end) {
        t.setSpan(new StyleSpan(Typeface.ITALIC), start, end, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static void makeBold(@Nonnull SpannableStringBuilder t, int start, int end) {
        t.setSpan(new StyleSpan(Typeface.BOLD), start, end, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static void makeColor(@Nonnull SpannableStringBuilder t, int start, int end, int color) {
        t.setSpan(new ForegroundColorSpan(color), start, end, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private int fillGroupSpans(@Nonnull SpannableStringBuilder sb, int start, int group, int groupsCount, @Nonnull List<GroupSpan> spans) {
        for (int i = start; i < sb.length(); i++) {
            final char c = sb.charAt(i);
            if (MathType.isOpenGroupSymbol(c)) {
                i = highlightGroup(sb, i, group + 1, groupsCount, spans);
            } else if (MathType.isCloseGroupSymbol(c)) {
                return i;
            }
        }

        return sb.length();
    }

    private int highlightGroup(SpannableStringBuilder sb, int start, int group, int groupsCount, @Nonnull List<GroupSpan> spans) {
        final int end = Math.min(sb.length(), fillGroupSpans(sb, start + 1, group, groupsCount, spans));
        if (start + 1 < end) {
            spans.add(new GroupSpan(start + 1, end, group));
        }
        return end;
    }

    private int getColor(int group, int groupsCount) {
        final int offset = (int) (dark * 255 * 0.6) * group / (groupsCount + 1);
        return (0xFF << 24) | ((red + offset) << 16) | ((green + offset) << 8) | (blue + offset);
    }

    private static class GroupSpan {
        final int start;
        final int end;
        final int group;

        private GroupSpan(int start, int end, int group) {
            Check.isTrue(start < end);
            this.start = start;
            this.end = end;
            this.group = group;
        }
    }
}
