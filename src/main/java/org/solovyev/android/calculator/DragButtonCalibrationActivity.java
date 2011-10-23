/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.view.widgets.*;
import org.solovyev.common.NumberIntervalMapper;
import org.solovyev.common.collections.ManyValuedHashMap;
import org.solovyev.common.collections.ManyValuedMap;
import org.solovyev.common.utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: serso
 * Date: 7/16/11
 * Time: 7:28 PM
 */
public class DragButtonCalibrationActivity extends Activity {

	@NotNull
	private final List<DragData> dragHistory = new ArrayList<DragData>();

	private final Map<DragButton, CalibrationArrow> map = new HashMap<DragButton, CalibrationArrow>();

	private static final float DEFAULT_VALUE = -999;
	private static final int MIN_HISTORY_FOR_CALIBRATION = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.drag_button_calibration);

		createCalibrationButton(R.id.calibrationButtonRight, R.id.calibrationArrowRight);
		createCalibrationButton(R.id.calibrationButtonLeft, R.id.calibrationArrowLeft);
	}

	private void createCalibrationButton(int buttonId, int arrowId) {
		final DragButton calibrationButton = (DragButton) findViewById(buttonId);
		calibrationButton.setOnDragListener(new CalibrationOnDragListener());

		ImageView imageView = (ImageView) findViewById(arrowId);
		CalibrationArrow calibrationArrow = new CalibrationArrow(imageView);

		createDragDirection(0, calibrationArrow);

		map.put(calibrationButton, calibrationArrow);
	}

	private void createDragDirection(long timeout, @NotNull final CalibrationArrow calibrationArrow) {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				calibrationArrow.dragDirection = Math.random() > 0.5 ? DragDirection.up : DragDirection.down;

				calibrationArrow.calibrationArrow.setImageResource(calibrationArrow.dragDirection == DragDirection.down ? R.drawable.down : R.drawable.up);
			}
		}, timeout);
	}

	public void restartClickHandler(View v) {
		for (CalibrationArrow calibrationArrow : map.values()) {
			createDragDirection(0, calibrationArrow);
		}
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

			final CalibrationArrow calibrationArrow = map.get(dragButton);
			final DragDirection dragDirection = calibrationArrow.dragDirection;

			assert dragDirection == DragDirection.up || dragDirection == DragDirection.down;

			double deviationAngle = angle;
			if (dragDirection == DragDirection.up) {
				deviationAngle = 180 - deviationAngle;
			}

			if (deviationAngle > 45) {
				calibrationArrow.calibrationArrow.setImageResource(R.drawable.not_ok);
			} else {
				calibrationArrow.calibrationArrow.setImageResource(R.drawable.ok);
				dragHistory.add(new DragData(dragDirection, distance, angle, (motionEvent.getEventTime() - motionEvent.getDownTime())));
			}

			createDragDirection(500, calibrationArrow);

			return true;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (dragHistory.size() > MIN_HISTORY_FOR_CALIBRATION) {
				final ManyValuedMap<DragDirection, Double> anglesByDirection = new ManyValuedHashMap<DragDirection, Double>();
				final ManyValuedMap<DragDirection, Double> distancesByDirection = new ManyValuedHashMap<DragDirection, Double>();
				final ManyValuedMap<DragDirection, Double> timesByDirection = new ManyValuedHashMap<DragDirection, Double>();
				for (DragData dragData : dragHistory) {
					anglesByDirection.put(dragData.getDirection(), dragData.getAngle());
					distancesByDirection.put(dragData.getDirection(), (double) dragData.getDistance());
					timesByDirection.put(dragData.getDirection(), dragData.getTime());
				}

				final Map<DragDirection, MathUtils.StatData> angleStatData = getStatDataByDirection(anglesByDirection);
				final Map<DragDirection, MathUtils.StatData> distanceStatData = getStatDataByDirection(distancesByDirection);
				final Map<DragDirection, MathUtils.StatData> timeStatData = getStatDataByDirection(timesByDirection);

				Log.d(this.getClass().getName(), "Angle statistics: ");
				logStatData(angleStatData);

				Log.d(this.getClass().getName(), "Distance statistics: ");
				logStatData(distanceStatData);

				Log.d(this.getClass().getName(), "Time statistics: ");
				logStatData(timeStatData);

				final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
				final SharedPreferences.Editor editor = settings.edit();

				setPreferences(angleStatData, editor, SimpleOnDragListener.PreferenceType.angle);
				setPreferences(distanceStatData, editor, SimpleOnDragListener.PreferenceType.distance);
				setPreferences(timeStatData, editor, SimpleOnDragListener.PreferenceType.duration);

				editor.commit();
			}
		}


		return super.onKeyDown(keyCode, event);
	}

	private void setPreferences(@NotNull Map<DragDirection, MathUtils.StatData> statData, @NotNull SharedPreferences.Editor editor, @NotNull SimpleOnDragListener.PreferenceType preferenceType) {
		final Mapper<Interval<Float>> mapper = new NumberIntervalMapper<Float>(Float.class);
		for (Map.Entry<DragDirection, MathUtils.StatData> entry : statData.entrySet()) {
			final float min = (float) entry.getValue().getMean() - 2 * (float) entry.getValue().getStandardDeviation();
			final float max = (float) entry.getValue().getMean() + 2 * (float) entry.getValue().getStandardDeviation();
			editor.putString(SimpleOnDragListener.getPreferenceId(preferenceType, entry.getKey()), mapper.formatValue(SimpleOnDragListener.transformInterval(preferenceType, entry.getKey(), new NumberInterval<Float>(Math.max(0, min), max))));
		}
	}


	private void logStatData(@NotNull Map<DragDirection, MathUtils.StatData> statData) {
		for (Map.Entry<DragDirection, MathUtils.StatData> entry : statData.entrySet()) {
			Log.d(this.getClass().getName(), entry.getKey() + "-> m: " + entry.getValue().getMean() + ", d: " + entry.getValue().getStandardDeviation());
		}
	}

	private Map<DragDirection, MathUtils.StatData> getStatDataByDirection(@NotNull ManyValuedMap<DragDirection, Double> valuesByDirection) {
		final Map<DragDirection, MathUtils.StatData> result = new HashMap<DragDirection, MathUtils.StatData>();

		for (Map.Entry<DragDirection, List<Double>> entry : valuesByDirection.entrySet()) {
			result.put(entry.getKey(), MathUtils.getStatData(entry.getValue()));
		}

		return result;
	}


	private class DragData {

		private float distance;

		private double angle;

		private double time;

		@NotNull
		private DragDirection direction;

		private DragData(@NotNull DragDirection direction, float distance, double angle, double time) {
			this.distance = distance;
			this.angle = angle;
			this.direction = direction;
			this.time = time;
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

		public double getTime() {
			return time;
		}
	}

	private class CalibrationArrow {
		@NotNull
		private ImageView calibrationArrow;

		@NotNull
		private DragDirection dragDirection = DragDirection.up;

		private CalibrationArrow(@NotNull ImageView calibrationArrow) {
			this.calibrationArrow = calibrationArrow;
		}
	}
}
