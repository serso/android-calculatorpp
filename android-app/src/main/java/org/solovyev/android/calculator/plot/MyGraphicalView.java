package org.solovyev.android.calculator.plot;

import android.content.Context;
import android.graphics.Canvas;
import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;
import org.solovyev.android.calculator.Locator;

public class MyGraphicalView extends GraphicalView {

	private static final String TAG = MyGraphicalView.class.getSimpleName();

	public MyGraphicalView(Context context, AbstractChart chart) {
		super(context, chart);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		try {
			super.onDraw(canvas);
		} catch (RuntimeException e) {
			Locator.getInstance().getLogger().error(TAG, e.getMessage(), e);
		}
	}
}
