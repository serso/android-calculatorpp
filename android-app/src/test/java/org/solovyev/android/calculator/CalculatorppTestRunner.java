package org.solovyev.android.calculator;

import com.xtremelabs.robolectric.RobolectricConfig;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.runners.model.InitializationError;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * User: serso
 * Date: 10/7/12
 * Time: 5:36 PM
 */
public class CalculatorppTestRunner extends RobolectricTestRunner {

	public CalculatorppTestRunner(@Nonnull Class<?> testClass) throws InitializationError {
		super(testClass, new File("."));
	}

	@Override
	public void setupApplicationState(RobolectricConfig robolectricConfig) {
		try {
			CalculatorTestUtils.staticSetUp();
		} catch (Exception e) {
			throw new AssertionError(e);
		}
		super.setupApplicationState(robolectricConfig);
	}
}
