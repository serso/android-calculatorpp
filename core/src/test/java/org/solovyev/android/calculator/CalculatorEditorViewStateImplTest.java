package org.solovyev.android.calculator;

import junit.framework.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 10/20/12
 * Time: 12:31 PM
 */
public class CalculatorEditorViewStateImplTest {

    @Test
    public void testSerialization() throws Exception {
        CalculatorTestUtils.testSerialization(CalculatorEditorViewStateImpl.newDefaultInstance());

        CalculatorEditorViewState out = CalculatorTestUtils.testSerialization(CalculatorEditorViewStateImpl.newInstance("treter", 2));
        Assert.assertEquals(2, out.getSelection());
        Assert.assertEquals("treter", out.getText());
    }
}
