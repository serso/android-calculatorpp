package org.solovyev.android.calculator;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.squareup.otto.Bus;
import org.hamcrest.Description;
import org.junit.Before;
import org.mockito.ArgumentMatcher;
import org.solovyev.android.calculator.calculations.CalculationFailedEvent;
import org.solovyev.android.calculator.calculations.CalculationFinishedEvent;
import org.solovyev.android.calculator.jscl.JsclOperation;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.solovyev.android.calculator.jscl.JsclOperation.numeric;

public abstract class BaseCalculatorTest {
    protected Calculator calculator;
    protected Bus bus;
    protected Engine engine;

    @Before
    public void setUp() throws Exception {
        bus = mock(Bus.class);
        calculator = new Calculator(mock(SharedPreferences.class), bus, Tests.sameThreadExecutor());
        engine = Tests.makeEngine();
        engine.variablesRegistry.bus = bus;
        calculator.engine = engine;
        final ToJsclTextProcessor processor = new ToJsclTextProcessor();
        processor.engine = engine;
        calculator.preprocessor = processor;
    }

    protected final void assertError(@NonNull String expression) {
        calculator.evaluate(numeric, expression, 0);
        verify(calculator.bus, atLeastOnce()).post(argThat(failed()));
    }

    @NonNull
    private static ArgumentMatcher<CalculationFailedEvent> failed() {
        return new ArgumentMatcher<CalculationFailedEvent>() {
            @Override
            public boolean matches(Object o) {
                if (!(o instanceof CalculationFailedEvent)) {
                    return false;
                }
                final CalculationFailedEvent e = (CalculationFailedEvent) o;
                return e.operation == numeric;
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
        return argThat(finished(expected, expression, operation));
    }

    protected static CalculationFinishedEvent anyFinishedEvent() {
        return argThat(new ArgumentMatcher<CalculationFinishedEvent>() {
            @Override
            public boolean matches(Object o) {
                return o instanceof CalculationFinishedEvent;
            }
        });
    }

    @NonNull
    protected static ArgumentMatcher<CalculationFinishedEvent> finished(@NonNull final String expected, @NonNull final String expression, final JsclOperation operation) {
        return new ArgumentMatcher<CalculationFinishedEvent>() {
            @Override
            public boolean matches(Object o) {
                if (!(o instanceof CalculationFinishedEvent)) {
                    return false;
                }
                final CalculationFinishedEvent e = (CalculationFinishedEvent) o;
                return e.operation == operation && e.expression.equals(expression) && e.stringResult.equals(expected);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(expected);
            }
        };
    }
}
