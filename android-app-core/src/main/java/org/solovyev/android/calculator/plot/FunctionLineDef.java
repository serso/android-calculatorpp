package org.solovyev.android.calculator.plot;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 1/5/13
 * Time: 7:41 PM
 */
public class FunctionLineDef implements Parcelable {

    /*
    **********************************************************************
    *
    *                           CONSTANTS
    *
    **********************************************************************
    */

    @NotNull
    private static final Float DEFAULT_LINE_WIDTH = -1f;

	/*
	**********************************************************************
	*
	*                           STATIC
	*
	**********************************************************************
	*/

	private static final Creator<FunctionLineDef> CREATOR = new Creator<FunctionLineDef>() {
		@Override
		public FunctionLineDef createFromParcel(@NotNull Parcel in) {
			return fromParcel(in);
		}

		@Override
		public FunctionLineDef[] newArray(int size) {
			return new FunctionLineDef[size];
		}
	};

    /*
    **********************************************************************
    *
    *                           FIELDS
    *
    **********************************************************************
    */

    @NotNull
    private FunctionLineColorType lineColorType = FunctionLineColorType.solid;

    private int lineColor = Color.WHITE;

    @NotNull
    private FunctionLineStyle lineStyle = FunctionLineStyle.solid;

    private float lineWidth = -DEFAULT_LINE_WIDTH;

    private FunctionLineDef() {
    }

    @NotNull
    public static FunctionLineDef newInstance(int lineColor, @NotNull FunctionLineStyle lineStyle) {
        final FunctionLineDef result = new FunctionLineDef();
        result.lineColor = lineColor;
        result.lineStyle = lineStyle;
        return result;
    }

    @NotNull
    public static FunctionLineDef newInstance(int lineColor, @NotNull FunctionLineStyle lineStyle, float lineWidth) {
        final FunctionLineDef result = new FunctionLineDef();
        result.lineColor = lineColor;
        result.lineStyle = lineStyle;
        result.lineWidth = lineWidth;
        return result;
    }

    @NotNull
    public static FunctionLineDef newInstance(int lineColor, @NotNull FunctionLineStyle lineStyle, float lineWidth, @NotNull FunctionLineColorType lineColorType) {
        final FunctionLineDef result = new FunctionLineDef();
        result.lineColor = lineColor;
        result.lineColorType = lineColorType;
        result.lineStyle = lineStyle;
        result.lineWidth = lineWidth;
        return result;
    }

	public static FunctionLineDef fromParcel(@NotNull Parcel in) {
		final FunctionLineDef result = new FunctionLineDef();

		result.lineColorType = (FunctionLineColorType) in.readSerializable();
		result.lineColor = in.readInt();
		result.lineStyle = (FunctionLineStyle) in.readSerializable();
		result.lineWidth = in.readFloat();

		return result;
	}


    @NotNull
    public static FunctionLineDef newDefaultInstance() {
        return new FunctionLineDef();
    }


    public int getLineColor() {
        return lineColor;
    }

    @NotNull
    public FunctionLineStyle getLineStyle() {
        return lineStyle;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    @NotNull
    public FunctionLineColorType getLineColorType() {
        return lineColorType;
    }

    public void applyToPaint(@NotNull Paint paint) {
        paint.setColor(lineColor);
        paint.setStyle(Paint.Style.STROKE);

        if ( lineWidth == DEFAULT_LINE_WIDTH ) {
            paint.setStrokeWidth(0);
        } else {
            paint.setStrokeWidth(lineWidth);
        }

        lineStyle.applyToPaint(paint);
    }

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NotNull Parcel out, int flags) {
		out.writeSerializable(lineColorType);
		out.writeInt(lineColor);
		out.writeSerializable(lineStyle);
		out.writeFloat(lineWidth);
	}
}
