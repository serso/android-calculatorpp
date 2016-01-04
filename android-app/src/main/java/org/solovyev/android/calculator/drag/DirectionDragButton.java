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
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

import org.solovyev.common.math.Point2d;
import org.solovyev.common.text.NumberParser;
import org.solovyev.common.text.StringCollections;
import org.solovyev.common.text.Strings;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DirectionDragButton extends DragButton {

    @Nonnull
    private final static Float DEFAULT_DIRECTION_TEXT_SCALE_FLOAT = 0.33f;

    @Nonnull
    private final static Integer DEFAULT_DIRECTION_TEXT_ALPHA = 140;

    private final static int DEFAULT_DIRECTION_TEXT_COLOR = Color.WHITE;

    @Nonnull
    private final static String DEFAULT_DIRECTION_TEXT_SCALE = "0.33;0.33;0.33;0.33";
    @Nonnull
    private final Map<GuiDragDirection, DirectionTextData> textDataMap = new EnumMap<>(GuiDragDirection.class);
    @Nonnull
    protected String directionTextScale = DEFAULT_DIRECTION_TEXT_SCALE;
    @Nonnull
    protected Integer directionTextAlpha = DEFAULT_DIRECTION_TEXT_ALPHA;
    protected int directionTextColor = DEFAULT_DIRECTION_TEXT_COLOR;
    private boolean initialized = false;

    public DirectionDragButton(Context context, @Nonnull AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(@Nonnull Context context, @Nonnull AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs, org.solovyev.android.view.R.styleable.DirectionDragButton);

        for (int i = 0; i < a.getIndexCount(); i++) {
            int attr = a.getIndex(i);

            if (a.hasValue(attr)) {
                if (attr == org.solovyev.android.view.R.styleable.DirectionDragButton_directionTextColor) {
                    this.directionTextColor = a.getColor(attr, DEFAULT_DIRECTION_TEXT_COLOR);
                } else if (attr == org.solovyev.android.view.R.styleable.DirectionDragButton_directionTextScale) {
                    this.directionTextScale = a.getString(attr);
                } else if (attr == org.solovyev.android.view.R.styleable.DirectionDragButton_directionTextAlpha) {
                    this.directionTextAlpha = a.getInt(attr, DEFAULT_DIRECTION_TEXT_ALPHA);
                } else {
                    // try drag direction text
                    for (GuiDragDirection guiDragDirection : GuiDragDirection.values()) {
                        if (guiDragDirection.getAttributeId() == attr) {
                            this.textDataMap.put(guiDragDirection, new DirectionTextData(guiDragDirection, a.getString(attr)));
                            break;
                        }
                    }
                }
            }
        }

        a.recycle();

        for (Map.Entry<GuiDragDirection, Float> entry : getDirectionTextScales().entrySet()) {
            final DirectionTextData td = textDataMap.get(entry.getKey());
            if (td != null) {
                td.scale = entry.getValue();
            }
        }

        initialized = true;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        measureText();
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        measureText();
    }

    protected void measureText() {
        if (initialized) {
            final Paint basePaint = getPaint();
            for (DirectionTextData textData : textDataMap.values()) {
                initDirectionTextPaint(basePaint, textData);
                textData.position = textData.direction.getTextPosition(textData.paint, basePaint, textData.text, getText(), getWidth(), getHeight());
            }
            invalidate();
        }
    }

    protected void initDirectionTextPaint(@Nonnull Paint basePaint, @Nonnull DirectionTextData textData) {
        textData.init(basePaint, directionTextColor, directionTextAlpha);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final TextPaint paint = getPaint();
        for (DirectionTextData td : textDataMap.values()) {
            if (td.show) {
                initDirectionTextPaint(paint, td);
                final String text = td.text;
                final Point2d position = td.position;
                canvas.drawText(text, 0, text.length(), position.getX(), position.getY(), td.paint);
            }
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @Nullable
    public String getTextUp() {
        return getText(GuiDragDirection.up);
    }

    @SuppressWarnings("UnusedDeclaration")
    @Nullable
    public String getTextDown() {
        return getText(GuiDragDirection.down);
    }

    @Nullable
    public String getText(@Nonnull DragDirection direction) {
        final GuiDragDirection guiDragDirection = GuiDragDirection.valueOf(direction);
        return guiDragDirection == null ? null : getText(guiDragDirection);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void showDirectionText(boolean show, @Nonnull DragDirection direction) {
        final GuiDragDirection guiDragDirection = GuiDragDirection.valueOf(direction);
        final DirectionTextData td = this.textDataMap.get(guiDragDirection);
        if (td != null) {
            td.show = show;
        }
    }

    public void setText(@Nullable String text, @Nonnull GuiDragDirection direction) {
        if (!TextUtils.isEmpty(text)) {
            final DirectionTextData data = new DirectionTextData(direction, text);
            initDirectionTextPaint(getPaint(), data);
            textDataMap.put(direction, data);
        } else {
            textDataMap.remove(direction);
        }
        measureText();
    }

    @Nullable
    private String getText(@Nonnull GuiDragDirection direction) {
        DirectionTextData td = textDataMap.get(direction);
        if (td == null) {
            return null;
        } else {
            if (td.show) {
                return td.text;
            } else {
                return null;
            }
        }
    }

    @Nonnull
    public String getDirectionTextScale() {
        return directionTextScale;
    }

    @Nonnull
    private Map<GuiDragDirection, Float> getDirectionTextScales() {
        final List<Float> scales = StringCollections.split(getDirectionTextScale(), ";", NumberParser.of(Float.class));

        final Map<GuiDragDirection, Float> result = new HashMap<>();
        for (GuiDragDirection direction : GuiDragDirection.values()) {
            result.put(direction, DEFAULT_DIRECTION_TEXT_SCALE_FLOAT);
        }

        if (scales.size() == 1) {
            final Float scale = scales.get(0);
            for (Map.Entry<GuiDragDirection, Float> entry : result.entrySet()) {
                entry.setValue(scale);
            }
        } else {
            for (int i = 0; i < scales.size(); i++) {
                for (GuiDragDirection direction : GuiDragDirection.values()) {
                    if (direction.getAttributePosition() == i) {
                        result.put(direction, scales.get(i));
                    }
                }
            }
        }

        return result;
    }


    public static enum GuiDragDirection {
        up(DragDirection.up, 0) {
            @Override
            public int getAttributeId() {
                return org.solovyev.android.view.R.styleable.DirectionDragButton_textUp;
            }

            @Nonnull
            @Override
            public Point2d getTextPosition(@Nonnull Paint paint, @Nonnull Paint basePaint, @Nonnull CharSequence text, CharSequence baseText, int w, int h) {
                return getUpDownTextPosition(paint, basePaint, text, baseText, 1, w, h);
            }
        },
        down(DragDirection.down, 2) {
            @Override
            public int getAttributeId() {
                return org.solovyev.android.view.R.styleable.DirectionDragButton_textDown;
            }

            @Nonnull
            @Override
            public Point2d getTextPosition(@Nonnull Paint paint, @Nonnull Paint basePaint, @Nonnull CharSequence text, CharSequence baseText, int w, int h) {
                return getUpDownTextPosition(paint, basePaint, text, baseText, -1, w, h);
            }
        },
        left(DragDirection.left, 3) {
            @Override
            public int getAttributeId() {
                return org.solovyev.android.view.R.styleable.DirectionDragButton_textLeft;
            }

            @Nonnull
            @Override
            public Point2d getTextPosition(@Nonnull Paint paint, @Nonnull Paint basePaint, @Nonnull CharSequence text, CharSequence baseText, int w, int h) {
                return getLeftRightTextPosition(paint, basePaint, text, baseText, w, h, true);
            }
        },

        right(DragDirection.right, 1) {
            @Override
            public int getAttributeId() {
                return org.solovyev.android.view.R.styleable.DirectionDragButton_textRight;
            }

            @Nonnull
            @Override
            public Point2d getTextPosition(@Nonnull Paint paint, @Nonnull Paint basePaint, @Nonnull CharSequence text, CharSequence baseText, int w, int h) {
                return getLeftRightTextPosition(paint, basePaint, text, baseText, w, h, false);
            }
        };

        @Nonnull
        private final DragDirection dragDirection;

        private final int attributePosition;

        GuiDragDirection(@Nonnull DragDirection dragDirection, int attributePosition) {
            this.dragDirection = dragDirection;
            this.attributePosition = attributePosition;
        }

        @Nonnull
        private static Point2d getLeftRightTextPosition(@Nonnull Paint paint, @Nonnull Paint basePaint, CharSequence text, @Nonnull CharSequence baseText, int w, int h, boolean left) {
            final Point2d result = new Point2d();

            if (left) {
                float width = paint.measureText(" ");
                result.setX(width);
            } else {
                float width = paint.measureText(text.toString() + " ");
                result.setX(w - width);
            }

            float selfHeight = paint.ascent() + paint.descent();

            basePaint.measureText(Strings.getNotEmpty(baseText, "|"));

            result.setY(h / 2 - selfHeight / 2);

            return result;
        }

        @Nonnull
        private static Point2d getUpDownTextPosition(@Nonnull Paint paint, @Nonnull Paint basePaint, @Nonnull CharSequence text, CharSequence baseText, float direction, int w, int h) {
            final Point2d result = new Point2d();

            float width = paint.measureText(text.toString() + " ");
            result.setX(w - width);

            float selfHeight = paint.ascent() + paint.descent();

            basePaint.measureText(Strings.getNotEmpty(baseText, "|"));

            if (direction < 0) {
                result.setY(h / 2 + h / 3 - selfHeight / 2);
            } else {
                result.setY(h / 2 - h / 3 - selfHeight / 2);
            }

            return result;
        }

        @Nullable
        public static GuiDragDirection valueOf(@Nonnull DragDirection dragDirection) {
            for (GuiDragDirection guiDragDirection : values()) {
                if (guiDragDirection.dragDirection == dragDirection) {
                    return guiDragDirection;
                }
            }
            return null;
        }

        public abstract int getAttributeId();

        public int getAttributePosition() {
            return attributePosition;
        }

        @Nonnull
        public abstract Point2d getTextPosition(@Nonnull Paint paint, @Nonnull Paint basePaint, @Nonnull CharSequence text, CharSequence baseText, int w, int h);
    }

    protected static class DirectionTextData {

        @Nonnull
        private final GuiDragDirection direction;
        @Nonnull
        private final TextPaint paint = new TextPaint();
        @Nonnull
        private String text;
        @Nonnull
        private Point2d position;
        @Nonnull
        private Float scale = 0.5f;

        private boolean show = true;

        private DirectionTextData(@Nonnull GuiDragDirection direction, @Nonnull String text) {
            this.direction = direction;
            this.text = text;
        }

        protected void init(@Nonnull Paint basePaint,
                            int color,
                            int alpha) {
            paint.set(basePaint);
            paint.setColor(color);
            paint.setAlpha(alpha);
            paint.setTextSize(basePaint.getTextSize() * scale);
        }

        @Nonnull
        public GuiDragDirection getDirection() {
            return direction;
        }

        @Nonnull
        public String getText() {
            return text;
        }

        @Nonnull
        public Point2d getPosition() {
            return position;
        }

        @Nonnull
        public TextPaint getPaint() {
            return paint;
        }

        @Nonnull
        public Float getScale() {
            return scale;
        }

        public boolean isShow() {
            return show;
        }
    }

}
