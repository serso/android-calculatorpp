package org.solovyev.android.calculator;

import android.os.Parcel;
import android.os.Parcelable;
import jscl.math.Generic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: serso
 * Date: 11/18/12
 * Time: 1:40 PM
 */
public final class ParcelableCalculatorDisplayViewState implements CalculatorDisplayViewState, Parcelable {

	public static final Creator<ParcelableCalculatorDisplayViewState> CREATOR = new Creator<ParcelableCalculatorDisplayViewState>() {
		@Override
		public ParcelableCalculatorDisplayViewState createFromParcel(@NotNull Parcel in) {
			return ParcelableCalculatorDisplayViewState.fromParcel(in);
		}

		@Override
		public ParcelableCalculatorDisplayViewState[] newArray(int size) {
			return new ParcelableCalculatorDisplayViewState[size];
		}
	};

	@NotNull
	private static ParcelableCalculatorDisplayViewState fromParcel(@NotNull Parcel in) {
		final int selection = in.readInt();
		final boolean valid = in.readInt() == 1;
		final String stringResult = in.readString();
		final String errorMessage = in.readString();
		final JsclOperation operation = (JsclOperation) in.readSerializable();

		CalculatorDisplayViewState calculatorDisplayViewState;
		if (valid) {
			calculatorDisplayViewState = CalculatorDisplayViewStateImpl.newValidState(operation, null, stringResult, selection);
		} else {
			calculatorDisplayViewState = CalculatorDisplayViewStateImpl.newErrorState(operation, errorMessage);
		}

		return new ParcelableCalculatorDisplayViewState(calculatorDisplayViewState);
	}

	@NotNull
	private CalculatorDisplayViewState viewState;

	public ParcelableCalculatorDisplayViewState(@NotNull CalculatorDisplayViewState viewState) {
		this.viewState = viewState;
	}

	@Override
	@NotNull
	public String getText() {
		return viewState.getText();
	}

	@Override
	public int getSelection() {
		return viewState.getSelection();
	}

	@Override
	@Nullable
	public Generic getResult() {
		return viewState.getResult();
	}

	@Override
	public boolean isValid() {
		return viewState.isValid();
	}

	@Override
	@Nullable
	public String getErrorMessage() {
		return viewState.getErrorMessage();
	}

	@Override
	@NotNull
	public JsclOperation getOperation() {
		return viewState.getOperation();
	}

	@Override
	@Nullable
	public String getStringResult() {
		return viewState.getStringResult();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NotNull Parcel out, int flags) {
		out.writeInt(viewState.getSelection());
		out.writeInt(viewState.isValid() ? 1 : 0);
		out.writeString(viewState.getStringResult());
		out.writeString(viewState.getErrorMessage());
		out.writeSerializable(viewState.getOperation());
	}
}
