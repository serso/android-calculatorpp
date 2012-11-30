/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.view.drag;

import android.view.MotionEvent;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.math.Point2d;

public class DragEvent {

	@NotNull
	private final Point2d startPoint;

	@NotNull
	private final MotionEvent motionEvent;

	public DragEvent(@NotNull Point2d startPoint, @NotNull MotionEvent motionEvent) {
		this.startPoint = startPoint;
		this.motionEvent = motionEvent;
	}

	/**
	 * @return motion event started at start point
	 */
	@NotNull
	public MotionEvent getMotionEvent() {
		return motionEvent;
	}

	/**
	 * @return start point of dragging
	 */
	@NotNull
	public Point2d getStartPoint() {
		return startPoint;
	}


}
