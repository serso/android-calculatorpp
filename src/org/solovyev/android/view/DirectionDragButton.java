package org.solovyev.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.util.AttributeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.R;
import org.solovyev.util.StringUtils;

/**
 * User: serso
 * Date: 7/17/11
 * Time: 10:25 PM
 */
public class DirectionDragButton extends DragButton {

	@Nullable
	private String textUp;

	@Nullable
	private String textDown;

	@Nullable
	private String textMiddle;

	public DirectionDragButton(Context context, @NotNull AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public DirectionDragButton(Context context, AttributeSet attrs, int defStyle) {
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
	}

	private String getStyledUpDownText(@Nullable String text) {
		final StringBuilder sb = new StringBuilder();

		sb.append("<font color='#585858'><small><small>");
		sb.append(StringUtils.getNotEmpty(text, "&nbsp;"));
		sb.append("</small></small></font>");
		return sb.toString();
	}

	public void setTextUp(@Nullable String textUp) {
		this.textUp = textUp;
	}

	@Nullable
	public String getTextUp() {
		return textUp;
	}

	public void setTextDown(@Nullable String textDown) {
		this.textDown = textDown;
	}

	@Nullable
	public String getTextDown() {
		return textDown;
	}

	public void setTextMiddle(@Nullable String textMiddle) {
		this.textMiddle = textMiddle;
	}

	@Nullable
	public String getTextMiddle() {
		return textMiddle;
	}
}
