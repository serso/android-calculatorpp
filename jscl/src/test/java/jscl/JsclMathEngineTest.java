package jscl;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 12/15/11
 * Time: 11:25 AM
 */
public class JsclMathEngineTest {
    @Test
    public void testFormat() throws Exception {
        final MathContext me = JsclMathEngine.getInstance();

        try {
            me.setUseGroupingSeparator(true);
            Assert.assertEquals("1", me.format(1d, NumeralBase.bin));
            Assert.assertEquals("10", me.format(2d, NumeralBase.bin));
            Assert.assertEquals("11", me.format(3d, NumeralBase.bin));
            Assert.assertEquals("100", me.format(4d, NumeralBase.bin));
            Assert.assertEquals("101", me.format(5d, NumeralBase.bin));
            Assert.assertEquals("110", me.format(6d, NumeralBase.bin));
            Assert.assertEquals("111", me.format(7d, NumeralBase.bin));
            Assert.assertEquals("1000", me.format(8d, NumeralBase.bin));
            Assert.assertEquals("1001", me.format(9d, NumeralBase.bin));
            Assert.assertEquals("1 0001", me.format(17d, NumeralBase.bin));
            Assert.assertEquals("1 0100", me.format(20d, NumeralBase.bin));
            Assert.assertEquals("1 0100", me.format(20d, NumeralBase.bin));
            Assert.assertEquals("1 1111", me.format(31d, NumeralBase.bin));

            me.setRoundResult(true);
            me.setPrecision(10);

            Assert.assertEquals("111 1111 0011 0110", me.format(32566d, NumeralBase.bin));
            Assert.assertEquals("100.0100 1100 11", me.format(4.3d, NumeralBase.bin));
            Assert.assertEquals("1 0001 0101 0011.0101 0101 10", me.format(4435.33423d, NumeralBase.bin));
            Assert.assertEquals("1100.0101 0101 01", me.format(12.3333d, NumeralBase.bin));
            Assert.assertEquals("1 0011 1101 1110 0100 0011 0101 0101.0001 1111 00", me.format(333333333.1212213321d, NumeralBase.bin));

            Assert.assertEquals("0.EE EE EE EE EE", me.format(14d / 15d, NumeralBase.hex));
            Assert.assertEquals("7F 36", me.format(32566d, NumeralBase.hex));
            Assert.assertEquals("24", me.format(36d, NumeralBase.hex));
            Assert.assertEquals("8", me.format(8d, NumeralBase.hex));
            Assert.assertEquals("1 3D", me.format(317d, NumeralBase.hex));
            Assert.assertEquals("13 DE 43 55.1F 08 5B EF 14", me.format(333333333.1212213321d, NumeralBase.hex));
            Assert.assertEquals("D 25 0F 77 0A.6F 73 18 FC 50", me.format(56456345354.43534534523459999d, NumeralBase.hex));
            Assert.assertEquals("3 E7.4C CC CC CC CC", me.format(999.3d, NumeralBase.hex));

            me.setRoundResult(false);
            Assert.assertEquals("0.00 00 00 00 00 00 00 00 00 6C", me.format(0.00000000000000000000009d, NumeralBase.hex));
            Assert.assertEquals("0.00 00 00 00 00 00 00 00 00 0A", me.format(0.000000000000000000000009d, NumeralBase.hex));

        } finally {
            me.setUseGroupingSeparator(false);
        }

        Assert.assertEquals("1", me.format(1d, NumeralBase.bin));
        Assert.assertEquals("10", me.format(2d, NumeralBase.bin));
        Assert.assertEquals("11", me.format(3d, NumeralBase.bin));
        Assert.assertEquals("100", me.format(4d, NumeralBase.bin));
        Assert.assertEquals("101", me.format(5d, NumeralBase.bin));
        Assert.assertEquals("110", me.format(6d, NumeralBase.bin));
        Assert.assertEquals("111", me.format(7d, NumeralBase.bin));
        Assert.assertEquals("1000", me.format(8d, NumeralBase.bin));
        Assert.assertEquals("1001", me.format(9d, NumeralBase.bin));
        Assert.assertEquals("10001", me.format(17d, NumeralBase.bin));
        Assert.assertEquals("10100", me.format(20d, NumeralBase.bin));
        Assert.assertEquals("10100", me.format(20d, NumeralBase.bin));
        Assert.assertEquals("11111", me.format(31d, NumeralBase.bin));
        Assert.assertEquals("111111100110110", me.format(32566d, NumeralBase.bin));

        Assert.assertEquals("7F36", me.format(32566d, NumeralBase.hex));
        Assert.assertEquals("24", me.format(36d, NumeralBase.hex));
        Assert.assertEquals("8", me.format(8d, NumeralBase.hex));
        Assert.assertEquals("13D", me.format(317d, NumeralBase.hex));
    }

    @Test
    public void testPiComputation() throws Exception {
        final JsclMathEngine me = JsclMathEngine.getInstance();
        Assert.assertEquals("-1+122.46467991473532E-18*i", me.evaluate("exp(√(-1)*Π)"));
    }
}
