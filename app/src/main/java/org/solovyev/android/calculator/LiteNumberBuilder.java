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

import org.solovyev.android.calculator.math.MathType;

import javax.annotation.Nonnull;

import jscl.NumeralBase;

/**
 * User: serso
 * Date: 12/15/11
 * Time: 8:33 PM
 */

public class LiteNumberBuilder extends BaseNumberBuilder {

    public LiteNumberBuilder(@Nonnull Engine engine) {
        super(engine);
        this.nb = engine.getNumeralBase();
    }

    @Override
    public int process(@Nonnull SpannableStringBuilder sb, @Nonnull MathType.Result result) {
        process(result);
        return 0;
    }

    public void process(@Nonnull MathType.Result result) {
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

        } else {
            // process current number (and go to the next one)
            if (numberBuilder != null) {
                numberBuilder = null;

                // must set default numeral base (exit numeral base mode)
                nb = engine.getNumeralBase();
            }
        }
    }

}

