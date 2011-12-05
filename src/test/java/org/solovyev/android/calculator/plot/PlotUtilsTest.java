/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.plot;

import jscl.math.Expression;
import jscl.math.function.Constant;
import junit.framework.Assert;
import org.achartengine.model.XYSeries;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

/**
 * User: serso
 * Date: 12/5/11
 * Time: 9:07 PM
 */

public class PlotUtilsTest {

	@Test
	public void testAddXY() throws Exception {
		MyXYSeries series = new MyXYSeries("test_01", 100);
		PlotUtils.addXY(-10, 10, Expression.valueOf("asin(t)"), new Constant("t"), series, new MyXYSeries("test_01_imag"), false, 200);
		testAscSeries(series);
		PlotUtils.addXY(-1, 1, Expression.valueOf("asin(t)"), new Constant("t"), series, new MyXYSeries("test_01_imag"), true, 200);
		testAscSeries(series);

		series = new MyXYSeries("test_02", 1000);
		PlotUtils.addXY(-10, 10, Expression.valueOf("1/t"), new Constant("t"), series, new MyXYSeries("test_01_imag"), false, 1000);
		testAscSeries(series);
		PlotUtils.addXY(-1, 1, Expression.valueOf("1/t"), new Constant("t"), series, new MyXYSeries("test_01_imag"), true, 1000);
		testAscSeries(series);

	}

	public void testAscSeries(@NotNull XYSeries series) {
		for ( int i = 0; i < series.getItemCount(); i++ ) {
			if (i > 1) {
				Assert.assertTrue(series.getX(i - 1) + " > " +series.getX(i) + " at " + i + " of " + series.getItemCount(), series.getX(i - 1) <= series.getX(i));
			}
		}
	}
}
