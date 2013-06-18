package org.solovyev.android.calculator.plot;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.junit.Test;

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
