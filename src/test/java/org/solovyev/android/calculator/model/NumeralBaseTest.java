package org.solovyev.android.calculator.model;

import au.com.bytecode.opencsv.CSVReader;
import jscl.JsclMathEngine;
import jscl.MathEngine;
import jscl.math.Expression;
import jscl.text.ParseException;
import jscl.util.ExpressionGenerator;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.utils.Converter;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 12/14/11
 * Time: 4:16 PM
 */
public class NumeralBaseTest {

	@BeforeClass
	public static void setUp() throws Exception {
		CalculatorEngine.instance.init(null, null);
		CalculatorEngine.instance.setPrecision(3);
		CalculatorEngine.instance.setThreadKiller(new CalculatorEngine.ThreadKillerImpl());
	}

	@Test
	public void testConversion() throws Exception {
		CSVReader reader = null;
		try {
			final MathEngine me = JsclMathEngine.instance;

			reader = new CSVReader(new InputStreamReader(NumeralBaseTest.class.getResourceAsStream("/jscl/math/nb_table.csv")), '\t');

			// skip first line
			reader.readNext();

			String[] line = reader.readNext();
			for (; line != null; line = reader.readNext()) {
				testExpression(line, new DummyExpression());
				testExpression(line, new Expression1());
				testExpression(line, new Expression2());
				testExpression(line, new Expression3());

				final String dec = line[0];
				final String hex = "0x:" + line[1];
				final String bin = "0b:" + line[2];

				final List<String> input = new ArrayList<String>();
				input.add(dec);
				input.add(hex);
				input.add(bin);

				System.out.println("Dec: " + dec);
				System.out.println("Hex: " + hex);
				System.out.println("Bin: " + bin);

				final ExpressionGenerator eg = new ExpressionGenerator(input, 20);
				final List<String> expressions = eg.generate();

				final String decExpression = expressions.get(0);
				final String hexExpression = expressions.get(1);
				final String binExpression = expressions.get(2);

				System.out.println("Dec expression: " + decExpression);
				System.out.println("Hex expression: " + hexExpression);
				System.out.println("Bin expression: " + binExpression);

				final String decResult = Expression.valueOf(decExpression).numeric().toString();
				System.out.println("Dec result: " + decResult);

				final String hexResult = Expression.valueOf(hexExpression).numeric().toString();
				System.out.println("Hex result: " + hexResult);

				final String binResult = Expression.valueOf(binExpression).numeric().toString();
				System.out.println("Bin result: " + binResult);

				Assert.assertEquals("dec-hex: " + decExpression + " : " + hexExpression, decResult, hexResult);
				Assert.assertEquals("dec-bin: " + decExpression + " : " + binExpression, decResult, binResult);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	public static void testExpression(@NotNull String[] line, @NotNull Converter<String, String> converter) throws ParseException, CalculatorEvalException, CalculatorParseException {
		final String dec = line[0];
		final String hex = "0x:" + line[1];
		final String bin = "0b:" + line[2];

		final String decExpression = converter.convert(dec);
		final String decResult = CalculatorEngine.instance.evaluate(JsclOperation.numeric, decExpression).getResult();
		final String hexExpression = converter.convert(hex);
		final String hexResult = CalculatorEngine.instance.evaluate(JsclOperation.numeric, hexExpression).getResult();
		final String binExpression = converter.convert(bin);
		final String binResult = CalculatorEngine.instance.evaluate(JsclOperation.numeric, binExpression).getResult();

		Assert.assertEquals("dec-hex: " + decExpression + " : " + hexExpression, decResult, hexResult);
		Assert.assertEquals("dec-bin: " + decExpression + " : " + binExpression, decResult, binResult);
	}

	private static class DummyExpression implements Converter<String, String> {

		@NotNull
		@Override
		public String convert(@NotNull String s) {
			return s;
		}
	}

	private static class Expression1 implements Converter<String, String> {

		@NotNull
		@Override
		public String convert(@NotNull String s) {
			return s + "*" + s;
		}
	}

	private static class Expression2 implements Converter<String, String> {

		@NotNull
		@Override
		public String convert(@NotNull String s) {
			return s + "*" + s + " * sin(" + s + ") - 0b:1101";
		}
	}

	private static class Expression3 implements Converter<String, String> {

		@NotNull
		@Override
		public String convert(@NotNull String s) {
			return s + "*" + s + " * sin(" + s + ") - 0b:1101 + √(" + s + ") + exp ( " + s + ")";
		}
	}
}
