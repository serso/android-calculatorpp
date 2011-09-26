package org.solovyev.common;

import junit.framework.Assert;
import org.junit.Test;
import org.solovyev.common.utils.Interval;
import org.solovyev.common.utils.IntervalImpl;
import org.solovyev.common.utils.Mapper;

/**
 * User: serso
 * Date: 9/23/11
 * Time: 2:54 PM
 */
public class FloatIntervalMapperTest {

	@Test
	public void testParse() throws Exception {
		final Mapper<Interval<Float>> mapper = new NumberIntervalMapper<Float>(Float.class);

		Assert.assertEquals(new IntervalImpl<Float>(1.2f, 12.2f), mapper.parseValue("1.2;12.2"));
		Assert.assertEquals(new IntervalImpl<Float>(0f, 0f), mapper.parseValue("0;0"));
	}
}
