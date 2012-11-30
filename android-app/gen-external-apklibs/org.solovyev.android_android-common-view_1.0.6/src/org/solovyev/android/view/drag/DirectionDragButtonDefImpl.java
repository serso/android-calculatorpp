package org.solovyev.android.view.drag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

/**
 * User: serso
 * Date: 11/3/12
 * Time: 1:57 PM
 */
public class DirectionDragButtonDefImpl implements DirectionDragButtonDef {

    @Nullable
    private CharSequence text;

    private Map<DragDirection, CharSequence> directionsTexts = new EnumMap<DragDirection, CharSequence>(DragDirection.class);

    @Nullable
    private Integer backgroundResId;

    @Nullable
    private Integer drawableResId;

    @Nullable
    private String tag;

    @Nullable
    private Float weight;

    @Nullable
    private Integer layoutMarginLeft;

    @Nullable
    private Integer layoutMarginRight;

    private DirectionDragButtonDefImpl() {
    }

    @NotNull
    public static DirectionDragButtonDefImpl newInstance(@Nullable CharSequence text) {
        return newInstance(text, null, null, null, null);
    }

    @NotNull
    public static DirectionDragButtonDefImpl newInstance(@Nullable CharSequence text,
                                                     @Nullable CharSequence up,
                                                     @Nullable CharSequence right,
                                                     @Nullable CharSequence down,
                                                     @Nullable CharSequence left) {
        return newInstance(text, up, right, down, left, null);
    }

    @NotNull
    public static DirectionDragButtonDefImpl newInstance(@Nullable CharSequence text,
                                                     @Nullable CharSequence up,
                                                     @Nullable CharSequence right,
                                                     @Nullable CharSequence down,
                                                     @Nullable CharSequence left,
                                                     @Nullable Integer backgroundColor) {
        final DirectionDragButtonDefImpl result = new DirectionDragButtonDefImpl();

        result.text = text;
        result.directionsTexts.put(DragDirection.up, up);
        result.directionsTexts.put(DragDirection.right, right);
        result.directionsTexts.put(DragDirection.down, down);
        result.directionsTexts.put(DragDirection.left, left);

        result.backgroundResId = backgroundColor;

        return result;
    }

    @NotNull
    public static DirectionDragButtonDefImpl newDrawableInstance(@NotNull Integer drawableResId) {
        return newDrawableInstance(drawableResId, null);
    }

    @NotNull
    public static DirectionDragButtonDefImpl newDrawableInstance(@NotNull Integer drawableResId, @Nullable Integer backgroundColor) {
        final DirectionDragButtonDefImpl result = new DirectionDragButtonDefImpl();

        result.drawableResId = drawableResId;
        result.backgroundResId = backgroundColor;

        return result;

    }

    @Nullable
    @Override
    public CharSequence getText(@NotNull DragDirection dragDirection) {
        return directionsTexts.get(dragDirection);
    }

    @Nullable
    @Override
    public Float getLayoutWeight() {
        return this.weight;
    }

    @Nullable
    @Override
    public Integer getLayoutMarginLeft() {
        return this.layoutMarginLeft;
    }

    @Nullable
    @Override
    public Integer getLayoutMarginRight() {
        return this.layoutMarginRight;
    }

    @Nullable
    @Override
    public Integer getDrawableResId() {
        return this.drawableResId;
    }

    @Nullable
    @Override
    public String getTag() {
        return tag;
    }

    @Nullable
    @Override
    public Integer getBackgroundResId() {
        return this.backgroundResId;
    }

    @Nullable
    @Override
    public CharSequence getText() {
        return text;
    }

    public void setWeight(@Nullable Float weight) {
        this.weight = weight;
    }

    public void setLayoutMarginRight(@Nullable Integer layoutMarginRight) {
        this.layoutMarginRight = layoutMarginRight;
    }

    public void setLayoutMarginLeft(@Nullable Integer layoutMarginLeft) {
        this.layoutMarginLeft = layoutMarginLeft;
    }

	public void setBackgroundResId(int backgroundResId) {
		this.backgroundResId = backgroundResId;
	}

	public void setTag(@Nullable String tag) {
		this.tag = tag;
	}

	public void setText(@Nullable CharSequence text) {
		this.text = text;
	}

	public void setBackgroundResId(@Nullable Integer backgroundResId) {
		this.backgroundResId = backgroundResId;
	}

	public void setDrawableResId(@Nullable Integer drawableResId) {
		this.drawableResId = drawableResId;
	}

	public void setDirectionText(@NotNull DragDirection key, @Nullable CharSequence text) {
		directionsTexts.put(key, text);
	}
}
