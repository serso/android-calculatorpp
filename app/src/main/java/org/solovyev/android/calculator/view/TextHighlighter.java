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

import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.android.calculator.text.TextProcessorEditorResult;
import org.solovyev.common.MutableObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * User: serso
 * Date: 10/12/11
 * Time: 9:47 PM
 */
public class TextHighlighter implements TextProcessor<TextProcessorEditorResult, String> {

    private final int red;
    private final int green;
    private final int blue;
    private final boolean formatNumber;
    private final boolean dark;

    public TextHighlighter(int color, boolean formatNumber) {
        this.formatNumber = formatNumber;
        //this.red = Color.red(baseColor);
        red = red(color);
        //this.green = Color.green(baseColor);
        green = green(color);
        //this.blue = Color.blue(baseColor);
        blue = blue(color);
        dark = isDark(red, green, blue);
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
    public TextProcessorEditorResult process(@Nonnull String text) throws CalculatorParseException {
        final CharSequence result;

        int maxNumberOfOpenGroupSymbols = 0;
        int numberOfOpenGroupSymbols = 0;

        final StringBuilder text1 = new StringBuilder(5 * text.length());

        int resultOffset = 0;

        final AbstractNumberBuilder numberBuilder;
        if (!formatNumber) {
            numberBuilder = new LiteNumberBuilder(Locator.getInstance().getEngine());
        } else {
            numberBuilder = new NumberBuilder(Locator.getInstance().getEngine());
        }
        for (int i = 0; i < text.length(); i++) {
            MathType.Result mathType = MathType.getType(text, i, numberBuilder.isHexMode());

            if (numberBuilder instanceof NumberBuilder) {
                final MutableObject<Integer> numberOffset = new MutableObject<>(0);
                ((NumberBuilder) numberBuilder).process(text1, mathType, numberOffset);
                resultOffset += numberOffset.getObject();
            } else {
                ((LiteNumberBuilder) numberBuilder).process(mathType);
            }

            final String match = mathType.getMatch();
            switch (mathType.getMathType()) {
                case open_group_symbol:
                    numberOfOpenGroupSymbols++;
                    maxNumberOfOpenGroupSymbols = Math.max(maxNumberOfOpenGroupSymbols, numberOfOpenGroupSymbols);
                    text1.append(text.charAt(i));
                    break;
                case close_group_symbol:
                    numberOfOpenGroupSymbols--;
                    text1.append(text.charAt(i));
                    break;
                case operator:
                    text1.append(match);
                    if (match.length() > 1) {
                        i += match.length() - 1;
                    }
                    break;
                case function:
                    i = processHighlightedText(text1, i, match, "i", null);
                    break;
                case constant:
                    i = processHighlightedText(text1, i, match, "b", null);
                    break;
                case numeral_base:
                    i = processHighlightedText(text1, i, match, "b", null);
                    break;
                default:
                    if (mathType.getMathType() == MathType.text || match.length() <= 1) {
                        text1.append(text.charAt(i));
                    } else {
                        text1.append(match);
                        i += match.length() - 1;
                    }
            }
        }

        if (numberBuilder instanceof NumberBuilder) {
            final MutableObject<Integer> numberOffset = new MutableObject<Integer>(0);
            ((NumberBuilder) numberBuilder).processNumber(text1, numberOffset);
            resultOffset += numberOffset.getObject();
        }

        if (maxNumberOfOpenGroupSymbols > 0) {

            final StringBuilder text2 = new StringBuilder(text1.length());

            int i = processBracketGroup(text2, text1, 0, 0, maxNumberOfOpenGroupSymbols);
            for (; i < text1.length(); i++) {
                text2.append(text1.charAt(i));
            }

            result = text2.toString();
        } else {
            result = text1.toString();
        }

        return new TextProcessorEditorResult(result, resultOffset);
    }

    private int processHighlightedText(@Nonnull StringBuilder result, int i, @Nonnull String match, @Nonnull String tag, @Nullable Map<String, String> tagAttributes) {
        result.append("<").append(tag);

        if (tagAttributes != null && !tagAttributes.entrySet().isEmpty()) {
            for (Map.Entry<String, String> entry : tagAttributes.entrySet()) {
                // attr1="attr1_value" attr2="attr2_value"
                result.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
            }
        }

        result.append(">").append(match).append("</").append(tag).append(">");
        if (match.length() > 1) {
            return i + match.length() - 1;
        } else {
            return i;
        }
    }

    private int processBracketGroup(@Nonnull StringBuilder result, @Nonnull CharSequence s, int i, int numberOfOpenings, int maxNumberOfGroups) {

        result.append("<font color=\"").append(getColor(maxNumberOfGroups, numberOfOpenings)).append("\">");

        for (; i < s.length(); i++) {
            char ch = s.charAt(i);
            String strCh = String.valueOf(ch);

            if (MathType.open_group_symbol.getTokens().contains(strCh)) {
                result.append(ch);
                result.append("</font>");
                i = processBracketGroup(result, s, i + 1, numberOfOpenings + 1, maxNumberOfGroups);
                result.append("<font color=\"").append(getColor(maxNumberOfGroups, numberOfOpenings)).append("\">");
                if (i < s.length() && MathType.close_group_symbol.getTokens().contains(String.valueOf(s.charAt(i)))) {
                    result.append(s.charAt(i));
                }
            } else if (MathType.close_group_symbol.getTokens().contains(strCh)) {
                break;
            } else {
                result.append(ch);
            }
        }

        result.append("</font>");


        return i;
    }

    private String getColor(int totalNumberOfOpenings, int numberOfOpenings) {
        int offset = ((int) (255 * 0.8)) * numberOfOpenings / (totalNumberOfOpenings + 1);
        if (!dark) {
            offset = -offset;
        }

        // for tests:
        // int result = Color.rgb(BASE_COLOUR_RED_COMPONENT - offset, BASE_COLOUR_GREEN_COMPONENT - offset, BASE_COLOUR_BLUE_COMPONENT - offset);
        int result = (0xFF << 24) | ((red + offset) << 16) | ((green + offset) << 8) | (blue + offset);

        return "#" + App.toColorString(result);
    }
}
