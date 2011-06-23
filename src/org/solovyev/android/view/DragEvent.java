package org.solovyev.android.view;

import org.jetbrains.annotations.NotNull;
import org.solovyev.util.math.Point2d;

import android.view.MotionEvent;

public class DragEvent {
	
	@NotNull
	private final Point2d startPoint;
	
	@NotNull
	private final MotionEvent motionEvent;

	public DragEvent(@NotNull Point2d startPoint, @NotNull MotionEvent motionEvent) {
		this.startPoint = startPoint;
		this.motionEvent = motionEvent;
	}

	@NotNull
	public MotionEvent getMotionEvent() {
		return motionEvent;
	}

	@NotNull
	public Point2d getStartPoint() {
		return startPoint;
	}
	
	
	
}
