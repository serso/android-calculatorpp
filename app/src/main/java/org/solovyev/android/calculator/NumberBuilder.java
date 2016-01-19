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

import android.text.SpannableStringBuilder;
import jscl.MathContext;
import jscl.MathEngine;
import jscl.NumeralBase;
import jscl.math.numeric.Real;
import jscl.text.*;
import org.solovyev.android.calculator.math.MathType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 10/23/11
 * Time: 2:57 PM
 */
public class NumberBuilder extends BaseNumberBuilder {

    public NumberBuilder(@Nonnull Engine engine) {
        super(engine);
    }

    private static int replaceNumberInText(@Nonnull SpannableStringBuilder sb,
                                           @Nullable String number,
                                           int trimmedChars,
                                           @Nonnull NumeralBase nb,
                                           @Nonnull final MathEngine engine) {
        if (number == null) {
            return 0;
        }
        // in any case remove old number from text
        final int oldNumberLength = number.length() + trimmedChars;
        sb.delete(sb.length() - oldNumberLength, sb.length());

        final String newNumber = formatNumber(number, nb, engine);
        sb.append(newNumber);
        // offset between old number and new number
        return newNumber.length() - oldNumberLength;
    }

    @Nonnull
    private static String formatNumber(@Nonnull String number, @Nonnull NumeralBase nb, @Nonnull MathEngine engine) {
        String result;

        int indexOfDot = number.indexOf('.');

        if (indexOfDot < 0) {
            int indexOfE;
            if (nb == NumeralBase.hex) {
                indexOfE = -1;
            } else {
                indexOfE = number.indexOf(MathType.POWER_10);
            }
            if (indexOfE < 0) {
                result = engine.addGroupingSeparators(nb, number);
            } else {
                final String partBeforeE;
                if (indexOfE != 0) {
                    partBeforeE = engine.addGroupingSeparators(nb, number.substring(0, indexOfE));
                } else {
                    partBeforeE = "";
                }
                result = partBeforeE + number.substring(indexOfE);
            }
        } else {
            final String integerPart;
            if (indexOfDot != 0) {
                integerPart = engine.addGroupingSeparators(nb, number.substring(0, indexOfDot));
            } else {
                integerPart = "";
            }
            result = integerPart + number.substring(indexOfDot);
        }

        return result;
    }

    @Nonnull
    private static Double toDouble(@Nonnull String s, @Nonnull NumeralBase nb, @Nonnull final MathContext mc) throws NumberFormatException {
        final NumeralBase defaultNb = mc.getNumeralBase();
        try {
            mc.setNumeralBase(nb);

            try {
                return JsclIntegerParser.parser.parse(Parser.Parameters.newInstance(s, new MutableInt(0), mc), null).content().doubleValue();
            } catch (ParseException e) {
                try {
                    return ((Real) DoubleParser.parser.parse(Parser.Parameters.newInstance(s, new MutableInt(0), mc), null).content()).doubleValue();
                } catch (ParseException e1) {
                    throw new NumberFormatException();
                }
            }

        } finally {
            mc.setNumeralBase(defaultNb);
        }
    }

    /**
     * Method replaces number in text according to some rules (e.g. formatting)
     *
     * @param sb     text where number can be replaced
     * @param result math type result of current token
     * @return offset between new number length and old number length (newNumberLength - oldNumberLength)
     */
    @Override
    public int process(@Nonnull SpannableStringBuilder sb, @Nonnull MathType.Result result) {
        if (canContinue(result)) {
            // let's continue building number
            if (numberBuilder == null) {
                // if new number => create new builder
                numberBuilder = new StringBuilder();
            }

            if (result.type != MathType.numeral_base) {
                // just add matching string
                numberBuilder.append(result.match);
            } else {
                // set explicitly numeral base (do not include it into number)
                nb = NumeralBase.getByPrefix(result.match);
            }
            return 0;
        } else {
            // process current number (and go to the next one)
            return processNumber(sb);
        }
    }

    /**
     * Method replaces number in text according to some rules (e.g. formatting)
     *
     * @param sb text where number can be replaced
     * @return offset between new number length and old number length (newNumberLength - oldNumberLength)
     */
    public int processNumber(@Nonnull SpannableStringBuilder sb) {
        // total number of trimmed chars
        int trimmedChars = 0;

        String number = null;

        // toXml numeral base (as later it might be replaced)
        final NumeralBase localNb = getNumeralBase();

        if (numberBuilder != null) {
            try {
                number = numberBuilder.toString();

                // let's get rid of unnecessary characters (grouping separators, + after E)
                final List<String> tokens = new ArrayList<String>();
                tokens.addAll(MathType.grouping_separator.getTokens());
                // + after E can be omitted: 10+E = 10E (NOTE: - cannot be omitted )
                tokens.add("+");
                for (String groupingSeparator : tokens) {
                    final String trimmedNumber = number.replace(groupingSeparator, "");
                    trimmedChars += number.length() - trimmedNumber.length();
                    number = trimmedNumber;
                }

                // check if number still valid
                toDouble(number, getNumeralBase(), engine.getMathEngine());

            } catch (NumberFormatException e) {
                // number is not valid => stop
                number = null;
            }

            numberBuilder = null;

            // must set default numeral base (exit numeral base mode)
            nb = engine.getNumeralBase();
        }

        return replaceNumberInText(sb, number, trimmedChars, localNb, engine.getMathEngine());
    }
}
