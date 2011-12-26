/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.view.widgets;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.Point2d;

public class DragButton extends ColorButton {

	@Nullable
	private Point2d startPoint = null;

	@Nullable
	private org.solovyev.android.view.widgets.OnDragListener onDragListener;

	private final OnTouchListener onTouchListener = new OnTouchListenerImpl();

	public DragButton(Context context, @NotNull AttributeSet attrs) {
		this(context, attrs, true);
	}

	public DragButton(Context context, @NotNull AttributeSet attrs, boolean init) {
		super(context, attrs, false);
		setOnTouchListener(this.onTouchListener);
		if ( init ) {
			super.init(context);
		}
	}

	public void setOnDragListener(@Nullable org.solovyev.android.view.widgets.OnDragListener onDragListener) {
		this.onDragListener = onDragListener;
	}

	@Nullable
	public org.solovyev.android.view.widgets.OnDragListener getOnDragListener() {
		return onDragListener;
	}

	/**
	 * OnTouchListener implementation that fires onDrag()
	 * 
	 * @author serso
	 * 
	 */
	private final class OnTouchListenerImpl implements OnTouchListener {

		@Override
		public boolean onTouch(@NotNull View v, @NotNull MotionEvent event) {
			// processing on touch event

			// in order to avoid possible NPEs
			final Point2d localStartPoint = startPoint;
			final org.solovyev.android.view.widgets.OnDragListener localOnDragListener = onDragListener;

			if (localOnDragListener != null) {
				// only if onDrag() listener specified

				Log.d(String.valueOf(getId()), "onTouch() for: " + getId() + " . Motion event: " + event);

				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						// start tracking: set start point
						startPoint = new Point2d(event.getX(), event.getY());
						break;

					case MotionEvent.ACTION_UP:
						// stop tracking

						if (localStartPoint != null && localOnDragListener.onDrag(DragButton.this, new DragEvent(localStartPoint, event))) {
							if (localOnDragListener.isSuppressOnClickEvent()) {
								// prevent on click action
								setPressed(false);

								// sometimes setPressed(false); doesn't work so to prevent onClick action button disables
								if (v instanceof Button) {
									final Button button = (Button) v;

									button.setEnabled(false);

									new Handler().postDelayed(new Runnable() {
										public void run() {
											button.setEnabled(true);
										}
									}, 200);
								}
							}
						}

						startPoint = null;
						break;
				}
			}

			return false;
		}
	}
}
