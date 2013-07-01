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

import jscl.math.function.IConstant;

import org.junit.Before;
import org.junit.Test;
import org.solovyev.android.calculator.model.Var;

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
