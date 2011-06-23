package org.solovyev.android.view;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.util.StringUtils;
import org.solovyev.util.math.MathUtils;
import org.solovyev.util.math.Point2d;

import android.util.Log;
import android.view.MotionEvent;

public class SimpleOnDragListener implements OnDragListener {

	@NotNull
	private final Point2d axis = new Point2d(0, 1);
	
	private float minDragDist = 20f;

	private float maxDragDist = 80f;

	// max angle (in degrees!) between start and end point vector and axis
	// vector to register drag event
	private double maxAngle = 30;

	@Override
	public boolean onDrag(@NotNull DragButton dragButton, @NotNull DragEvent event) {
		logDragEvent(dragButton, event);
		
		processButtonAction(dragButton, getActionText(dragButton, event));
	}

	@Override
	public boolean isSuppressOnClickEvent() {
		return true;
	}
	
	/**
	 * Method creates drag event in case if all conditions are satisfied
	 * 
	 * @param event
	 *            motion event
	 * 
	 * @return filled drag event object only if drag event is possible, null
	 *         otherwise
	 */
	@Nullable
	protected DragEvent getDragEvent(@NotNull MotionEvent event) {
		DragEvent result = null;

		if (startPoint != null) {
			final Point2d endPoint = new Point2d(event.getX(), event.getY());
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
					if ( direction == DragDirection.up && StringUtils.isEmpty(textUp) ) {
						// no action if text is empty
					} else if  (direction == DragDirection.down && StringUtils.isEmpty(textDown)) {
						// no action if text is empty
					} else {
						result = new DragEvent(direction);
					}
					
				}
			}
		}

		return result;
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
}