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

import au.com.bytecode.opencsv.CSVReader;
import jscl.JsclMathEngine;
import jscl.MathEngine;
import jscl.math.Expression;
import jscl.util.ExpressionGeneratorWithInput;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solovyev.android.calculator.BaseCalculatorTest;
import org.solovyev.android.calculator.ParseException;
import org.solovyev.common.Converter;

import javax.annotation.Nonnull;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NumeralBaseTest extends BaseCalculatorTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        engine.getMathEngine().setPrecision(3);
    }

    public void testExpression(@Nonnull String[] line, @Nonnull Converter<String, String> converter) throws jscl.text.ParseException, ParseException {
        final String dec = line[0].toUpperCase();
        final String hex = "0x:" + line[1].toUpperCase();
        final String bin = "0b:" + line[2].toUpperCase();

        final String decExpression = converter.convert(dec);
        final String decResult = engine.getMathEngine().evaluate(decExpression);
        final String hexExpression = converter.convert(hex);
        final String hexResult = engine.getMathEngine().evaluate(hexExpression);
        final String binExpression = converter.convert(bin);
        final String binResult = engine.getMathEngine().evaluate(binExpression);

        Assert.assertEquals("dec-hex: " + decExpression + " : " + hexExpression, decResult, hexResult);
        Assert.assertEquals("dec-bin: " + decExpression + " : " + binExpression, decResult, binResult);
    }

    @Test
    public void testConversion() throws Exception {
        CSVReader reader = null;
        try {
            final MathEngine me = JsclMathEngine.getInstance();

            reader = new CSVReader(new InputStreamReader(NumeralBaseTest.class.getResourceAsStream("/org/solovyev/android/calculator/model/nb_table.csv")), '\t');

            // skip first line
            reader.readNext();

            String[] line = reader.readNext();
            for (; line != null; line = reader.readNext()) {
                testExpression(line, new DummyExpression());
                testExpression(line, new Expression1());
                testExpression(line, new Expression2());
                testExpression(line, new Expression3());

                final String dec = line[0].toUpperCase();
                final String hex = "0x:" + line[1].toUpperCase();
                final String bin = "0b:" + line[2].toUpperCase();

                final List<String> input = new ArrayList<String>();
                input.add(dec);
                input.add(hex);
                input.add(bin);

                //System.out.println("Dec: " + dec);
                //System.out.println("Hex: " + hex);
                //System.out.println("Bin: " + bin);

                final ExpressionGeneratorWithInput eg = new ExpressionGeneratorWithInput(input, 20);
                final List<String> expressions = eg.generate();

                final String decExpression = expressions.get(0);
                final String hexExpression = expressions.get(1);
                final String binExpression = expressions.get(2);

                //System.out.println("Dec expression: " + decExpression);
                //System.out.println("Hex expression: " + hexExpression);
                //System.out.println("Bin expression: " + binExpression);

                final String decResult = Expression.valueOf(decExpression).numeric().toString();
                //System.out.println("Dec result: " + decResult);

                final String hexResult = Expression.valueOf(hexExpression).numeric().toString();
                //System.out.println("Hex result: " + hexResult);

                final String binResult = Expression.valueOf(binExpression).numeric().toString();
                //System.out.println("Bin result: " + binResult);

                Assert.assertEquals("dec-hex: " + decExpression + " : " + hexExpression, decResult, hexResult);
                Assert.assertEquals("dec-bin: " + decExpression + " : " + binExpression, decResult, binResult);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private static class DummyExpression implements Converter<String, String> {

        @Nonnull
        @Override
        public String convert(@Nonnull String s) {
            return s;
        }
    }

    private static class Expression1 implements Converter<String, String> {

        @Nonnull
        @Override
        public String convert(@Nonnull String s) {
            return s + "*" + s;
        }
    }

    private static class Expression2 implements Converter<String, String> {

        @Nonnull
        @Override
        public String convert(@Nonnull String s) {
            return s + "*" + s + " * sin(" + s + ") - 0b:1101";
        }
    }

    private static class Expression3 implements Converter<String, String> {

        @Nonnull
        @Override
        public String convert(@Nonnull String s) {
            return s + "*" + s + " * sin(" + s + ") - 0b:1101 + √(" + s + ") + exp ( " + s + ")";
        }
    }
}
