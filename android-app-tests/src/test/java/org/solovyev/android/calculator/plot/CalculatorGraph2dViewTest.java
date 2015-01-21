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

package org.solovyev.android.calculator.plot;

import org.junit.Test;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import static org.junit.Assert.assertEquals;

/**
 * User: serso
 * Date: 1/15/13
 * Time: 9:58 PM
 */
public class CalculatorGraph2dViewTest {

	@Test
	public void testFormatTick() throws Exception {
		assertEquals("23324", CalculatorGraph2dView.formatTick(23324.0f, 0));

		final DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
		final DecimalFormatSymbols decimalFormatSymbols = format.getDecimalFormatSymbols();
		if (',' == decimalFormatSymbols.getDecimalSeparator()) {
			assertEquals("23324,1", CalculatorGraph2dView.formatTick(23324.1f, 1));
		} else if ('.' == decimalFormatSymbols.getDecimalSeparator()) {
			assertEquals("23324.1", CalculatorGraph2dView.formatTick(23324.1f, 1));
		}
	}

	@Test
	public void testCountTickDigits() throws Exception {
		assertEquals(0, CalculatorGraph2dView.countTickDigits(1));
		assertEquals(0, CalculatorGraph2dView.countTickDigits(10));
		assertEquals(0, CalculatorGraph2dView.countTickDigits(100));
		assertEquals(1, CalculatorGraph2dView.countTickDigits(0.9f));
		assertEquals(1, CalculatorGraph2dView.countTickDigits(0.2f));
		assertEquals(1, CalculatorGraph2dView.countTickDigits(0.1f));
		assertEquals(2, CalculatorGraph2dView.countTickDigits(0.099f));
		assertEquals(3, CalculatorGraph2dView.countTickDigits(0.009f));
	}
}
