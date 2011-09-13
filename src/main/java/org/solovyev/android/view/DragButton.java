package org.solovyev.android.view;

import android.os.Handler;
import android.widget.Button;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.util.math.Point2d;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DragButton extends ColorButton {

	@Nullable
	private Point2d startPoint = null;

	@Nullable
	private OnDragListener onDragListener;

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

	public void setOnDragListener(@Nullable OnDragListener onDragListener) {
		this.onDragListener = onDragListener;
	}

	@Nullable
	public OnDragListener getOnDragListener() {
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
			final OnDragListener localOnDragListener = onDragListener;

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
							}


							if (v instanceof Button) {
								final Button button = (Button)v;

								button.setEnabled(false);

								new Handler().postDelayed(new Runnable() {
									public void run() {
										button.setEnabled(true);
									}
								}, 500);
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
