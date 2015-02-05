/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.drag;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MotionEvent;
import org.solovyev.common.MutableObject;
import org.solovyev.common.interval.Interval;
import org.solovyev.common.interval.Intervals;
import org.solovyev.common.math.Maths;
import org.solovyev.common.math.Point2d;
import org.solovyev.common.text.Mapper;
import org.solovyev.common.text.NumberIntervalMapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class SimpleDragListener implements DragListener, DragPreferencesChangeListener {

	@Nonnull
	public static final Point2d axis = new Point2d(0, 1);

	@Nonnull
	private DragProcessor dragProcessor;

	@Nonnull
	private Preferences preferences;

	public SimpleDragListener(@Nonnull DragProcessor dragProcessor, @Nonnull Preferences preferences) {
		this.dragProcessor = dragProcessor;
		this.preferences = preferences;
	}

	@Override
	public boolean onDrag(@Nonnull DragButton dragButton, @Nonnull DragEvent event) {
		boolean result = false;

		logDragEvent(dragButton, event);

		final Point2d startPoint = event.getStartPoint();
		final MotionEvent motionEvent = event.getMotionEvent();

		// init end point
		final Point2d endPoint = new Point2d(motionEvent.getX(), motionEvent.getY());

		final float distance = Maths.getDistance(startPoint, endPoint);

		final MutableObject<Boolean> right = new MutableObject<Boolean>();
		final double angle = Math.toDegrees(Maths.getAngle(startPoint, Maths.sum(startPoint, axis), endPoint, right));
		Log.d(String.valueOf(dragButton.getId()), "Angle: " + angle);
		Log.d(String.valueOf(dragButton.getId()), "Is right?: " + right.getObject());

		final double duration = motionEvent.getEventTime() - motionEvent.getDownTime();

		final Preference distancePreferences = preferences.getPreferencesMap().get(PreferenceType.distance);
		final Preference anglePreferences = preferences.getPreferencesMap().get(PreferenceType.angle);

		DragDirection direction = null;
		for (Map.Entry<DragDirection, DragPreference> directionEntry : distancePreferences.getDirectionPreferences().entrySet()) {

			Log.d(String.valueOf(dragButton.getId()), "Drag direction: " + directionEntry.getKey());
			Log.d(String.valueOf(dragButton.getId()), "Trying direction interval: " + directionEntry.getValue().getInterval());

			if (directionEntry.getValue().getInterval().contains(distance)) {
				final DragPreference anglePreference = anglePreferences.getDirectionPreferences().get(directionEntry.getKey());

				Log.d(String.valueOf(dragButton.getId()), "Trying angle interval: " + anglePreference.getInterval());

				if (directionEntry.getKey() == DragDirection.left && right.getObject()) {
				} else if (directionEntry.getKey() == DragDirection.right && !right.getObject()) {
				} else {
					if (anglePreference.getInterval().contains((float) angle)) {
						direction = directionEntry.getKey();
						Log.d(String.valueOf(dragButton.getId()), "MATCH! Direction: " + direction);
						break;
					}
				}
			}

		}

		if (direction != null) {
			final Preference durationPreferences = preferences.getPreferencesMap().get(PreferenceType.duration);

			final DragPreference durationDragPreferences = durationPreferences.getDirectionPreferences().get(direction);

			Log.d(String.valueOf(dragButton.getId()), "Trying time interval: " + durationDragPreferences.getInterval());
			if (durationDragPreferences.getInterval().contains((float) duration)) {
				Log.d(String.valueOf(dragButton.getId()), "MATCH!");
				result = dragProcessor.processDragEvent(direction, dragButton, startPoint, motionEvent);
			}
		}

		return result;
	}

	@Override
	public boolean isSuppressOnClickEvent() {
		return true;
	}

	private void logDragEvent(@Nonnull DragButton dragButton, @Nonnull DragEvent event) {
		final Point2d startPoint = event.getStartPoint();
		final MotionEvent motionEvent = event.getMotionEvent();
		final Point2d endPoint = new Point2d(motionEvent.getX(), motionEvent.getY());

		Log.d(String.valueOf(dragButton.getId()), "Start point: " + startPoint + ", End point: " + endPoint);
		Log.d(String.valueOf(dragButton.getId()), "Distance: " + Maths.getDistance(startPoint, endPoint));
		final MutableObject<Boolean> right = new MutableObject<Boolean>();
		Log.d(String.valueOf(dragButton.getId()), "Angle: " + Math.toDegrees(Maths.getAngle(startPoint, Maths.sum(startPoint, axis), endPoint, right)));
		Log.d(String.valueOf(dragButton.getId()), "Is right angle? " + right);
		Log.d(String.valueOf(dragButton.getId()), "Axis: " + axis + " Vector: " + Maths.subtract(endPoint, startPoint));
		Log.d(String.valueOf(dragButton.getId()), "Total time: " + (motionEvent.getEventTime() - motionEvent.getDownTime()) + " ms");
	}

	@Override
	public void onDragPreferencesChange(@Nonnull Preferences preferences) {
		this.preferences = preferences;
	}

	public interface DragProcessor {

		boolean processDragEvent(@Nonnull DragDirection dragDirection, @Nonnull DragButton dragButton, @Nonnull Point2d startPoint2d, @Nonnull MotionEvent motionEvent);
	}

	// todo serso: currently we do not use direction
	public static String getPreferenceId(@Nonnull PreferenceType preferenceType, @Nonnull DragDirection direction) {
		return "org.solovyev.android.calculator.DragButtonCalibrationActivity" + "_" + preferenceType.name() /*+ "_" + direction.name()*/;
	}

	@Nonnull
	public static Preferences getDefaultPreferences(@Nonnull Context context) {
		return getPreferences0(null, context);
	}

	@Nonnull
	public static Preferences getPreferences(@Nonnull final SharedPreferences preferences, @Nonnull Context context) {
		return getPreferences0(preferences, context);
	}

	@Nonnull
	private static Preferences getPreferences0(@Nullable final SharedPreferences preferences, @Nonnull Context context) {

		final Mapper<Interval<Float>> mapper = NumberIntervalMapper.of(Float.class);

		final Preferences result = new Preferences();

		for (PreferenceType preferenceType : PreferenceType.values()) {
			for (DragDirection dragDirection : DragDirection.values()) {

				final String preferenceId = getPreferenceId(preferenceType, dragDirection);

				final String defaultValue;
				switch (preferenceType) {
					case angle:
						defaultValue = context.getResources().getString(org.solovyev.android.view.R.string.p_drag_angle);
						break;
					case distance:
						defaultValue = context.getResources().getString(org.solovyev.android.view.R.string.p_drag_distance);
						break;
					case duration:
						defaultValue = context.getResources().getString(org.solovyev.android.view.R.string.p_drag_duration);
						break;
					default:
						defaultValue = null;
						Log.e(SimpleDragListener.class.getName(), "New preference type added: default preferences should be defined. Preference id: " + preferenceId);
				}

				final String value = preferences == null ? defaultValue : preferences.getString(preferenceId, defaultValue);

				if (value != null) {
					final Interval<Float> intervalPref = transformInterval(preferenceType, dragDirection, mapper.parseValue(value));

					Log.d(SimpleDragListener.class.getName(), "Preference loaded for " + dragDirection + ". Id: " + preferenceId + ", value: " + intervalPref.toString());

					final DragPreference directionPreference = new DragPreference(intervalPref);

					Preference preference = result.getPreferencesMap().get(preferenceType);
					if (preference == null) {
						preference = new Preference();
						result.getPreferencesMap().put(preferenceType, preference);
					}

					preference.getDirectionPreferences().put(dragDirection, directionPreference);
				}
			}
		}

		return result;
	}

	@Nonnull
	public static Interval<Float> transformInterval(@Nonnull PreferenceType preferenceType,
													@Nonnull DragDirection dragDirection,
													@Nonnull Interval<Float> interval) {

		if (preferenceType == PreferenceType.angle) {
			final Float leftLimit = interval.getLeftLimit();
			final Float rightLimit = interval.getRightLimit();

			if (leftLimit != null && rightLimit != null) {
				final Float newLeftLimit;
				final Float newRightLimit;

				if (dragDirection == DragDirection.up) {
					newLeftLimit = 180f - rightLimit;
					newRightLimit = 180f - leftLimit;
				} else if (dragDirection == DragDirection.left) {
					newLeftLimit = 90f - rightLimit;
					newRightLimit = 90f + rightLimit;
				} else if (dragDirection == DragDirection.right) {
					newLeftLimit = 90f - rightLimit;
					newRightLimit = 90f + rightLimit;
				} else {
					newLeftLimit = leftLimit;
					newRightLimit = rightLimit;
				}

				return Intervals.newClosedInterval(newLeftLimit, newRightLimit);
			}
		}

		return interval;
	}


	public static enum PreferenceType {
		angle,
		distance,
		duration
	}

	public static class DragPreference {

		@Nonnull
		private Interval<Float> interval;


		public DragPreference(@Nonnull Interval<Float> interval) {
			this.interval = interval;
		}

		@Nonnull
		public Interval<Float> getInterval() {
			return interval;
		}

	}

	public static class Preference {

		@Nonnull
		private Map<DragDirection, DragPreference> directionPreferences = new HashMap<DragDirection, DragPreference>();


		@Nonnull
		public Map<DragDirection, DragPreference> getDirectionPreferences() {
			return directionPreferences;
		}

	}

	public static class Preferences {

		private final Map<PreferenceType, Preference> preferencesMap = new HashMap<>();

		public Map<PreferenceType, Preference> getPreferencesMap() {
			return preferencesMap;
		}
	}
}