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

package org.solovyev.android.calculator.errors;

import android.os.Parcel;
import android.os.Parcelable;
import org.solovyev.android.calculator.CalculatorMessages;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FixableError implements Parcelable {

    public static final Creator<FixableError> CREATOR = new Creator<FixableError>() {
        @Override
        public FixableError createFromParcel(@Nonnull Parcel in) {
            return new FixableError(in);
        }

        @Override
        public FixableError[] newArray(int size) {
            return new FixableError[size];
        }
    };
    @Nonnull
    public final String message;
    @Nonnull
    public final MessageType messageType;
    @Nullable
    public final FixableErrorType error;

    private FixableError(@Nonnull Parcel in) {
        message = in.readString();
        messageType = MessageType.values()[in.readInt()];
        final int errorOrdinal = in.readInt();
        error = errorOrdinal == -1 ? null : FixableErrorType.values()[errorOrdinal];
    }

    public FixableError(@Nonnull Message message) {
        this.message = message.getLocalizedMessage();
        final int messageLevel = message.getMessageLevel().getMessageLevel();
        this.messageType = CalculatorMessages.toMessageType(messageLevel);
        this.error = FixableErrorType.getErrorByCode(message.getMessageCode());
    }

    public FixableError(@Nonnull String message,
                        @Nonnull MessageType messageType,
                        @Nullable FixableErrorType error) {
        this.message = message;
        this.messageType = messageType;
        this.error = error;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@Nonnull Parcel out, int flags) {
        out.writeString(message);
        out.writeInt(messageType.ordinal());
        out.writeInt(error == null ? -1 : error.ordinal());
    }
}
