package org.solovyev.android.calculator.plot;

class ZoomTracker {

	private static final float EPS = 1.5f;
	private static final float MIN_DISTANCE = distance(0f, 50f);
	public static final String TAG = "ZoomTracker";

	private float initialXDistance;
	private float initialYDistance;

	private float initialXValue;
	private float initialYValue;

	float xValue;
	float yValue;

	void start(float xValue, float yValue,
			   float x1, float y1,
			   float x2, float y2) {

		initialXDistance = distance(x1, x2);
		initialYDistance = distance(y1, y2);

		initialXValue = xValue;
		initialYValue = yValue;

		this.xValue = xValue;
		this.yValue = yValue;
	}

	boolean update(float x1, float y1, float x2, float y2) {
		boolean result = false;

		if (initialXDistance > MIN_DISTANCE) {
			final float xDistance = distance(x1, x2);
			if (xDistance > EPS) {
				xValue = initialXDistance / xDistance * initialXValue;
				result = true;
			}
		}

		if (initialYDistance > MIN_DISTANCE) {
			final float yDistance = distance(y1, y2);
			if (yDistance > EPS) {
				yValue = initialYDistance / yDistance * initialYValue;
				result = true;
			}
		}

		return result;
	}

	private static float distance(float x1, float x2) {
		final float dx = x1 - x2;
		return dx * dx;
	}
}
