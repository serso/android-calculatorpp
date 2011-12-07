/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.view.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.R;
import org.solovyev.common.utils.Point2d;
import org.solovyev.common.utils.StringUtils;

import java.util.EnumMap;
import java.util.Map;

/**
 * User: serso
 * Date: 7/17/11
 * Time: 10:25 PM
 */
public class DirectionDragButton extends DragButton {

	@NotNull
	private final static Float DEFAULT_DIRECTION_TEXT_SCALE = 0.33f;

	@Nullable
	private String textMiddle;

	protected static class DirectionTextData {

		@NotNull
		private final GuiDragDirection guiDragDirection;

		@NotNull
		private String text;

		@NotNull
		private Point2d position;

		@NotNull
		private TextPaint paint;

		private DirectionTextData(@NotNull GuiDragDirection guiDragDirection, @NotNull String text) {
			this.guiDragDirection = guiDragDirection;
			this.text = text;
		}

		@NotNull
		public GuiDragDirection getGuiDragDirection() {
			return guiDragDirection;
		}

		@NotNull
		public String getText() {
			return text;
		}

		public void setText(@NotNull String text) {
			this.text = text;
		}

		@NotNull
		public Point2d getPosition() {
			return position;
		}

		public void setPosition(@NotNull Point2d position) {
			this.position = position;
		}

		@NotNull
		public TextPaint getPaint() {
			return paint;
		}

		public void setPaint(@NotNull TextPaint paint) {
			this.paint = paint;
		}
	}

	protected static enum GuiDragDirection {
		up(DragDirection.up) {
			@Override
			public int getAttributeId() {
				return R.styleable.DragButton_textUp;
			}

			@NotNull
			@Override
			public Point2d getTextPosition(@NotNull Paint paint, @NotNull Paint basePaint, @NotNull CharSequence text, CharSequence baseText, int w, int h) {
				return getUpDownTextPosition(paint, basePaint, text, baseText, 1, w, h);
			}
		},
		down(DragDirection.down) {
			@Override
			public int getAttributeId() {
				return R.styleable.DragButton_textDown;
			}

			@NotNull
			@Override
			public Point2d getTextPosition(@NotNull Paint paint, @NotNull Paint basePaint, @NotNull CharSequence text, CharSequence baseText, int w, int h) {
				return getUpDownTextPosition(paint, basePaint, text, baseText, -1, w, h);
			}
		},
		left(DragDirection.left) {
			@Override
			public int getAttributeId() {
				return R.styleable.DragButton_textLeft;
			}

			@NotNull
			@Override
			public Point2d getTextPosition(@NotNull Paint paint, @NotNull Paint basePaint, @NotNull CharSequence text, CharSequence baseText, int w, int h) {
				throw new UnsupportedOperationException("Not implemented yet!");
			}
		}/*,
		right(DragDirection.right) {
			@Override
			public int getAttributeId() {
				return 0;
			}
		}*/;

		@NotNull
		private final DragDirection dragDirection;

		GuiDragDirection(@NotNull DragDirection dragDirection) {
			this.dragDirection = dragDirection;
		}

		public abstract int getAttributeId();

		@NotNull
		public abstract Point2d getTextPosition(@NotNull Paint paint, @NotNull Paint basePaint, @NotNull CharSequence text, CharSequence baseText, int w, int h);

		@NotNull
		private static Point2d getUpDownTextPosition(@NotNull Paint paint, @NotNull Paint basePaint, @NotNull CharSequence text, CharSequence baseText, float direction, int w, int h) {
			final Point2d result = new Point2d();

			float width = paint.measureText(text.toString() + " ");
			result.setX(w - width);

			float selfHeight = paint.ascent() + paint.descent();

			basePaint.measureText(StringUtils.getNotEmpty(baseText, "|"));

			if (direction < 0) {
				result.setY(h / 2 + h / 3 - selfHeight / 2);
			} else {
				result.setY(h / 2 - h / 3 - selfHeight / 2);
			}

			return result;
		}

		@Nullable
		public static GuiDragDirection valueOf ( @NotNull DragDirection dragDirection ) {
			for (GuiDragDirection guiDragDirection : values()) {
				if ( guiDragDirection.dragDirection == dragDirection ) {
					return guiDragDirection;
				}
			}
			return null;
		}
	}

