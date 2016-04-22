package org.solovyev.android.calculator.converter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConverterTest {

    @Test(expected = NumberFormatException.class)
    public void testShouldNotParseInvalidDecNumber() throws Exception {
        Converter.parse("1A", 10);
    }

    @Test
    public void testShouldParseValidHexNumber() throws Exception {
        assertEquals(26, Converter.parse("1A", 16).toLong());
    }
}