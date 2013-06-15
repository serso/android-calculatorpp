package org.solovyev.android.calculator.history;

import junit.framework.Assert;
import javax.annotation.Nonnull;
import org.junit.BeforeClass;
import org.junit.Test;
import org.solovyev.android.calculator.CalculatorDisplayViewStateImpl;
import org.solovyev.android.calculator.CalculatorEditorViewStateImpl;
import org.solovyev.android.calculator.CalculatorTestUtils;
import org.solovyev.android.calculator.Locator;

import java.util.List;

/**
 * User: Solovyev_S
 * Date: 10.10.12
 * Time: 15:07
 */
public class CalculatorHistoryImplTest {

	@BeforeClass
	public static void setUp() throws Exception {
		CalculatorTestUtils.staticSetUp();
	}

	@Test
	public void testGetStates() throws Exception {
		CalculatorHistory calculatorHistory = new CalculatorHistoryImpl(Locator.getInstance().getCalculator());

		addState(calculatorHistory, "1");
		addState(calculatorHistory, "12");
		addState(calculatorHistory, "123");
		addState(calculatorHistory, "123+");
		addState(calculatorHistory, "123+3");
		addState(calculatorHistory, "");
		addState(calculatorHistory, "2");
		addState(calculatorHistory, "23");
		addState(calculatorHistory, "235");
		addState(calculatorHistory, "2355");
		addState(calculatorHistory, "235");
		addState(calculatorHistory, "2354");
		addState(calculatorHistory, "23547");

		final List<CalculatorHistoryState> states = calculatorHistory.getStates(false);
		Assert.assertEquals(2, states.size());
		Assert.assertEquals("23547", states.get(1).getEditorState().getText());
		Assert.assertEquals("123+3", states.get(0).getEditorState().getText());
	}

	private void addState(@Nonnull CalculatorHistory calculatorHistory, @Nonnull String text) {
		calculatorHistory.addState(CalculatorHistoryState.newInstance(CalculatorEditorViewStateImpl.newInstance(text, 3), CalculatorDisplayViewStateImpl.newDefaultInstance()));
	}
}
