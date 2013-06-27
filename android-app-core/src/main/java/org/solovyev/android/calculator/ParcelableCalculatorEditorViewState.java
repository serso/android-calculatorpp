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

package org.solovyev.android.calculator;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 11/18/12
 * Time: 1:40 PM
 */
public final class ParcelableCalculatorEditorViewState implements CalculatorEditorViewState, Parcelable {

	public static final Creator<ParcelableCalculatorEditorViewState> CREATOR = new Creator<ParcelableCalculatorEditorViewState>() {
		@Override
		public ParcelableCalculatorEditorViewState createFromParcel(@Nonnull Parcel in) {
			return ParcelableCalculatorEditorViewState.fromParcel(in);
		}

		@Override
		public ParcelableCalculatorEditorViewState[] newArray(int size) {
			return new ParcelableCalculatorEditorViewState[size];
		}
	};

	@Nonnull
	private CalculatorEditorViewState viewState;

	public ParcelableCalculatorEditorViewState(@Nonnull CalculatorEditorViewState viewState) {
		this.viewState = viewState;
	}

	@Nonnull
	private static ParcelableCalculatorEditorViewState fromParcel(@Nonnull Parcel in) {
		final String text = in.readString();
		final int selection = in.readInt();
		return new ParcelableCalculatorEditorViewState(CalculatorEditorViewStateImpl.newInstance(text, selection));
	}

	@Override
	@Nonnull
	public String getText() {
		return viewState.getText();
	}

	@Nonnull
	@Override
	public CharSequence getTextAsCharSequence() {
		return getText();
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
	public void writeToParcel(@Nonnull Parcel out, int flags) {
		out.writeString(viewState.getText());
		out.writeInt(viewState.getSelection());
	}
}
