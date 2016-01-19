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

package org.solovyev.android.calculator.jscl;


import jscl.MathEngine;
import jscl.math.Generic;
import jscl.text.ParseException;
import org.solovyev.android.calculator.text.DummyTextProcessor;
import org.solovyev.android.calculator.text.FromJsclSimplifyTextProcessor;
import org.solovyev.android.calculator.text.TextProcessor;

import javax.annotation.Nonnull;

public enum JsclOperation {

    simplify,
    elementary,
    numeric;

    JsclOperation() {
    }


    @Nonnull
    public TextProcessor<String, Generic> getFromProcessor() {
        switch (this) {
            case simplify:
                return FromJsclSimplifyTextProcessor.instance;
            case elementary:
                return DummyTextProcessor.instance;
            case numeric:
                return FromJsclNumericTextProcessor.instance;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Nonnull
    public final String evaluate(@Nonnull String expression, @Nonnull MathEngine engine) throws ParseException {
        switch (this) {
            case simplify:
                return engine.simplify(expression);
            case elementary:
                return engine.elementary(expression);
            case numeric:
                return engine.evaluate(expression);
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Nonnull
    public final Generic evaluateGeneric(@Nonnull String expression, @Nonnull MathEngine engine) throws ParseException {
        switch (this) {
            case simplify:
                return engine.simplifyGeneric(expression);
            case elementary:
                return engine.elementaryGeneric(expression);
            case numeric:
                return engine.evaluateGeneric(expression);
            default:
                throw new UnsupportedOperationException();
        }
    }


}
