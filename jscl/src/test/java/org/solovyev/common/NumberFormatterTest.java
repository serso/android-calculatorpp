package org.solovyev.common;

import org.junit.Before;
import org.junit.Test;

import static java.lang.Math.pow;
import static org.junit.Assert.assertEquals;
import static org.solovyev.common.NumberFormatter.DEFAULT_MAGNITUDE;
import static org.solovyev.common.NumberFormatter.NO_ROUNDING;

public class NumberFormatterTest {

    private NumberFormatter numberFormatter;

    @Before
    public void setUp() throws Exception {
        numberFormatter = new NumberFormatter();
    }

    @Test
    public void testEngineeringFormat() throws Exception {
        numberFormatter.useEngineeringFormat(5);
        assertEquals("0.1", numberFormatter.format(0.1d));
        assertEquals("0.01", numberFormatter.format(0.01d));
        assertEquals("0.001", numberFormatter.format(0.001d));
        assertEquals("5", numberFormatter.format(5d));
        assertEquals("5000", numberFormatter.format(5000d));
    }

    @Test
    public void testScientificFormatNoRounding() throws Exception {
        numberFormatter.useScientificFormat(DEFAULT_MAGNITUDE);
        numberFormatter.setPrecision(NO_ROUNDING);

        assertEquals("1", numberFormatter.format(1d));
        assertEquals("0.3333333333333333", numberFormatter.format(1d / 3));
        assertEquals("3.333333333333333E-19", numberFormatter.format(pow(10, -18) / 3));
        assertEquals("1.23456789E18", numberFormatter.format(123456789 * pow(10, 10)));
        assertEquals("1E-16", numberFormatter.format(pow(10, -16)));
        assertEquals("5.9999999999999949E18", numberFormatter.format(5999999999999994999d));

        testScientificFormat();
    }

    @Test
    public void testScientificFormatWithRounding() throws Exception {
        numberFormatter.useScientificFormat(DEFAULT_MAGNITUDE);
        numberFormatter.setPrecision(5);

        assertEquals("1", numberFormatter.format(1d));
        assertEquals("0.33333", numberFormatter.format(1d / 3));
        assertEquals("3.33333E-19", numberFormatter.format(pow(10, -18) / 3));
        assertEquals("1.23457E18", numberFormatter.format(123456789 * pow(10, 10)));
        assertEquals("1E-16", numberFormatter.format(pow(10, -16)));
        assertEquals("6E18", numberFormatter.format(5999999999999994999d));


        testScientificFormat();
    }

    @Test
    public void testSimpleFormatNoRounding() throws Exception {
        numberFormatter.useSimpleFormat();
        numberFormatter.setPrecision(NO_ROUNDING);

        assertEquals("1", numberFormatter.format(1d));
        assertEquals("0.000001", numberFormatter.format(pow(10, -6)));
        assertEquals("0.3333333333333333", numberFormatter.format(1d / 3));
        assertEquals("3.333333333333333E-19", numberFormatter.format(pow(10, -18) / 3));
        assertEquals("1234567890000000000", numberFormatter.format(123456789 * pow(10, 10)));
        assertEquals("0.0000000000000001", numberFormatter.format(pow(10, -16)));
        assertEquals("1E-17", numberFormatter.format(pow(10, -17)));
        assertEquals("1E-18", numberFormatter.format(pow(10, -18)));
        assertEquals("1.5E-18", numberFormatter.format(1.5 * pow(10, -18)));
        assertEquals("1E-100", numberFormatter.format(pow(10, -100)));

        testSimpleFormat();
    }

    @Test
    public void testSimpleFormatWithRounding() throws Exception {
        numberFormatter.useSimpleFormat();
        numberFormatter.setPrecision(5);

        assertEquals("1", numberFormatter.format(1d));
        assertEquals("0", numberFormatter.format(pow(10, -6)));
        assertEquals("0.33333", numberFormatter.format(1d / 3));
        assertEquals("0", numberFormatter.format(pow(10, -18) / 3));
        assertEquals("1234567890000000000", numberFormatter.format(123456789 * pow(10, 10)));
        assertEquals("0", numberFormatter.format(pow(10, -16)));
        assertEquals("0", numberFormatter.format(pow(10, -17)));
        assertEquals("0", numberFormatter.format(pow(10, -18)));
        assertEquals("0", numberFormatter.format(1.5 * pow(10, -18)));
        assertEquals("0", numberFormatter.format(pow(10, -100)));

        testSimpleFormat();
    }

    // testing simple format with and without rounding
    private void testSimpleFormat() {
        assertEquals("0.00001", numberFormatter.format(pow(10, -5)));
        assertEquals("100", numberFormatter.format(pow(10, 2)));
        assertEquals("1000000000000000000", numberFormatter.format(pow(10, 18)));
        assertEquals("1E19", numberFormatter.format(pow(10, 19)));
        assertEquals("1E20", numberFormatter.format(pow(10, 20)));
        assertEquals("1E100", numberFormatter.format(pow(10, 100)));

        assertEquals("0.01", numberFormatter.format(pow(10, -2)));

        assertEquals("5000000000000000000", numberFormatter.format(5000000000000000000d));
        assertEquals("5000000000000000000", numberFormatter.format(5000000000000000001d));
        assertEquals("5999999999999994900", numberFormatter.format(5999999999999994999d));
        assertEquals("5E19", numberFormatter.format(50000000000000000000d));
        assertEquals("5E40", numberFormatter.format(50000000000000000000000000000000000000000d));
    }

    // testing scientific format with and without rounding
    private void testScientificFormat() {
        assertEquals("0.00001", numberFormatter.format(pow(10, -5)));
        assertEquals("1E-6", numberFormatter.format(pow(10, -6)));
        assertEquals("100", numberFormatter.format(pow(10, 2)));
        assertEquals("10000", numberFormatter.format(pow(10, 4)));
        assertEquals("1E5", numberFormatter.format(pow(10, 5)));
        assertEquals("1E18", numberFormatter.format(pow(10, 18)));
        assertEquals("1E19", numberFormatter.format(pow(10, 19)));
        assertEquals("1E20", numberFormatter.format(pow(10, 20)));
        assertEquals("1E100", numberFormatter.format(pow(10, 100)));

        assertEquals("0.01", numberFormatter.format(pow(10, -2)));
        assertEquals("1E-17", numberFormatter.format(pow(10, -17)));
        assertEquals("1E-18", numberFormatter.format(pow(10, -18)));
        assertEquals("1.5E-18", numberFormatter.format(1.5 * pow(10, -18)));
        assertEquals("1E-100", numberFormatter.format(pow(10, -100)));

        assertEquals("5E18", numberFormatter.format(5000000000000000000d));
        assertEquals("5E18", numberFormatter.format(5000000000000000001d));
        assertEquals("5E19", numberFormatter.format(50000000000000000000d));
        assertEquals("5E40", numberFormatter.format(50000000000000000000000000000000000000000d));
    }
}