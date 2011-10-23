/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import junit.framework.Assert;
import org.junit.Test;
import org.solovyev.android.calculator.model.TextProcessor;

import java.util.Date;
import java.util.Random;

/**
 * User: serso
 * Date: 10/12/11
 * Time: 10:07 PM
 */
public class TextHighlighterTest {

	@Test
	public void testProcess() throws Exception {
		final TextProcessor textHighlighter = new TextHighlighter(0);

		final Random random = new Random(new Date().getTime());
		for (int i = 0; i < 1000; i++) {
			final StringBuilder sb = new StringBuilder();
			for (int j = 0; j < 1000; j++) {
				sb.append(random.nextBoolean() ? "(" : ")");
			}
			try {
				textHighlighter.process(sb.toString());
			} catch (Exception e) {
				System.out.println(sb.toString());
				throw e;
			}
		}

		Assert.assertEquals("<font color=\"#000000\"></font>)(((())())", textHighlighter.process(")(((())())"));
		Assert.assertEquals(")", textHighlighter.process(")"));
		Assert.assertEquals(")()(", textHighlighter.process(")()("));
		Assert.assertEquals("1 000 000", textHighlighter.process("1000000"));
	}
}
