package org.solovyev.android.calculator;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.jetbrains.annotations.NotNull;
import org.junit.runners.model.InitializationError;

import java.io.File;

/**
 * User: serso
 * Date: 10/7/12
 * Time: 5:36 PM
 */
public class CalculatorppTestRunner extends RobolectricTestRunner {

    public CalculatorppTestRunner(@NotNull Class<?> testClass) throws InitializationError {
        super(testClass, new File("calculatorpp"));
    }
}
