package org.solovyev.android.calculator;

import android.os.Parcel;
import android.os.Parcelable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.common.msg.MessageLevel;
import org.solovyev.common.msg.MessageType;

/**
 * User: serso
 * Date: 1/20/13
 * Time: 1:04 PM
 */
public final class ParcelableDialogData implements DialogData, Parcelable {

	/*
	**********************************************************************
	*
	*                           STATIC
	*
	**********************************************************************
	*/

	public final static Creator<ParcelableDialogData> CREATOR = new Creator<ParcelableDialogData>() {
		@Override
		public ParcelableDialogData createFromParcel(@Nonnull Parcel in) {
			return fromParcel(in);
		}

		@Override
		public ParcelableDialogData[] newArray(int size) {
			return new ParcelableDialogData[size];
		}
	};

	/*
	**********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/

	@Nonnull
	private DialogData nestedData;

	/*
	**********************************************************************
	*
	*                           CONSTRUCTORS
	*
	**********************************************************************
	*/

	public ParcelableDialogData(@Nonnull DialogData nestedData) {
		this.nestedData = nestedData;
	}

	@Nonnull
	public static ParcelableDialogData wrap(@Nonnull DialogData nestedData) {
		if (nestedData instanceof ParcelableDialogData) {
			return ((ParcelableDialogData) nestedData);
		} else {
			return new ParcelableDialogData(nestedData);
		}
	}

	@Nonnull
	public static ParcelableDialogData fromParcel(@Nonnull Parcel in) {
		final String message = in.readString();
		final MessageType messageType = MessageType.values()[in.readInt()];
		final String title = in.readString();
		return wrap(StringDialogData.newInstance(message, messageType, title));
	}

	@Nonnull
	@Override
	public String getMessage() {
		return nestedData.getMessage();
	}

	@Nonnull
	@Override
	public MessageLevel getMessageLevel() {
		return nestedData.getMessageLevel();
	}

	@Nullable
	@Override
	public String getTitle() {
		return nestedData.getTitle();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@Nonnull Parcel out, int flags) {
		out.writeString(this.getMessage());
		out.writeInt(this.getMessageLevel().getMessageLevel());
		out.writeString(this.getTitle());
	}
}
