package org.solovyev.android.calculator;

import org.junit.Before;
import org.junit.Test;

/**
 * User: Solovyev_S
 * Date: 15.10.12
 * Time: 12:30
 */
public class CalculatorImplTest extends AbstractCalculatorTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testAnsVariable() throws Exception {
		CalculatorTestUtils.assertError("ans");
		CalculatorTestUtils.assertError("ans");
		CalculatorTestUtils.assertEval("2", "2");
		CalculatorTestUtils.assertEval("2", "ans");
		CalculatorTestUtils.assertEval("4", "ans^2");
		CalculatorTestUtils.assertEval("16", "ans^2");
		CalculatorTestUtils.assertEval("0", "0");
		CalculatorTestUtils.assertEval("0", "ans");
		CalculatorTestUtils.assertEval("3", "3");
		CalculatorTestUtils.assertEval("9", "ans*ans");
		CalculatorTestUtils.assertError("ans*an");
		CalculatorTestUtils.assertEval("81", "ans*ans");
	}
}
