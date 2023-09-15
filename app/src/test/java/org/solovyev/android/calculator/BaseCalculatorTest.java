package org.solovyev.android.calculator;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.solovyev.android.calculator.jscl.JsclOperation.numeric;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import com.squareup.otto.Bus;
import java.util.Collections;
import org.junit.Before;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.solovyev.android.calculator.calculations.CalculationFailedEvent;
import org.solovyev.android.calculator.calculations.CalculationFinishedEvent;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.msg.Message;

public abstract class BaseCalculatorTest {
    protected Calculator calculator;
    protected Bus bus;
    protected Engine engine;

    @Before
    public void setUp() throws Exception {
        bus = mock(Bus.class);
        calculator = new Calculator(mock(SharedPreferences.class), bus);
        calculator.setSynchronous();
        engine = Tests.makeEngine();
        engine.variablesRegistry.bus = bus;
        calculator.engine = engine;
        final ToJsclTextProcessor processor = new ToJsclTextProcessor();
        processor.engine = engine;
        calculator.preprocessor = processor;
    }

    protected final void assertError(@NonNull String expression) {
        calculator.evaluate(numeric, expression, 0);
        verify(calculator.bus, atLeastOnce()).post(ArgumentMatchers.argThat(failed()));
    }

    @NonNull
    private static ArgumentMatcher<CalculationFailedEvent> failed() {
        return new ArgumentMatcher<CalculationFailedEvent>() {
            @Override
            public boolean matches(CalculationFailedEvent o) {
                return o.operation == numeric;
            }
        };
    }

    protected final void assertEval(@NonNull String expected, @NonNull String expression) {
        assertEval(expected, expression, numeric);
    }

    protected final void assertEval(@NonNull final String expected, @NonNull final String expression, final JsclOperation operation) {
        calculator.evaluate(operation, expression, 0);
        verify(calculator.bus, atLeastOnce()).post(finishedEvent(expected, expression, operation));
    }

    protected static CalculationFinishedEvent finishedEvent(@NonNull String expected, @NonNull String expression, JsclOperation operation) {
        return ArgumentMatchers.argThat(finished(expected, expression, operation));
    }

    protected static CalculationFinishedEvent anyFinishedEvent() {
        return ArgumentMatchers.argThat(new ArgumentMatcher<CalculationFinishedEvent>() {
            @Override
            public boolean matches(CalculationFinishedEvent o) {
                return true;
            }
        });
    }

    @NonNull
    protected static ArgumentMatcher<CalculationFinishedEvent> finished(@NonNull final String expected, @NonNull final String expression, final JsclOperation operation) {
        return new ArgumentMatcher<CalculationFinishedEvent>() {
            @Override
            public boolean matches(CalculationFinishedEvent e) {
                return e.expression.equals(expression) && e.stringResult.equals(expected);
            }

            @Override
            public String toString() {
                return new CalculationFinishedEvent(operation, expression, 0, null, expected,
                        Collections.<Message>emptyList()).toString();
            }
        };
    }
}
