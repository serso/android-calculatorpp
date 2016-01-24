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

import jscl.AngleUnit;
import jscl.text.msg.Messages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * User: serso
 * Date: 11/17/12
 * Time: 7:30 PM
 */
public enum CalculatorFixableError implements FixableError {

    must_be_rad(Messages.msg_23, Messages.msg_24, Messages.msg_25) {
        @Override
        public void fix() {
            Locator.getInstance().getPreferenceService().setAngleUnits(AngleUnit.rad);
        }
    },

    preferred_numeral_base() {
        @Override
        public void fix() {
            Locator.getInstance().getPreferenceService().setPreferredNumeralBase();
        }
    },

    preferred_angle_units() {
        @Override
        public void fix() {
            Locator.getInstance().getPreferenceService().setPreferredAngleUnits();
        }
    };

    @Nonnull
    private final List<String> messageCodes;

    CalculatorFixableError(@Nullable String... messageCodes) {
        this.messageCodes = messageCodes == null || messageCodes.length == 0 ? java.util.Collections.<String>emptyList() : Arrays.asList(messageCodes);
    }

    @Nullable
    public static CalculatorFixableError getErrorByMessageCode(@Nonnull String messageCode) {
        for (CalculatorFixableError fixableError : values()) {
            if (fixableError.messageCodes.contains(messageCode)) {
                return fixableError;
            }
        }
        return null;
    }


    @Nullable
    @Override
    public CharSequence getFixCaption() {
        return null;
    }
}
