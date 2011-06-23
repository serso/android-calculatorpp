package org.solovyev.android.view;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.R;
import org.solovyev.util.StringUtils;
import org.solovyev.util.math.MathUtils;
import org.solovyev.util.math.Point2d;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class DragButton extends Button {

	// max time in ms to register drag event
	private long maxTime = 700;

	@Nullable
	private Point2d startPoint = null;

	@Nullable
	private OnDragListener onDragListener;

	private final OnTouchListener onTouchListener = new OnTouchListenerImpl();

	@Nullable
	private String textUp;

	@Nullable
	private String textDown;

	@Nullable
	private String textMiddle;

	public DragButton(Context context, @NotNull AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public DragButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(@NotNull Context context, @NotNull AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DragButton);

		final int N = a.getIndexCount();
		for (int i = 0; i < N; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
				case R.styleable.DragButton_textUp:
					this.textUp = a.getString(attr);
					break;
				case R.styleable.DragButton_textDown:
					this.textDown = a.getString(attr);
					break;
			}
		}

		// backup text
		this.textMiddle = String.valueOf(getText());

		setText(Html.fromHtml(getStyledUpDownText(this.textUp) + "<br><b>" + StringUtils.getNotEmpty(this.textMiddle, "&nbsp;") + "</b><br>" + getStyledUpDownText(this.textDown)));

		// change top padding in order to show all text
		setPadding(getPaddingLeft(), -7, getPaddingRight(), getPaddingBottom());

		setOnTouchListener(this.onTouchListener);
	}

	private String getStyledUpDownText(@Nullable String text) {
		final StringBuilder sb = new StringBuilder();

		sb.append("<font color='#585858'><small><small>");
		sb.append(StringUtils.getNotEmpty(text, "&nbsp;"));
		sb.append("</small></small></font>");
		return sb.toString();
	}

	public void setOnDragListener(@Nullable OnDragListener onDragListener) {
		this.onDragListener = onDragListener;
	}

	@Nullable
	public OnDragListener getOnDragListener() {
		return onDragListener;
	}

	public void setTextUp(String textUp) {
		this.textUp = textUp;
	}

	public String getTextUp() {
		return textUp;
	}

	public void setTextDown(String textDown) {
		this.textDown = textDown;
	}

	public String getTextDown() {
		return textDown;
	}

	public void setTextMiddle(String textMiddle) {
		this.textMiddle = textMiddle;
	}

	public String getTextMiddle() {
		return textMiddle;
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

			if (onDragListener != null) {
				// only if onDrag() listener specified

				Log.d(String.valueOf(getId()), "onTouch() for: " + getId() + " . Motion event: " + event);

				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						// start tracking: set start point
						startPoint = new Point2d(event.getX(), event.getY());
						break;

					case MotionEvent.ACTION_MOVE:
						if (event.getEventTime() - event.getDownTime() > maxTime) {
							// do not allow very long touch movements
							startPoint = null;
						}
						break;

					case MotionEvent.ACTION_UP:
						// stop tracking
							
						if (onDragListener.onDrag(DragButton.this, new DragEvent(startPoint, event))) {
							if (onDragListener.isSuppressOnClickEvent()) {
								// prevent on click action
								setPressed(false);
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