	@NotNull
	private final Map<GuiDragDirection, DirectionTextData> directionTextDataMap = new EnumMap<GuiDragDirection, DirectionTextData>(GuiDragDirection.class);

	@NotNull
	private Float directionTextScale = DEFAULT_DIRECTION_TEXT_SCALE;

	private boolean initialized = false;

	public DirectionDragButton(Context context, @NotNull AttributeSet attrs) {
		super(context, attrs, false);
		init(context, attrs);
	}

	private void init(@NotNull Context context, @NotNull AttributeSet attrs) {

		TypedArray a = context.obtainStyledAttributes(attrs, org.solovyev.android.calculator.R.styleable.DragButton);

		final int N = a.getIndexCount();
		for (int i = 0; i < N; i++) {
			int attr = a.getIndex(i);

			final String attrValue = a.getString(attr);

			if (!StringUtils.isEmpty(attrValue)) {
				switch (attr) {
					case R.styleable.DragButton_directionTextScale:
						this.directionTextScale = Float.valueOf(attrValue);
						break;
					default:
						// try drag direction text
						for (GuiDragDirection guiDragDirection : GuiDragDirection.values()) {
							if ( guiDragDirection.getAttributeId() == attr ) {
								this.directionTextDataMap.put(guiDragDirection, new DirectionTextData(guiDragDirection, attrValue));
								break;
							}
						}
						break;
				}
			}
		}

		// backup text
		this.textMiddle = String.valueOf(getText());

		super.init(context);
		initialized = true;
	}

	@Override
	protected void measureText() {
		super.measureText();

		if (initialized) {
			final Paint basePaint = getPaint();
			final Resources resources = getResources();

			for (DirectionTextData directionTextData : directionTextDataMap.values()) {
				initDirectionTextPaint(basePaint, directionTextData, resources);

				final GuiDragDirection guiDragDirection = directionTextData.getGuiDragDirection();
				final String directionText = directionTextData.getText();
				final Paint directionPaint = directionTextData.getPaint();

				directionTextData.setPosition(guiDragDirection.getTextPosition(directionPaint, basePaint, directionText, getText(), getWidth(), getHeight()));
			}
		}
	}


	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		final TextPaint paint = getPaint();
		final Resources resources = getResources();

		for (DirectionTextData directionTextData : directionTextDataMap.values()) {
			initDirectionTextPaint(paint, directionTextData, resources);
			final String text = directionTextData.getText();
			final Point2d position = directionTextData.getPosition();
			canvas.drawText(text, 0, text.length(), position.getX(), position.getY(), directionTextData.getPaint());
		}
	}

	protected void initDirectionTextPaint(@NotNull Paint basePaint,
										  @NotNull DirectionTextData directionTextData,
										  @NotNull Resources resources) {
		final TextPaint directionTextPaint = new TextPaint(basePaint);

		directionTextPaint.setColor(resources.getColor(R.color.button_text_color));
		directionTextPaint.setAlpha(getDefaultDirectionTextAlpha());
		directionTextPaint.setTextSize(basePaint.getTextSize() * getDirectionTextScale());

		directionTextData.setPaint(directionTextPaint);
	}

	protected static int getDefaultDirectionTextAlpha() {
		return 150;
	}

	@Nullable
	public String getTextUp() {
		return getText(GuiDragDirection.up);
	}

	@Nullable
	public String getTextDown() {
		return getText(GuiDragDirection.down);
	}

	@Nullable
	public String getTextMiddle() {
		return textMiddle;
	}

	@Nullable
	public String getText(@NotNull DragDirection direction) {
		final GuiDragDirection guiDragDirection = GuiDragDirection.valueOf(direction);
		return guiDragDirection == null ? null : getText(guiDragDirection);
	}

	@Nullable
	private String getText(@NotNull GuiDragDirection direction) {
		DirectionTextData directionTextData = this.directionTextDataMap.get(direction);
		return directionTextData == null ? null : directionTextData.getText();
	}


	@NotNull
	public Float getDirectionTextScale() {
		return directionTextScale;
	}

}
