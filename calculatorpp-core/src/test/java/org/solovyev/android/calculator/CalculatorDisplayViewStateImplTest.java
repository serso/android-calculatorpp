package org.solovyev.android.calculator;

import jscl.math.Expression;
import org.junit.Test;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: serso
 * Date: 10/20/12
 * Time: 12:24 PM
 */
public class CalculatorDisplayViewStateImplTest {

    @Test
    public void testSerializable() throws Exception {
        CalculatorTestUtils.testSerialization(CalculatorDisplayViewStateImpl.newValidState(JsclOperation.numeric, null, "test", 3));
        CalculatorTestUtils.testSerialization(CalculatorDisplayViewStateImpl.newValidState(JsclOperation.numeric, Expression.valueOf("3"), "test", 3));
        CalculatorTestUtils.testSerialization(CalculatorDisplayViewStateImpl.newValidState(JsclOperation.simplify, Expression.valueOf("3+3"), "test", 3));
        CalculatorTestUtils.testSerialization(CalculatorDisplayViewStateImpl.newDefaultInstance());
        CalculatorTestUtils.testSerialization(CalculatorDisplayViewStateImpl.newErrorState(JsclOperation.numeric, "ertert"));
    }

}
