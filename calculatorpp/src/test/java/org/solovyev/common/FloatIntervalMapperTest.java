package org.solovyev.common;

import junit.framework.Assert;
import org.junit.Test;
import org.solovyev.common.interval.IntervalImpl;
import org.solovyev.common.text.NumberIntervalMapper;

/**
 * User: serso
 * Date: 9/23/11
 * Time: 2:54 PM
 */
public class FloatIntervalMapperTest {

	@Test
	public void testParse() throws Exception {
		final NumberIntervalMapper<Float> mapper = new NumberIntervalMapper<Float>(Float.class);

		Assert.assertEquals(IntervalImpl.newClosed(1.2f, 12.2f), mapper.parseValue("1.2;12.2"));
		Assert.assertEquals(IntervalImpl.newPoint(0f), mapper.parseValue("0;0"));
	}
}
