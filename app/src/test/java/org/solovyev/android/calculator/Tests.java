package org.solovyev.android.calculator;

import android.support.annotation.NonNull;
import org.solovyev.android.calculator.calculations.CalculationFailedEvent;
import org.solovyev.android.calculator.calculations.CalculationFinishedEvent;
import org.solovyev.common.msg.Message;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.verify;
import static org.solovyev.android.calculator.jscl.JsclOperation.numeric;

public class Tests {

    @NonNull
    public static Executor sameThreadExecutor() {
        return new Executor() {
            @Override
            public void execute(@NonNull Runnable command) {
                command.run();
            }
        };
    }

    static void assertError(@NonNull Calculator calculator, @NonNull String expression) {
        calculator.evaluate(numeric, expression);
        verify(calculator.bus).post(refEq(new CalculationFailedEvent(numeric, expression, 0, new Exception()), "exception", "sequence"));
    }

    static void assertEval(@NonNull Calculator calculator, @NonNull String expression, @NonNull String expected) {
        calculator.evaluate(numeric, expression);
        verify(calculator.bus).post(refEq(new CalculationFinishedEvent(numeric, expression, 0, null, expected, new ArrayList<Message>()), "result", "sequence"));
    }
}
