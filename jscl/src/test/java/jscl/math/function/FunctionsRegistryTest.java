package jscl.math.function;

import junit.framework.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 11/12/11
 * Time: 2:14 PM
 */
public class FunctionsRegistryTest {

    @Test
    public void testOrder() throws Exception {
        Function prev = null;
        for (Function function : FunctionsRegistry.getInstance().getEntities()) {
            if (prev != null) {
                Assert.assertTrue(prev.getName() + "<" + function.getName(), prev.getName().length() >= function.getName().length());
            }
            prev = function;
        }
    }
}
