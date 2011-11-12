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
		TextProcessor textHighlighter = new TextHighlighter(0, true);

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

		Assert.assertEquals("<font color=\"#000000\"></font>)(((())())", textHighlighter.process(")(((())())").toString());
		Assert.assertEquals(")", textHighlighter.process(")").toString());
		Assert.assertEquals(")()(", textHighlighter.process(")()(").toString());
		Assert.assertEquals("1 000 000", textHighlighter.process("1000000").toString());
		Assert.assertEquals("1 000 000", textHighlighter.process("1000000").toString());

		textHighlighter = new TextHighlighter(0, false);
		Assert.assertEquals("0.1E3", textHighlighter.process("0.1E3").toString());
		Assert.assertEquals("1E3", textHighlighter.process("1E3").toString());
		Assert.assertEquals("1 000 000E3", textHighlighter.process("1000000E3").toString());
		Assert.assertEquals("-1 000 000E3", textHighlighter.process("-1000000E3").toString());
		Assert.assertEquals("-1 000 000E-3", textHighlighter.process("-1000000E-3").toString());
		Assert.assertEquals("-1 000 000E-30000", textHighlighter.process("-1000000E-30000").toString());
		textHighlighter = new TextHighlighter(0, true);

		textHighlighter.process("cannot calculate 3^10^10 !!!\n" +
				"        unable to enter 0. FIXED\n" +
				"        empty display in Xperia Rayo\n" +
				"        check привиденная FIXED\n" +
				"        set display result only if text in editor was not changed FIXED\n" +
				"        shift M text to the left\n" +
				"        do not show SYNTAX ERROR always (may be show send clock?q) FIXED\n" +
				"        ln(8)*log(8) =>  ln(8)*og(8) FIXED\n" +
				"        copy/paste ln(8)*log(8)\n" +
				"        6!^2 ERROR");

		Assert.assertEquals("<font color=\"#000000\"><i>sin</i>(</font><font color=\"#ffff9a\">2</font><font color=\"#000000\">)</font>", textHighlighter.process("sin(2)").toString());
		Assert.assertEquals("<font color=\"#000000\"><i>atanh</i>(</font><font color=\"#ffff9a\">2</font><font color=\"#000000\">)</font>", textHighlighter.process("atanh(2)").toString());

	}
}
