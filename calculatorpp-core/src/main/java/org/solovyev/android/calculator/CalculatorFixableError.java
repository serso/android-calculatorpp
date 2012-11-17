package org.solovyev.android.calculator;

import jscl.AngleUnit;
import jscl.text.msg.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.collections.CollectionsUtils;

import java.util.List;

/**
 * User: serso
 * Date: 11/17/12
 * Time: 7:30 PM
 */
public enum CalculatorFixableError {

    must_be_rad(Messages.msg_23, Messages.msg_24, Messages.msg_25) {
        @Override
        public void fix() {
            CalculatorLocatorImpl.getInstance().getPreferenceService().setAngleUnits(AngleUnit.rad);
        }
    };

    @NotNull
    private final List<String> messageCodes;

     CalculatorFixableError(@Nullable String... messageCodes) {
         this.messageCodes = CollectionsUtils.asList(messageCodes);
    }

    @Nullable
    public static CalculatorFixableError getErrorByMessageCode(@NotNull String messageCode) {
        for (CalculatorFixableError fixableError : values()) {
            if (fixableError.messageCodes.contains(messageCode)) {
                return fixableError;
            }
        }
        return null;
    }

    public abstract void fix();
}
