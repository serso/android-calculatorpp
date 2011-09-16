package org.solovyev.android.view;

import android.util.Log;
import android.view.MotionEvent;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.DragButtonCalibrationActivity;
import org.solovyev.common.utils.Interval;
import org.solovyev.common.utils.MathUtils;
import org.solovyev.common.utils.Point2d;

import java.util.Map;

public class SimpleOnDragListener implements OnDragListener {

	@NotNull
	public static final Point2d axis = new Point2d(0, 1);

	@NotNull
	private DragProcessor dragProcessor;

	@NotNull
	private DragButtonCalibrationActivity.Preferences preferences;

	public SimpleOnDragListener(@NotNull DragButtonCalibrationActivity.Preferences preferences) {
		this.preferences = preferences;
	}

	public SimpleOnDragListener(@NotNull DragProcessor dragProcessor, @NotNull DragButtonCalibrationActivity.Preferences preferences) {
		this.dragProcessor = dragProcessor;
		this.preferences = preferences;
	}

	public void setPreferences(@NotNull DragButtonCalibrationActivity.Preferences preferences) {
		this.preferences = preferences;
	}

	@Override
	public boolean onDrag(@NotNull DragButton dragButton, @NotNull DragEvent event) {
		boolean result = false;

		logDragEvent(dragButton, event);

		final Point2d startPoint = event.getStartPoint();
		final MotionEvent motionEvent = event.getMotionEvent();

		// init end point
		final Point2d endPoint = new Point2d(motionEvent.getX(), motionEvent.getY());

		final float distance = MathUtils.getDistance(startPoint, endPoint);
		final double angle = Math.toDegrees(MathUtils.getAngle(startPoint, MathUtils.sum(startPoint, axis), endPoint));
		final double duration = motionEvent.getEventTime() - motionEvent.getDownTime();

		final DragButtonCalibrationActivity.Preference distancePreferences = preferences.getPreferencesMap().get(DragButtonCalibrationActivity.PreferenceType.distance);
		final DragButtonCalibrationActivity.Preference anglePreferences = preferences.getPreferencesMap().get(DragButtonCalibrationActivity.PreferenceType.angle);

		DragDirection direction = null;
		for (Map.Entry<DragDirection, DragButtonCalibrationActivity.DragPreference> directionEntry : distancePreferences.getDirectionPreferences().entrySet()) {

			if (isInInterval(directionEntry.getValue().getInterval(), distance)) {
				for (Map.Entry<DragDirection, DragButtonCalibrationActivity.DragPreference> angleEntry : anglePreferences.getDirectionPreferences().entrySet()) {
					if (isInInterval(angleEntry.getValue().getInterval(), (float)angle)) {
						direction = angleEntry.getKey();
						break;
					}
				}
			}

			if (direction != null) {
				break;
			}
		}

		if (direction != null) {
			final DragButtonCalibrationActivity.Preference durationPreferences = preferences.getPreferencesMap().get(DragButtonCalibrationActivity.PreferenceType.duration);

			final DragButtonCalibrationActivity.DragPreference durationDragPreferences = durationPreferences.getDirectionPreferences().get(direction);

			if (isInInterval(durationDragPreferences.getInterval(), (float)duration)) {
				result = dragProcessor.processDragEvent(direction, dragButton, startPoint, motionEvent);

			}
		}

		return result;
	}

	private boolean isInInterval(@NotNull Interval interval, float value) {
		return interval.getStart() - MathUtils.MIN_AMOUNT <= value && value <= interval.getEnd() + MathUtils.MIN_AMOUNT;
	}

	@Override
	public boolean isSuppressOnClickEvent() {
		return true;
	}

	private void logDragEvent(@NotNull DragButton dragButton, @NotNull DragEvent event) {
		final Point2d startPoint = event.getStartPoint();
		final MotionEvent motionEvent = event.getMotionEvent();
		final Point2d endPoint = new Point2d(motionEvent.getX(), motionEvent.getY());

		Log.d(String.valueOf(dragButton.getId()), "Start point: " + startPoint + ", End point: " + endPoint);
		Log.d(String.valueOf(dragButton.getId()), "Distance: " + MathUtils.getDistance(startPoint, endPoint));
		Log.d(String.valueOf(dragButton.getId()), "Angle: " + Math.toDegrees(MathUtils.getAngle(startPoint, MathUtils.sum(startPoint, axis), endPoint)));
		Log.d(String.valueOf(dragButton.getId()), "Axis: " + axis + " Vector: " + MathUtils.subtract(endPoint, startPoint));
		Log.d(String.valueOf(dragButton.getId()), "Total time: " + (motionEvent.getEventTime() - motionEvent.getDownTime()) + " ms");
	}

	@NotNull
	public DragProcessor getDragProcessor() {
		return dragProcessor;
	}

	public void setDragProcessor(@NotNull DragProcessor dragProcessor) {
		this.dragProcessor = dragProcessor;
	}

	public interface DragProcessor {

		boolean processDragEvent(@NotNull DragDirection dragDirection, @NotNull DragButton dragButton, @NotNull Point2d startPoint2d, @NotNull MotionEvent motionEvent);
	}
}