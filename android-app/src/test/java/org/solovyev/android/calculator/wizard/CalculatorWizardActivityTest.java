package org.solovyev.android.calculator.wizard;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: serso
 * Date: 6/17/13
 * Time: 9:57 PM
 */
@RunWith(value = RobolectricTestRunner.class)
public class CalculatorWizardActivityTest {

	@Test
	public void testRestoreState() throws Exception {
		final ActivityController<CalculatorWizardActivity> controller = Robolectric.buildActivity(CalculatorWizardActivity.class);
		controller.attach();
		controller.create();

		final CalculatorWizardActivity activity = controller.get();
		assertNotNull(activity.getFlow());
		assertNotNull(activity.getStep());
		assertEquals(activity.getFlow().getFirstStep(), activity.getStep());
	}
}
