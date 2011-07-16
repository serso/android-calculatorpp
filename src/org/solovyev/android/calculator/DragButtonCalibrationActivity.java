package org.solovyev.android.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.view.*;
import org.solovyev.util.math.MathUtils;
import org.solovyev.util.math.Point2d;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 7/16/11
 * Time: 7:28 PM
 */
public class DragButtonCalibrationActivity extends Activity {


	@NotNull
	private DragDirection dragDirection = DragDirection.up;

	private final List<DragData> dragHistory = new ArrayList<DragData>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.drag_button_calibration);

		final DragButton calibrationButton = (DragButton) findViewById(R.id.calibrationButton);
		calibrationButton.setOnDragListener(new CalibrationOnDragListener());

		createDragDirection();
	}

	private void createDragDirection() {
		dragDirection = Math.random() > 0.5 ? DragDirection.up : DragDirection.down;

		Toast.makeText(this, dragDirection.name(), Toast.LENGTH_SHORT).show();
	}

	public void restartClickHandler(View v) {
		createDragDirection();
	}


	private class CalibrationOnDragListener implements OnDragListener {

		@Override
		public boolean isSuppressOnClickEvent() {
			return true;
		}

		@Override
		public boolean onDrag(@NotNull DragButton dragButton, @NotNull DragEvent event) {
			final Point2d startPoint = event.getStartPoint();
			final MotionEvent motionEvent = event.getMotionEvent();

			// init end point
			final Point2d endPoint = new Point2d(motionEvent.getX(), motionEvent.getY());

			float distance = MathUtils.getDistance(startPoint, endPoint);

			double angle = Math.toDegrees(MathUtils.getAngle(startPoint, MathUtils.sum(startPoint, SimpleOnDragListener.axis), endPoint));

			assert dragDirection == DragDirection.up || dragDirection == DragDirection.down;
			if (dragDirection == DragDirection.up) {
				angle = 180 - angle;
			}

			dragHistory.add(new DragData(distance, angle, dragDirection));

			createDragDirection();

			return true;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			final List<Double> angleValues = new ArrayList<Double>();
			final List<Double> distanceValues = new ArrayList<Double>();
			for (DragData dragData : dragHistory) {
				angleValues.add(dragData.getAngle());
				distanceValues.add((double) dragData.getDistance());
			}

			double angleMean = MathUtils.countMean(angleValues);
			double angleDeviation = MathUtils.countStandardDeviation(angleMean, angleValues);

			double distanceMean = MathUtils.countMean(distanceValues);
			double distanceDeviation = MathUtils.countStandardDeviation(distanceMean, distanceValues);

			Toast.makeText(this, "Angle: m=" + angleMean + ", d=" + angleDeviation, Toast.LENGTH_SHORT).show();
			Toast.makeText(this, "Distance: m=" + distanceMean + ", d=" + distanceDeviation, Toast.LENGTH_SHORT).show();

		}


		return super.onKeyDown(keyCode, event);
	}


	private class DragData {

		private float distance;

		private double angle;

		@NotNull
		private DragDirection direction;

		private DragData(float distance, double angle, @NotNull DragDirection direction) {
			this.distance = distance;
			this.angle = angle;
			this.direction = direction;
		}

		public float getDistance() {
			return distance;
		}

		public double getAngle() {
			return angle;
		}

		@NotNull
		public DragDirection getDirection() {
			return direction;
		}
	}
}
