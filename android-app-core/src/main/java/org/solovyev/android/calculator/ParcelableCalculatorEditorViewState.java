package org.solovyev.android.calculator;

import android.os.Parcel;
import android.os.Parcelable;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 11/18/12
 * Time: 1:40 PM
 */
public final class ParcelableCalculatorEditorViewState implements CalculatorEditorViewState, Parcelable {

	public static final Creator<ParcelableCalculatorEditorViewState> CREATOR = new Creator<ParcelableCalculatorEditorViewState>() {
		@Override
		public ParcelableCalculatorEditorViewState createFromParcel(@NotNull Parcel in) {
			return ParcelableCalculatorEditorViewState.fromParcel(in);
		}

		@Override
		public ParcelableCalculatorEditorViewState[] newArray(int size) {
			return new ParcelableCalculatorEditorViewState[size];
		}
	};

	@NotNull
	private CalculatorEditorViewState viewState;

	public ParcelableCalculatorEditorViewState(@NotNull CalculatorEditorViewState viewState) {
		this.viewState = viewState;
	}

	@NotNull
	private static ParcelableCalculatorEditorViewState fromParcel(@NotNull Parcel in) {
		final String text = in.readString();
		final int selection = in.readInt();
		return new ParcelableCalculatorEditorViewState(CalculatorEditorViewStateImpl.newInstance(text, selection));
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
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NotNull Parcel out, int flags) {
		out.writeString(viewState.getText());
		out.writeInt(viewState.getSelection());
	}
}
