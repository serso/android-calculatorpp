package org.solovyev.android.calculator;

import jscl.JsclMathEngine;
import junit.framework.Assert;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mockito.Mockito;
import org.solovyev.android.calculator.external.CalculatorExternalListenersContainer;
import org.solovyev.android.calculator.history.CalculatorHistory;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.plot.CalculatorPlotter;

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * User: serso
 * Date: 10/7/12
 * Time: 8:40 PM
 */
public class CalculatorTestUtils {

    // in seconds
    public static final int TIMEOUT = 3;

    public static void staticSetUp() throws Exception {
        Locator.getInstance().init(new CalculatorImpl(), newCalculatorEngine(), Mockito.mock(CalculatorClipboard.class), Mockito.mock(CalculatorNotifier.class), Mockito.mock(CalculatorHistory.class), new SystemOutCalculatorLogger(), Mockito.mock(CalculatorPreferenceService.class), Mockito.mock(CalculatorKeyboard.class), Mockito.mock(CalculatorExternalListenersContainer.class), Mockito.mock(CalculatorPlotter.class));
        Locator.getInstance().getEngine().init();
    }

    @NotNull
    static CalculatorEngineImpl newCalculatorEngine() {
        final MathEntityDao mathEntityDao = Mockito.mock(MathEntityDao.class);

        final JsclMathEngine jsclEngine = JsclMathEngine.getInstance();

        final CalculatorVarsRegistry varsRegistry = new CalculatorVarsRegistry(jsclEngine.getConstantsRegistry(), mathEntityDao);
        final CalculatorFunctionsMathRegistry functionsRegistry = new CalculatorFunctionsMathRegistry(jsclEngine.getFunctionsRegistry(), mathEntityDao);
        final CalculatorOperatorsMathRegistry operatorsRegistry = new CalculatorOperatorsMathRegistry(jsclEngine.getOperatorsRegistry(), mathEntityDao);
        final CalculatorPostfixFunctionsRegistry postfixFunctionsRegistry = new CalculatorPostfixFunctionsRegistry(jsclEngine.getPostfixFunctionsRegistry(), mathEntityDao);

        return new CalculatorEngineImpl(jsclEngine, varsRegistry, functionsRegistry, operatorsRegistry, postfixFunctionsRegistry, null);
    }

    public static void assertEval(@NotNull String expected, @NotNull String expression) {
        assertEval(expected, expression, JsclOperation.numeric);
    }

    public static void assertEval(@NotNull String expected, @NotNull String expression, @NotNull JsclOperation operation) {
        final Calculator calculator = Locator.getInstance().getCalculator();

        Locator.getInstance().getDisplay().setViewState(CalculatorDisplayViewStateImpl.newDefaultInstance());

        final CountDownLatch latch = new CountDownLatch(1);
        final TestCalculatorEventListener calculatorEventListener = new TestCalculatorEventListener(latch);
        try {
            calculator.addCalculatorEventListener(calculatorEventListener);

            calculatorEventListener.setCalculatorEventData(calculator.evaluate(operation, expression));

            if (latch.await(TIMEOUT, TimeUnit.SECONDS)) {
                Assert.assertNotNull(calculatorEventListener.getResult());
                Assert.assertEquals(expected, calculatorEventListener.getResult().getText());
            } else {
                Assert.fail("Too long wait for: " + expression);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            calculator.removeCalculatorEventListener(calculatorEventListener);
        }
    }

    public static void assertError(@NotNull String expression) {
        assertError(expression, JsclOperation.numeric);
    }

    public static <S extends Serializable> S testSerialization(@NotNull S serializable) throws IOException, ClassNotFoundException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(out);
            oos.writeObject(serializable);
        } finally {
            if (oos != null) {
                oos.close();
            }
        }

        byte[] serialized = out.toByteArray();

        Assert.assertTrue(serialized.length > 0);


        final ObjectInputStream resultStream = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
        final S result = (S) resultStream.readObject();

        Assert.assertNotNull(result);

        return result;
    }

    private static final class TestCalculatorEventListener implements CalculatorEventListener {

        @Nullable
        private CalculatorEventData calculatorEventData;

        @NotNull
        private final CountDownLatch latch;

        @Nullable
        private volatile CalculatorDisplayViewState result = null;

        public TestCalculatorEventListener(@NotNull CountDownLatch latch) {
            this.latch = latch;
        }

        public void setCalculatorEventData(@Nullable CalculatorEventData calculatorEventData) {
            this.calculatorEventData = calculatorEventData;
        }

        @Override
        public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
            if (this.calculatorEventData != null && calculatorEventData.isSameSequence(this.calculatorEventData)) {
                if (calculatorEventType == CalculatorEventType.display_state_changed) {
                    final CalculatorDisplayChangeEventData displayChange = (CalculatorDisplayChangeEventData) data;

                    result = displayChange.getNewValue();

                    try {
                        // need to sleep a little bit as await
                        new CountDownLatch(1).await(100, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                    }
                    latch.countDown();
                }
            }
        }

        @Nullable
        public CalculatorDisplayViewState getResult() {
            return result;
        }
    }

    public static void assertError(@NotNull String expression, @NotNull JsclOperation operation) {
        final Calculator calculator = Locator.getInstance().getCalculator();

        Locator.getInstance().getDisplay().setViewState(CalculatorDisplayViewStateImpl.newDefaultInstance());

        final CountDownLatch latch = new CountDownLatch(1);
        final TestCalculatorEventListener calculatorEventListener = new TestCalculatorEventListener(latch);
        try {
            calculator.addCalculatorEventListener(calculatorEventListener);
            calculatorEventListener.setCalculatorEventData(calculator.evaluate(operation, expression));

            if (latch.await(TIMEOUT, TimeUnit.SECONDS)) {
                Assert.assertNotNull(calculatorEventListener.getResult());
                Assert.assertFalse(calculatorEventListener.getResult().isValid());
            } else {
                Assert.fail("Too long wait for: " + expression);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            calculator.removeCalculatorEventListener(calculatorEventListener);
        }
    }
}
