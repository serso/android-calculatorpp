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

package org.solovyev.android.calculator.plot;

import android.view.MotionEvent;
import android.view.VelocityTracker;

import javax.annotation.Nonnull;

import org.solovyev.android.Views;

class TouchHandler {

	static interface TouchHandlerListener {
		void onTouchDown(float x, float y);

		void onTouchMove(float x, float y);

		void onTouchUp(float x, float y);

		void onTouchZoomDown(float x1, float y1, float x2, float y2);

		void onTouchZoomMove(float x1, float y1, float x2, float y2);
	}

	@Nonnull
	private final VelocityTracker velocityTracker = VelocityTracker.obtain();

	private boolean afterZoom;

	@Nonnull
	private TouchHandlerListener listener;

	TouchHandler(@Nonnull TouchHandlerListener listener) {
		this.listener = listener;
	}

	public boolean handleTouchEvent(@Nonnull MotionEvent event) {
		// Calculator.log("touch " + event + ' ' + event.getPointerCount() + event.getPointerId(0));

		final int fullAction = event.getAction();
		final int action = fullAction & MotionEvent.ACTION_MASK;
		final int pointer = (fullAction & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;

		float x = event.getX();
		float y = event.getY();

		int pointerCount = Views.getPointerCountFromMotionEvent(event);

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				afterZoom = false;
				velocityTracker.clear();
				velocityTracker.addMovement(event);
				listener.onTouchDown(x, y);
				break;

			case MotionEvent.ACTION_MOVE:
				if (pointerCount == 1) {
					if (afterZoom) {
						velocityTracker.clear();
						listener.onTouchDown(x, y);
						afterZoom = false;
					}
					velocityTracker.addMovement(event);
					listener.onTouchMove(x, y);
				} else if (pointerCount == 2) {
					listener.onTouchZoomMove(x, y, Views.getXFromMotionEvent(event, 1), Views.getYFromMotionEvent(event, 1));
				}
				break;

			case MotionEvent.ACTION_UP:
				velocityTracker.addMovement(event);
				velocityTracker.computeCurrentVelocity(1000);
				listener.onTouchUp(x, y);
				break;

			case MotionEvent.ACTION_POINTER_DOWN:
				if (pointerCount == 2) {
					listener.onTouchZoomDown(x, y, Views.getXFromMotionEvent(event, 1), Views.getYFromMotionEvent(event, 1));
				}
				break;

			case MotionEvent.ACTION_POINTER_UP:
				if (pointerCount == 2) {
					afterZoom = true;
				}
				break;
		}
		return true;
	}

	public float getXVelocity() {
		return velocityTracker.getXVelocity();
	}

	public float getYVelocity() {
		return velocityTracker.getYVelocity();
	}
}
