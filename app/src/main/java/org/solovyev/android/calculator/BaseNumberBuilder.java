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
import com.google.common.base.Strings;
import jscl.NumeralBase;
import org.solovyev.android.calculator.math.MathType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BaseNumberBuilder {

    @Nonnull
    protected final Engine engine;

    @Nullable
    protected StringBuilder numberBuilder = null;

    @Nullable
    protected NumeralBase nb;

    protected BaseNumberBuilder(@Nonnull Engine engine) {
        this.engine = engine;
        this.nb = engine.getMathEngine().getNumeralBase();
    }

    /**
     * Method determines if we can continue to process current number
     *
     * @param result current math type result
     * @return true if we can continue of processing of current number, if false - new number should be constructed
     */
    protected boolean canContinue(@Nonnull MathType.Result result) {
        final boolean number = result.type.getGroupType() == MathType.MathGroupType.number;
        return number && !spaceBefore(result) &&
                numeralBaseCheck(result) &&
                numeralBaseInTheStart(result.type)
                || isSignAfterE(result);
    }

    private boolean spaceBefore(@Nonnull MathType.Result mathTypeResult) {
        return numberBuilder == null && Strings.isNullOrEmpty(mathTypeResult.match.trim());
    }

    private boolean numeralBaseInTheStart(@Nonnull MathType result) {
        return result != MathType.numeral_base || numberBuilder == null;
    }

    private boolean numeralBaseCheck(@Nonnull MathType.Result result) {
        return result.type != MathType.digit || getNumeralBase().getAcceptableCharacters().contains(result.match.charAt(0));
    }

    private boolean isSignAfterE(@Nonnull MathType.Result mathTypeResult) {
        if (isHexMode()) {
            return false;
        }
        final String match = mathTypeResult.match;
        if (!"−".equals(match) && !"-".equals(match) && !"+".equals(match)) {
            return false;
        }
        final StringBuilder nb = numberBuilder;
        if (nb == null || nb.length() == 0) {
            return false;
        }
        return nb.charAt(nb.length() - 1) == MathType.EXPONENT;
    }

    public boolean isHexMode() {
        return getNumeralBase() == NumeralBase.hex;
    }

    @Nonnull
    protected NumeralBase getNumeralBase() {
        return nb == null ? engine.getMathEngine().getNumeralBase() : nb;
    }

    public abstract int process(@Nonnull SpannableStringBuilder sb, @Nonnull MathType.Result result);
}
