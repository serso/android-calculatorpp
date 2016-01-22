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

package org.solovyev.android.calculator.text;

import jscl.math.Generic;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.math.MathType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class FromJsclSimplifyTextProcessor implements TextProcessor<String, Generic> {

    public static final FromJsclSimplifyTextProcessor instance = new FromJsclSimplifyTextProcessor();
    private final List<MathType> mathTypes = Arrays.asList(MathType.function, MathType.constant);

    public FromJsclSimplifyTextProcessor() {
    }

    @Nonnull
    @Override
    public String process(@Nonnull Generic from) {
        return removeMultiplicationSigns(from.toString());
    }

    public String process(@Nonnull String s) {
        return removeMultiplicationSigns(s);
    }

    @Nonnull
    private String removeMultiplicationSigns(String s) {
        final StringBuilder sb = new StringBuilder();

        MathType.Result mathTypeBefore;
        MathType.Result mathType = null;
        MathType.Result mathTypeAfter = null;

        for (int i = 0; i < s.length(); i++) {
            mathTypeBefore = mathType;
            if (mathTypeAfter == null) {
                mathType = MathType.getType(s, i, false);
            } else {
                mathType = mathTypeAfter;
            }

            char ch = s.charAt(i);
            if (ch == '*') {
                if (i + 1 < s.length()) {
                    mathTypeAfter = MathType.getType(s, i + 1, false);
                } else {
                    mathTypeAfter = null;
                }

                if (needMultiplicationSign(mathTypeBefore == null ? null : mathTypeBefore.type, mathTypeAfter == null ? null : mathTypeAfter.type)) {
                    sb.append(Locator.getInstance().getEngine().getMultiplicationSign());
                }

            } else {
                if (mathType.type == MathType.constant || mathType.type == MathType.function || mathType.type == MathType.operator) {
                    sb.append(mathType.match);
                    i += mathType.match.length() - 1;
                } else {
                    sb.append(ch);
                }
                mathTypeAfter = null;
            }

        }

        return sb.toString();
    }

    private boolean needMultiplicationSign(@Nullable MathType mathTypeBefore, @Nullable MathType mathTypeAfter) {
        if (mathTypeBefore == null || mathTypeAfter == null) {
            return true;
        } else if (mathTypes.contains(mathTypeBefore) || mathTypes.contains(mathTypeAfter)) {
            return false;
        } else if (mathTypeBefore == MathType.close_group_symbol) {
            return false;
        } else if (mathTypeAfter == MathType.open_group_symbol) {
            return false;
        }

        return true;
    }

}
