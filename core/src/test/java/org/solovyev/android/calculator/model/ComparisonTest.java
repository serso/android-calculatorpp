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

package org.solovyev.android.calculator.model;

import org.junit.BeforeClass;
import org.junit.Test;
import org.solovyev.android.calculator.AbstractCalculatorTest;
import org.solovyev.android.calculator.CalculatorTestUtils;
import org.solovyev.android.calculator.Locator;


/**
 * User: serso
 * Date: 9/17/11
 * Time: 9:47 PM
 */

@SuppressWarnings("deprecation")
public class ComparisonTest extends AbstractCalculatorTest {

	@BeforeClass
	public static void staticSetUp() throws Exception {
		CalculatorTestUtils.staticSetUp();
		Locator.getInstance().getEngine().setPrecision(3);
	}

	@Test
	public void testComparisonFunction() throws Exception {
		CalculatorTestUtils.assertEval("0", "eq(0, 1)");
		CalculatorTestUtils.assertEval("1", "eq(1, 1)");
		CalculatorTestUtils.assertEval("1", "eq(1, 1.0)");
		CalculatorTestUtils.assertEval("0", "eq(1, 1.000000000000001)");
		CalculatorTestUtils.assertEval("0", "eq(1, 0)");

		CalculatorTestUtils.assertEval("1", "lt(0, 1)");
		CalculatorTestUtils.assertEval("0", "lt(1, 1)");
		CalculatorTestUtils.assertEval("0", "lt(1, 0)");

		CalculatorTestUtils.assertEval("0", "gt(0, 1)");
		CalculatorTestUtils.assertEval("0", "gt(1, 1)");
		CalculatorTestUtils.assertEval("1", "gt(1, 0)");

		CalculatorTestUtils.assertEval("1", "ne(0, 1)");
		CalculatorTestUtils.assertEval("0", "ne(1, 1)");
		CalculatorTestUtils.assertEval("1", "ne(1, 0)");

		CalculatorTestUtils.assertEval("1", "le(0, 1)");
		CalculatorTestUtils.assertEval("1", "le(1, 1)");
		CalculatorTestUtils.assertEval("0", "le(1, 0)");

		CalculatorTestUtils.assertEval("0", "ge(0, 1)");
		CalculatorTestUtils.assertEval("1", "ge(1, 1)");
		CalculatorTestUtils.assertEval("1", "ge(1, 0)");

		CalculatorTestUtils.assertEval("0", "ap(0, 1)");
		CalculatorTestUtils.assertEval("1", "ap(1, 1)");
		CalculatorTestUtils.assertEval("0", "ap(1, 0)");

	}
}
