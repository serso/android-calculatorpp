package org.solovyev.android.calculator.plot;

import android.os.Parcel;
import android.os.Parcelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParcelablePlotInput implements Parcelable {

	/*
	**********************************************************************
	*
	*                           STATIC
	*
	**********************************************************************
	*/
	@NotNull
	public static Creator<ParcelablePlotInput> CREATOR = new Creator<ParcelablePlotInput>() {
		@Override
		public ParcelablePlotInput createFromParcel(@NotNull Parcel in) {
			return fromParcel(in);
		}

		@Override
		public ParcelablePlotInput[] newArray(int size) {
			return new ParcelablePlotInput[size];
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
	private String expression;

	@Nullable
	private String xVariableName;

	@Nullable
	private String yVariableName;

	@Nullable
	private FunctionLineDef lineDef;

	public ParcelablePlotInput(@NotNull String expression,
							   @Nullable String xVariableName,
							   @Nullable String yVariableName) {
		this(expression, xVariableName, yVariableName, null);
	}

	public ParcelablePlotInput(@NotNull String expression,
							   @Nullable String xVariableName,
							   @Nullable String yVariableName,
							   @Nullable FunctionLineDef lineDef) {
		this.expression = expression;
		this.xVariableName = xVariableName;
		this.yVariableName = yVariableName;
		this.lineDef = lineDef;
	}

	@NotNull
	public static ParcelablePlotInput fromParcel(@NotNull Parcel in) {
		final String expression = in.readString();
		final String xVariableName = in.readString();
		final String yVariableName = in.readString();
		final FunctionLineDef lineDef = in.readParcelable(Thread.currentThread().getContextClassLoader());
		return new ParcelablePlotInput(expression, xVariableName, yVariableName, lineDef);
	}

	@NotNull
	public String getExpression() {
		return expression;
	}

	@Nullable
	public String getXVariableName() {
		return xVariableName;
	}

	@Nullable
	public String getYVariableName() {
		return yVariableName;
	}

	@Nullable
	public FunctionLineDef getLineDef() {
		return lineDef;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NotNull Parcel out, int flags) {
		out.writeString(expression);
		out.writeString(xVariableName);
		out.writeString(yVariableName);
		out.writeParcelable(lineDef, 0);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ParcelablePlotInput)) return false;

		final ParcelablePlotInput that = (ParcelablePlotInput) o;

		if (!expression.equals(that.expression)) return false;
		if (xVariableName != null ? !xVariableName.equals(that.xVariableName) : that.xVariableName != null)
			return false;
		if (yVariableName != null ? !yVariableName.equals(that.yVariableName) : that.yVariableName != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = expression.hashCode();
		result = 31 * result + (xVariableName != null ? xVariableName.hashCode() : 0);
		result = 31 * result + (yVariableName != null ? yVariableName.hashCode() : 0);
		return result;
	}
}
