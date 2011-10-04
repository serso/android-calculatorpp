/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.math;

import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.solovyev.android.calculator.CalculatorModel;

/**
 * User: serso
 * Date: 10/5/11
 * Time: 1:25 AM
 */
public class MathEntityTypeTest {

	@BeforeClass
	public static void setUp() throws Exception {
		CalculatorModel.init(null);
	}

	@Test
	public void testGetType() throws Exception {
		Assert.assertEquals(MathEntityType.function, MathEntityType.getType("sin", 0).getMathEntityType());
		Assert.assertEquals(MathEntityType.text, MathEntityType.getType("sn", 0).getMathEntityType());
		Assert.assertEquals(MathEntityType.text, MathEntityType.getType("s", 0).getMathEntityType());
		Assert.assertEquals(MathEntityType.text, MathEntityType.getType("", 0).getMathEntityType());

		try {
			Assert.assertEquals(MathEntityType.text, MathEntityType.getType("22", -1).getMathEntityType());
			Assert.fail();
		} catch (IllegalArgumentException e) {
		}

		try {
			Assert.assertEquals(MathEntityType.text, MathEntityType.getType("22", 2).getMathEntityType());
			Assert.fail();
		} catch (IllegalArgumentException e) {
		}
	}
}
