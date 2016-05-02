package jscl;

import org.junit.Before;
import org.junit.Test;

import midpcalc.Real;

import static org.junit.Assert.assertEquals;

/**
 * User: serso
 * Date: 12/15/11
 * Time: 11:25 AM
 */
public class JsclMathEngineTest {

    private JsclMathEngine me;

    @Before
    public void setUp() throws Exception {
        me = new JsclMathEngine();
    }

    @Test
    public void testFormat() throws Exception {
        try {
            me.setGroupingSeparator(' ');
            assertEquals("1", me.format(1d, NumeralBase.bin));
            assertEquals("10", me.format(2d, NumeralBase.bin));
            assertEquals("11", me.format(3d, NumeralBase.bin));
            assertEquals("100", me.format(4d, NumeralBase.bin));
            assertEquals("101", me.format(5d, NumeralBase.bin));
            assertEquals("110", me.format(6d, NumeralBase.bin));
            assertEquals("111", me.format(7d, NumeralBase.bin));
            assertEquals("1000", me.format(8d, NumeralBase.bin));
            assertEquals("1001", me.format(9d, NumeralBase.bin));
            assertEquals("1 0001", me.format(17d, NumeralBase.bin));
            assertEquals("1 0100", me.format(20d, NumeralBase.bin));
            assertEquals("1 0100", me.format(20d, NumeralBase.bin));
            assertEquals("1 1111", me.format(31d, NumeralBase.bin));

            me.setRoundResult(true);
            me.setPrecision(10);

            assertEquals("111 1111 0011 0110", me.format(32566d, NumeralBase.bin));
            assertEquals("100.0100110011", me.format(4.3d, NumeralBase.bin));
            assertEquals("1 0001 0101 0011.010101011", me.format(4435.33423d, NumeralBase.bin));
            assertEquals("1100.0101010101", me.format(12.3333d, NumeralBase.bin));
            assertEquals("1 0011 1101 1110 0100 0011 0101 0101.00011111", me.format(333333333.1212213321d, NumeralBase.bin));

            assertEquals("0.EEEEEEEEEF", me.format(14d / 15d, NumeralBase.hex));
            assertEquals("7F 36", me.format(32566d, NumeralBase.hex));
            assertEquals("24", me.format(36d, NumeralBase.hex));
            assertEquals("8", me.format(8d, NumeralBase.hex));
            assertEquals("1 3D", me.format(317d, NumeralBase.hex));
            assertEquals("13 DE 43 55.1F085BEF", me.format(333333333.1212213321d, NumeralBase.hex));
            assertEquals("D 25 0F 77 0A.6F7319", me.format(56456345354.43534534523459999d, NumeralBase.hex));
            assertEquals("3 E7.4CCCCCCCCD", me.format(999.3d, NumeralBase.hex));

            me.setRoundResult(false);
            assertEquals("6.CCDA6A054226DB6E-19", me.format(0.00000000000000000000009d, NumeralBase.hex));
            assertEquals("A.E15D766ED03E2BEE-20", me.format(0.000000000000000000000009d, NumeralBase.hex));
        } finally {
            me.setGroupingSeparator(JsclMathEngine.GROUPING_SEPARATOR_NO);
        }

        assertEquals("1", me.format(1d, NumeralBase.bin));
        assertEquals("10", me.format(2d, NumeralBase.bin));
        assertEquals("11", me.format(3d, NumeralBase.bin));
        assertEquals("100", me.format(4d, NumeralBase.bin));
        assertEquals("101", me.format(5d, NumeralBase.bin));
        assertEquals("110", me.format(6d, NumeralBase.bin));
        assertEquals("111", me.format(7d, NumeralBase.bin));
        assertEquals("1000", me.format(8d, NumeralBase.bin));
        assertEquals("1001", me.format(9d, NumeralBase.bin));
        assertEquals("10001", me.format(17d, NumeralBase.bin));
        assertEquals("10100", me.format(20d, NumeralBase.bin));
        assertEquals("10100", me.format(20d, NumeralBase.bin));
        assertEquals("11111", me.format(31d, NumeralBase.bin));
        assertEquals("111111100110110", me.format(32566d, NumeralBase.bin));

        assertEquals("7F36", me.format(32566d, NumeralBase.hex));
        assertEquals("24", me.format(36d, NumeralBase.hex));
        assertEquals("8", me.format(8d, NumeralBase.hex));
        assertEquals("13D", me.format(317d, NumeralBase.hex));
    }


    @Test
    public void testPiComputation() throws Exception {
        assertEquals("-1+0.0000000000000001*i", me.evaluate("exp(√(-1)*Π)"));
    }

    @Test
    public void testBinShouldAlwaysUseSpaceAsGroupingSeparator() throws Exception {
        me.setGroupingSeparator('\'');

        assertEquals("100 0000 0000", me.format(1024d, NumeralBase.bin));
    }

    @Test
    public void testHexShouldAlwaysUseSpaceAsGroupingSeparator() throws Exception {
        me.setGroupingSeparator('\'');

        assertEquals("4 00", me.format(1024d, NumeralBase.hex));
    }

