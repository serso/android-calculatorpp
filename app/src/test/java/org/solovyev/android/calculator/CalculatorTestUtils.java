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

import android.content.Context;

import org.junit.Assert;
import org.mockito.Mockito;
import org.solovyev.android.calculator.history.CalculatorHistory;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.plot.CalculatorPlotter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormatSymbols;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.JsclMathEngine;

/**
 * User: serso
 * Date: 10/7/12
 * Time: 8:40 PM
 */
public class CalculatorTestUtils {

    // in seconds
    public static final int TIMEOUT = 3;

    public static void staticSetUp() throws Exception {
        Locator.getInstance().init(new CalculatorImpl(), newCalculatorEngine(), Mockito.mock(CalculatorClipboard.class), Mockito.mock(CalculatorNotifier.class), Mockito.mock(CalculatorHistory.class), new SystemOutCalculatorLogger(), Mockito.mock(CalculatorPreferenceService.class), Mockito.mock(CalculatorKeyboard.class), Mockito.mock(CalculatorPlotter.class), null);
        Locator.getInstance().getEngine().init();

        final DecimalFormatSymbols decimalGroupSymbols = new DecimalFormatSymbols();
        decimalGroupSymbols.setDecimalSeparator('.');
        decimalGroupSymbols.setGroupingSeparator(' ');
        Locator.getInstance().getEngine().setDecimalGroupSymbols(decimalGroupSymbols);
    }

    public static void staticSetUp(@Nullable Context context) throws Exception {
        Locator.getInstance().init(new CalculatorImpl(), newCalculatorEngine(), Mockito.mock(CalculatorClipboard.class), Mockito.mock(CalculatorNotifier.class), Mockito.mock(CalculatorHistory.class), new SystemOutCalculatorLogger(), Mockito.mock(CalculatorPreferenceService.class), Mockito.mock(CalculatorKeyboard.class), Mockito.mock(CalculatorPlotter.class), null);
        Locator.getInstance().getEngine().init();

        if (context != null) {
            initViews(context);
        }
    }

    public static void initViews(@Nonnull Context context) {
        final AndroidCalculatorEditorView editor = new AndroidCalculatorEditorView(context);
        editor.init();
        Locator.getInstance().getEditor().setView(editor);

        final AndroidCalculatorDisplayView display = new AndroidCalculatorDisplayView(context);
        display.init(context);
        Locator.getInstance().getDisplay().setView(display);
    }

    @Nonnull
    static CalculatorEngineImpl newCalculatorEngine() {
        final MathEntityDao mathEntityDao = Mockito.mock(MathEntityDao.class);

        final JsclMathEngine jsclEngine = JsclMathEngine.getInstance();

        final CalculatorVarsRegistry varsRegistry = new CalculatorVarsRegistry(jsclEngine.getConstantsRegistry(), mathEntityDao);
        final CalculatorFunctionsMathRegistry functionsRegistry = new CalculatorFunctionsMathRegistry(jsclEngine.getFunctionsRegistry(), mathEntityDao);
        final CalculatorOperatorsMathRegistry operatorsRegistry = new CalculatorOperatorsMathRegistry(jsclEngine.getOperatorsRegistry(), mathEntityDao);
        final CalculatorPostfixFunctionsRegistry postfixFunctionsRegistry = new CalculatorPostfixFunctionsRegistry(jsclEngine.getPostfixFunctionsRegistry(), mathEntityDao);

        return new CalculatorEngineImpl(jsclEngine, varsRegistry, functionsRegistry, operatorsRegistry, postfixFunctionsRegistry, null);
    }

    public static void assertEval(@Nonnull String expected, @Nonnull String expression) {
        assertEval(expected, expression, JsclOperation.numeric);
    }

    public static void assertEval(@Nonnull String expected, @Nonnull String expression, @Nonnull JsclOperation operation) {
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

    public static void assertError(@Nonnull String expression) {
        assertError(expression, JsclOperation.numeric);
    }

    public static <S extends Serializable> S testSerialization(@Nonnull S serializable) throws IOException, ClassNotFoundException {
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

    public static void assertError(@Nonnull String expression, @Nonnull JsclOperation operation) {
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

    private static final class TestCalculatorEventListener implements CalculatorEventListener {

        @Nonnull
        private final CountDownLatch latch;
        @Nullable
        private CalculatorEventData calculatorEventData;
        @Nullable
        private volatile CalculatorDisplayViewState result = null;

        public TestCalculatorEventListener(@Nonnull CountDownLatch latch) {
            this.latch = latch;
        }

        public void setCalculatorEventData(@Nullable CalculatorEventData calculatorEventData) {
            this.calculatorEventData = calculatorEventData;
        }

        @Override
        public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
            waitForEventData();

            if (calculatorEventData.isSameSequence(this.calculatorEventData)) {
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

        private void waitForEventData() {
            while (this.calculatorEventData == null) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                }
            }
        }

        @Nullable
        public CalculatorDisplayViewState getResult() {
            return result;
        }
    }
}
