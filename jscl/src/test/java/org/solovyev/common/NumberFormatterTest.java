package org.solovyev.common;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
    public void testSimpleFormat() throws Exception {
        numberFormatter.useSimpleFormat();
        assertEquals("5000000000000000000", numberFormatter.format(5000000000000000000d));
        assertEquals("5000000000000000000", numberFormatter.format(5000000000000000001d));
        assertEquals("5999999999999994900", numberFormatter.format(5999999999999994999d));
        assertEquals("5E19", numberFormatter.format(50000000000000000000d));
        assertEquals("5E40", numberFormatter.format(50000000000000000000000000000000000000000d));
    }
}