package jscl.math;

import au.com.bytecode.opencsv.CSVReader;
import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.MathEngine;
import junit.framework.Assert;
import org.junit.Test;

import java.io.InputStreamReader;

/**
 * User: serso
 * Date: 11/22/11
 * Time: 12:49 PM
 */
public class TrigonometricTest {

    // todo serso: due to conversion errors values on the borders are calculated not precisely
    /*
 0;0;1;0;Infinity
 90;1;0;1.633123935319537E16;0
 180;0;-1;0;-8.165619676597685E15
 270;-1;0;5.443746451065123E15;0
 360;0;1;0;-4.0828098382988425E15
      */

    @Test
    public void testValues() throws Exception {
        CSVReader reader = null;
        try {
            final MathEngine me = JsclMathEngine.getInstance();

            reader = new CSVReader(new InputStreamReader(TrigonometricTest.class.getResourceAsStream("./trig_table.csv")), '\t');

            // skip first line
            reader.readNext();

            String[] line = reader.readNext();
            for (; line != null; line = reader.readNext()) {
                final Integer degrees = Integer.valueOf(line[0]);

                final Double sinValue = Double.valueOf(line[1]);
                final Double cosValue = Double.valueOf(line[2]);
                final Double tgValue = Double.valueOf(line[3]);
                final Double ctgValue = Double.valueOf(line[4]);

                final Double radians = Double.valueOf(line[5]);

                final Double sinhValue = Double.valueOf(line[6]);
                final Double coshValue = Double.valueOf(line[7]);
                final Double tghValue = Double.valueOf(line[8]);
                final Double cthgValue = Double.valueOf(line[9]);

                final Double asinValue = Double.valueOf(line[10]);
                final Double acosValue = Double.valueOf(line[11]);
                final Double atanValue = Double.valueOf(line[12]);


                testValue(sinValue, Double.valueOf(me.evaluate("sin(" + degrees + "°)")), degrees);
                testValue(cosValue, Double.valueOf(me.evaluate("cos(" + degrees + "°)")), degrees);
                testValue(tgValue, Double.valueOf(me.evaluate("tan(" + degrees + "°)")), degrees);
                testValue(ctgValue, Double.valueOf(me.evaluate("cot(" + degrees + "°)")), degrees);

                testValue(sinhValue, Double.valueOf(me.evaluate("sinh(" + degrees + "°)")), degrees);
                testValue(coshValue, Double.valueOf(me.evaluate("cosh(" + degrees + "°)")), degrees);
                testValue(tghValue, Double.valueOf(me.evaluate("tanh(" + degrees + "°)")), degrees);
                testValue(cthgValue, Double.valueOf(me.evaluate("coth(" + degrees + "°)")), degrees);

                final AngleUnit angleUnits = me.getAngleUnits();
                try {
                    me.setAngleUnits(AngleUnit.rad);

                    testValue(sinValue, Double.valueOf(me.evaluate("sin(" + radians + ")")), degrees);
                    testValue(cosValue, Double.valueOf(me.evaluate("cos(" + radians + ")")), degrees);
                    testValue(tgValue, Double.valueOf(me.evaluate("tan(" + radians + ")")), degrees);
                    testValue(ctgValue, Double.valueOf(me.evaluate("cot(" + radians + ")")), degrees);

                    testValue(sinhValue, Double.valueOf(me.evaluate("sinh(" + radians + ")")), degrees);
                    testValue(coshValue, Double.valueOf(me.evaluate("cosh(" + radians + ")")), degrees);
                    testValue(tghValue, Double.valueOf(me.evaluate("tanh(" + radians + ")")), degrees);
                    testValue(cthgValue, Double.valueOf(me.evaluate("coth(" + radians + ")")), degrees);
                } finally {
                    me.setAngleUnits(angleUnits);
                }

                testValue(asinValue, Double.valueOf(me.evaluate("rad(asin(" + sinValue + "))")), degrees);
                testValue(acosValue, Double.valueOf(me.evaluate("rad(acos(" + cosValue + "))")), degrees);
                testValue(atanValue, Double.valueOf(me.evaluate("rad(atan(" + tgValue + "))")), degrees);

                // todo serso: check this
                //testValue((double)degrees, Double.valueOf(me.evaluate("asin(sin(" + degrees + "°))")), degrees);
                //testValue((double)degrees, Double.valueOf(me.evaluate("acos(cos(" + degrees + "°))")), degrees);
                //testValue((double)degrees, Double.valueOf(me.evaluate("atan(tan(" + degrees + "°))")), degrees);
                //testValue((double)degrees, Double.valueOf(me.evaluate("acot(cot(" + degrees + "°))")), degrees);

                testValue(sinValue, Double.valueOf(me.evaluate("sin(asin(sin(" + degrees + "°)))")), degrees);
                testValue(cosValue, Double.valueOf(me.evaluate("cos(acos(cos(" + degrees + "°)))")), degrees);
                testValue(tgValue, Double.valueOf(me.evaluate("tan(atan(tan(" + degrees + "°)))")), degrees);
                testValue(ctgValue, Double.valueOf(me.evaluate("cot(acot(cot(" + degrees + "°)))")), degrees);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private void testValue(Double expected, Double actual, Integer degrees) {
        if (expected.isInfinite() && actual.isInfinite()) {
            // ok
        } else if (expected.isNaN() && actual.isNaN()) {
            // ok
        } else {
            Assert.assertTrue("Actual: " + actual + ", expected: " + expected + " for " + degrees + "°", Math.abs(expected - actual) < Math.pow(10, -10));
        }
    }
}
