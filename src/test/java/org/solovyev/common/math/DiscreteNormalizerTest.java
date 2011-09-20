package org.solovyev.common.math;

import org.junit.Assert;
import org.junit.Test;
import org.solovyev.common.utils.MathUtils;

/**
 * User: serso
 * Date: 9/20/11
 * Time: 5:51 PM
 */
public class DiscreteNormalizerTest {

	@Test
	public void testNormalize() throws Exception {
	 	DiscreteNormalizer dn = new DiscreteNormalizer(0, 10, 1);

		Assert.assertTrue(MathUtils.equals(0, dn.normalize(0.5), 2));
		Assert.assertTrue(MathUtils.equals(0, dn.normalize(0.99), 2));
		Assert.assertTrue(MathUtils.equals(0.1, dn.normalize(1), 2));
		Assert.assertTrue(MathUtils.equals(0.1, dn.normalize(1.01), 2));
		Assert.assertTrue(MathUtils.equals(1, dn.normalize(10), 2));
		Assert.assertTrue(MathUtils.equals(0.9, dn.normalize(9.99), 2));
	}

	@Test
	public void testDenormalize() throws Exception {
		DiscreteNormalizer dn = new DiscreteNormalizer(0, 10, 1);

		Assert.assertTrue(MathUtils.equals(0, dn.normalize(dn.denormalize(0)), 2));
		Assert.assertTrue(MathUtils.equals(0.1, dn.normalize(dn.denormalize(0.1)), 2));
		Assert.assertTrue(MathUtils.equals(1, dn.normalize(dn.denormalize(1)), 2));
		Assert.assertTrue(MathUtils.equals(0.9, dn.normalize(dn.denormalize(0.9)), 2));
	}
}
