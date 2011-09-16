package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.view.*;
import org.solovyev.common.collections.ManyValuedHashMap;
import org.solovyev.common.collections.ManyValuedMap;
import org.solovyev.common.utils.Interval;
import org.solovyev.common.utils.MathUtils;
import org.solovyev.common.utils.Point2d;

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

	public static final String PREFERENCES = "dragButtonPreferences";

	public static final String PREFERENCES_FIRST_RUN = "firstRun";

	public static final String PREFERENCES_MIN = "min";
	public static final String PREFERENCES_MAX = "max";

	private static final float DEFAULT_VALUE = -999;
	private static final int MIN_HISTORY_FOR_CALIBRATION = 10;
	public static final String INTENT_ACTION = "org.solovyev.android.calculator.DragButtonPreferencesChanged";

	public static enum PreferenceType {
		angle,
		distance,
		duration
	}


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

				final SharedPreferences settings = getSharedPreferences(PREFERENCES, 0);
				final SharedPreferences.Editor editor = settings.edit();

				setPreferences(angleStatData, editor, PreferenceType.angle);
				setPreferences(distanceStatData, editor, PreferenceType.distance);
				setPreferences(timeStatData, editor, PreferenceType.duration);

				editor.commit();

				sendOrderedBroadcast(new Intent(INTENT_ACTION), null);
			}
		}


		return super.onKeyDown(keyCode, event);
	}

	private void setPreferences(@NotNull Map<DragDirection, MathUtils.StatData> statData, @NotNull SharedPreferences.Editor editor, @NotNull PreferenceType preferenceType) {
		for (Map.Entry<DragDirection, MathUtils.StatData> entry : statData.entrySet()) {
			final float min = (float) entry.getValue().getMean() - 2 * (float) entry.getValue().getStandardDeviation();
			final float max = (float) entry.getValue().getMean() + 2 * (float) entry.getValue().getStandardDeviation();
			editor.putFloat(preferenceType.name() + "_" + entry.getKey().name() + "_" + PREFERENCES_MIN, Math.max(0, min));
			editor.putFloat(preferenceType.name() + "_" + entry.getKey().name() + "_" + PREFERENCES_MAX, max);
		}
	}

	@NotNull
	public static Preferences getPreferences(@NotNull Context context) {
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, MODE_PRIVATE);

		final Preferences result = new Preferences();

		for (PreferenceType preferenceType : PreferenceType.values()) {
			for (DragDirection dragDirection : DragDirection.values()) {

				final float defaultMin;
				final float defaultMax;
				switch (preferenceType) {
					case angle:
						switch (dragDirection) {
							case up:
								defaultMin = 150f;
								defaultMax = 180f;
								break;
							case down:
								defaultMin = 0f;
								defaultMax = 30f;
								break;
							default:
								defaultMin = DEFAULT_VALUE;
								defaultMax = DEFAULT_VALUE;
						}
						break;
					case distance:
						defaultMin = 50f;
						defaultMax = 150f;
						break;
					case duration:
						defaultMin = 40f;
						defaultMax = 1000f;
						break;
					default:
						defaultMin = DEFAULT_VALUE;
						defaultMax = DEFAULT_VALUE;
				}

				final float min = preferences.getFloat(preferenceType.name() + "_" + dragDirection.name() + "_" + PREFERENCES_MIN, defaultMin);
				final float max = preferences.getFloat(preferenceType.name() + "_" + dragDirection.name() + "_" + PREFERENCES_MAX, defaultMax);

				if (min != DEFAULT_VALUE && max != DEFAULT_VALUE) {
					final DragPreference directionPreference = new DragPreference(dragDirection, new Interval(min, max));

					Preference preference = result.getPreferencesMap().get(preferenceType);
					if (preference == null) {
						preference = new Preference(preferenceType);
						result.getPreferencesMap().put(preferenceType, preference);
					}

					preference.getDirectionPreferences().put(dragDirection, directionPreference);

				} else {
					Log.e(DragButtonCalibrationActivity.class.getName(), "New preference type added: default preferences should be defined!");
				}
			}
		}

		return result;
	}

	public static class DragPreference {

		@NotNull
		private DragDirection direction;

		@NotNull
		private Interval interval;


		public DragPreference(@NotNull DragDirection direction, @NotNull Interval interval) {
			this.direction = direction;
			this.interval = interval;
		}

		@NotNull
		public DragDirection getDirection() {
			return direction;
		}

		public void setDirection(@NotNull DragDirection direction) {
			this.direction = direction;
		}

		@NotNull
		public Interval getInterval() {
			return interval;
		}

		public void setInterval(@NotNull Interval interval) {
			this.interval = interval;
		}
	}

	public static class Preference {

		@NotNull
		private PreferenceType preferenceType;

		@NotNull
		private Map<DragDirection, DragPreference> directionPreferences = new HashMap<DragDirection, DragPreference>();


		public Preference(@NotNull PreferenceType preferenceType) {
			this.preferenceType = preferenceType;
		}

		@NotNull
		public PreferenceType getPreferenceType() {
			return preferenceType;
		}

		public void setPreferenceType(@NotNull PreferenceType preferenceType) {
			this.preferenceType = preferenceType;
		}

		@NotNull
		public Map<DragDirection, DragPreference> getDirectionPreferences() {
			return directionPreferences;
		}

		public void setDirectionPreferences(@NotNull Map<DragDirection, DragPreference> directionPreferences) {
			this.directionPreferences = directionPreferences;
		}
	}


	public static class Preferences {

		private final Map<PreferenceType, Preference> preferencesMap = new HashMap<PreferenceType, Preference>();

		public Map<PreferenceType, Preference> getPreferencesMap() {
			return preferencesMap;
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
