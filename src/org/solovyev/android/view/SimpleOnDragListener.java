package org.solovyev.android.view;

import org.jetbrains.annotations.NotNull;
import org.solovyev.util.math.MathUtils;
import org.solovyev.util.math.Point2d;

import android.util.Log;
import android.view.MotionEvent;

public class SimpleOnDragListener implements OnDragListener {

	@NotNull
	public static final Point2d axis = new Point2d(0, 1);

	private float minDragDist = 20f;

	private float maxDragDist = 80f;

	// max angle (in degrees!) between start and end point vector and axis
	// vector to register drag event
	private double maxAngle = 30;
	
	@NotNull
	private DragProcessor dragProcessor;

	public SimpleOnDragListener() {
	}

	public SimpleOnDragListener(@NotNull DragProcessor dragProcessor) {
		this.dragProcessor = dragProcessor;
	}

	@Override
	public boolean onDrag(@NotNull DragButton dragButton, @NotNull DragEvent event) {
		boolean result = false;

		logDragEvent(dragButton, event);

		final Point2d startPoint = event.getStartPoint();
		final MotionEvent motionEvent = event.getMotionEvent();

		// init end point
		final Point2d endPoint = new Point2d(motionEvent.getX(), motionEvent.getY());

		float distance = MathUtils.getDistance(startPoint, endPoint);

		if (minDragDist < distance && distance < maxDragDist) {
			double angle = Math.toDegrees(MathUtils.getAngle(startPoint, MathUtils.sum(startPoint, axis), endPoint));

			final DragDirection direction;
			if (angle < maxAngle) {
				direction = DragDirection.down;
			} else if (180 - angle < maxAngle) {
				direction = DragDirection.up;
			} else {
				direction = null;
			}

			if (direction != null) {
				result = dragProcessor.processDragEvent(direction, dragButton, startPoint, motionEvent);
			}
		}

		return result;
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