    @Test
    public void testEngineeringNotationWithRounding() throws Exception {
        me.setNumberFormat(Real.NumberFormat.FSE_ENG);
        me.setRoundResult(true);
        me.setPrecision(5);

        assertEquals("10E6", me.format(10000000d));
        assertEquals("99E6", me.format(99000000d));
        assertEquals("999E6", me.format(999000000d));
        assertEquals("999E6", me.format(999000001d));
        assertEquals("999.00001E6", me.format(999000011d));
        assertEquals("1E6", me.format(1000000d));
        assertEquals("111.11E3", me.format(111110d));
        assertEquals("111.1E3", me.format(111100d));
        assertEquals("111E3", me.format(111000d));
        assertEquals("110E3", me.format(110000d));
        assertEquals("100E3", me.format(100000d));
        assertEquals("10000", me.format(10000d));
        assertEquals("1000", me.format(1000d));
        assertEquals("100", me.format(100d));
        assertEquals("100.1", me.format(100.1d));
        assertEquals("100.12", me.format(100.12d));
        assertEquals("100.12345", me.format(100.123454d));
        assertEquals("100.12346", me.format(100.123455d));
        assertEquals("100.12346", me.format(100.123456d));
        assertEquals("1", me.format(1d));
        assertEquals("-42", me.format(-42d));
        assertEquals("-999", me.format(-999d));
        assertEquals("-999.99", me.format(-999.99d));
        assertEquals("-0.1", me.format(-0.1d));

        assertEquals("-0.12", me.format(-0.12d));
        assertEquals("-0.123", me.format(-0.123d));
        assertEquals("-0.1234", me.format(-0.1234d));
        assertEquals("0.1", me.format(0.1));
        assertEquals("0.01", me.format(0.01));
        assertEquals("0.001", me.format(0.001));
        assertEquals("0.001", me.format(0.00100000001));
        assertEquals("0.0011", me.format(0.0011));
        assertEquals("0.001", me.format(0.000999999));
        assertEquals("0.0001", me.format(0.0001));
        assertEquals("1E-6", me.format(0.000001));
        assertEquals("10E-9", me.format(0.00000001));

        assertEquals("-100.001E3", me.format(-100001d));
        assertEquals("100.001E3", me.format(100001d));
        assertEquals("111.111E3", me.format(111111d));
        assertEquals("111.11123E3", me.format(111111.234567d));
        assertEquals("111.11123E3", me.format(111111.23456d));
        assertEquals("111.11123E3", me.format(111111.2345d));
        assertEquals("111.11123E3", me.format(111111.2345d));
        assertEquals("111.11123E3", me.format(111111.234d));
        assertEquals("111.11123E3", me.format(111111.23d));
        assertEquals("111.1112E3", me.format(111111.2d));
    }

    @Test
    public void testEngineeringNotationWithoutRounding() throws Exception {
        me.setNumberFormat(Real.NumberFormat.FSE_ENG);
        me.setRoundResult(false);

        assertEquals("10E6", me.format(10000000d));
        assertEquals("99E6", me.format(99000000d));
        assertEquals("999E6", me.format(999000000d));
        assertEquals("999.000001E6", me.format(999000001d));
        assertEquals("999.000011E6", me.format(999000011d));
        assertEquals("1E6", me.format(1000000d));
        assertEquals("111.11E3", me.format(111110d));
        assertEquals("111.1E3", me.format(111100d));
        assertEquals("111E3", me.format(111000d));
        assertEquals("110E3", me.format(110000d));
        assertEquals("100E3", me.format(100000d));
        assertEquals("10000", me.format(10000d));
        assertEquals("1000", me.format(1000d));
        assertEquals("100", me.format(100d));
        assertEquals("100.1", me.format(100.1d));
        assertEquals("100.12", me.format(100.12d));
        assertEquals("100.123454", me.format(100.123454d));
        assertEquals("100.123455", me.format(100.123455d));
        assertEquals("100.123456", me.format(100.123456d));
        assertEquals("1", me.format(1d));
        assertEquals("-42", me.format(-42d));
        assertEquals("-999", me.format(-999d));
        assertEquals("-999.99", me.format(-999.99d));
        assertEquals("-0.1", me.format(-0.1d));
        assertEquals("-0.12", me.format(-0.12d));
        assertEquals("-0.123", me.format(-0.123d));
        assertEquals("-0.1234", me.format(-0.1234d));
        assertEquals("0.1", me.format(0.1));
        assertEquals("0.01", me.format(0.01));
        assertEquals("0.001", me.format(0.001));
        assertEquals("0.0011", me.format(0.0011));
        assertEquals("0.000999999", me.format(0.000999999));
        assertEquals("0.0001", me.format(0.0001));

        assertEquals("100.001E3", me.format(100001d));
        assertEquals("111.111E3", me.format(111111d));
        assertEquals("111.111234567E3", me.format(111111.234567d));
        assertEquals("111.11123456E3", me.format(111111.23456d));
        assertEquals("111.1112345E3", me.format(111111.2345d));
        assertEquals("111.1112345E3", me.format(111111.2345d));
        assertEquals("111.111234E3", me.format(111111.234d));
        assertEquals("111.11123E3", me.format(111111.23d));
        assertEquals("111.1112E3", me.format(111111.2d));
    }
}
