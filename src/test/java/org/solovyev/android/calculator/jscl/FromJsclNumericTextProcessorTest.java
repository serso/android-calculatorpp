/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.jscl;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 10/18/11
 * Time: 10:42 PM
 */
public class FromJsclNumericTextProcessorTest {

	@Test
	public void testCreateResultForComplexNumber() throws Exception {
		final FromJsclNumericTextProcessor cm = new FromJsclNumericTextProcessor();

		Assert.assertEquals("1.22133+23 123i", cm.createResultForComplexNumber("1.22133232+23123*i"));
		Assert.assertEquals("1.22133+1.2i", cm.createResultForComplexNumber("1.22133232+1.2*i"));
		Assert.assertEquals("1.22i", cm.createResultForComplexNumber("1.22*i"));
		Assert.assertEquals("i", cm.createResultForComplexNumber("i"));
	}
}
