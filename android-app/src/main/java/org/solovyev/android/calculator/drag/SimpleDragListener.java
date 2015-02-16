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
import android.support.v4.view.ViewConfigurationCompat;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import org.solovyev.android.calculator.R;
import org.solovyev.common.MutableObject;
import org.solovyev.common.interval.Interval;
import org.solovyev.common.interval.Intervals;
import org.solovyev.common.math.Maths;
import org.solovyev.common.math.Point2d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.EnumMap;

public class SimpleDragListener implements DragListener {

	@Nonnull
	private static final Point2d axis = new Point2d(0, 1);

	@Nonnull
	private static final EnumMap<DragDirection, Interval<Float>> sAngleIntervals = new EnumMap<>(DragDirection.class);

	static {
		for (DragDirection direction : DragDirection.values()) {
			sAngleIntervals.put(direction, makeAngleInterval(direction, 0, 45));
		}
	}

	@Nonnull
	private final DragProcessor processor;

	private final float minDistancePxs;

	public SimpleDragListener(@Nonnull DragProcessor processor, @Nonnull Context context) {
		this.processor = processor;
		this.minDistancePxs = context.getResources().getDimensionPixelSize(R.dimen.cpp_min_drag_distance);
	}

	@Override
	public boolean onDrag(@Nonnull DragButton dragButton, @Nonnull DragEvent event) {
		boolean consumed = false;

		final MotionEvent motionEvent = event.getMotionEvent();

		final Point2d start = event.getStartPoint();
		final Point2d end = new Point2d(motionEvent.getX(), motionEvent.getY());
		final float distance = Maths.getDistance(start, end);

		final MutableObject<Boolean> right = new MutableObject<>();
		final double angle = Math.toDegrees(Maths.getAngle(start, Maths.sum(start, axis), end, right));

		final long duration = motionEvent.getEventTime() - motionEvent.getDownTime();
		final DragDirection direction = getDirection(distance, (float) angle, right.getObject());
		if (direction != null && duration > 40 && duration < 2500) {
			consumed = processor.processDragEvent(direction, dragButton, start, motionEvent);
		}

		return consumed;
	}

	@Nullable
	private DragDirection getDirection(float distance, float angle, boolean right) {
		if (distance > minDistancePxs) {
			for (DragDirection direction : DragDirection.values()) {
				final Interval<Float> angleInterval = sAngleIntervals.get(direction);
				final boolean wrongDirection = (direction == DragDirection.left && right) ||
						(direction == DragDirection.right && !right);
				if (!wrongDirection && angleInterval.contains(angle)) {
					return direction;
				}
			}
		}
		return null;
	}

	@Override
	public boolean isSuppressOnClickEvent() {
		return true;
	}

	public interface DragProcessor {

		boolean processDragEvent(@Nonnull DragDirection dragDirection, @Nonnull DragButton dragButton, @Nonnull Point2d startPoint2d, @Nonnull MotionEvent motionEvent);
	}

	@Nonnull
	private static Interval<Float> makeAngleInterval(@Nonnull DragDirection direction,
													 float leftLimit,
													 float rightLimit) {
		final Float newLeftLimit;
		final Float newRightLimit;
		switch (direction) {
			case up:
				newLeftLimit = 180f - rightLimit;
				newRightLimit = 180f - leftLimit;
				break;
			case down:
				newLeftLimit = leftLimit;
				newRightLimit = rightLimit;
				break;
			case left:
				newLeftLimit = 90f - rightLimit;
				newRightLimit = 90f + rightLimit;
				break;
			case right:
				newLeftLimit = 90f - rightLimit;
				newRightLimit = 90f + rightLimit;
				break;
			default:
				throw new AssertionError();
		}

		return Intervals.newClosedInterval(newLeftLimit, newRightLimit);
	}
}