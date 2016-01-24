package jscl.text;

import junit.framework.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 11/19/11
 * Time: 7:22 PM
 */
public class ParserUtilsTest {
    @Test
    public void testCopyOf() throws Exception {
        final Integer[] array = new Integer[]{1, 2, 3, 7};
        final Integer[] copy = ParserUtils.copyOf(array);

        Assert.assertEquals(array.length, copy.length);
        Assert.assertEquals(array[0], copy[0]);
        Assert.assertEquals(array[1], copy[1]);
        Assert.assertEquals(array[2], copy[2]);
        Assert.assertEquals(array[3], copy[3]);

        copy[3] = 12;
        Assert.assertFalse(array[3].equals(copy[3]));

    }
}
