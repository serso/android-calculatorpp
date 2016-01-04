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

import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 11/17/12
 * Time: 6:54 PM
 */
public class FixableMessage implements Parcelable {

    public static final Creator<FixableMessage> CREATOR = new Creator<FixableMessage>() {
        @Override
        public FixableMessage createFromParcel(@Nonnull Parcel in) {
            return FixableMessage.fromParcel(in);
        }

        @Override
        public FixableMessage[] newArray(int size) {
            return new FixableMessage[size];
        }
    };
    @Nonnull
    private final String message;
    @Nonnull
    private final MessageType messageType;
    @Nullable
    private final FixableError fixableError;

    public FixableMessage(@Nonnull Message message) {
        this.message = message.getLocalizedMessage();
        final int messageLevel = message.getMessageLevel().getMessageLevel();
        this.messageType = CalculatorMessages.toMessageType(messageLevel);
        this.fixableError = CalculatorFixableError.getErrorByMessageCode(message.getMessageCode());
    }

    public FixableMessage(@Nonnull String message,
                          @Nonnull MessageType messageType,
                          @Nullable FixableError fixableError) {
        this.message = message;
        this.messageType = messageType;
        this.fixableError = fixableError;
    }

    @Nonnull
    private static FixableMessage fromParcel(@Nonnull Parcel in) {
        final String message = in.readString();
        final MessageType messageType = (MessageType) in.readSerializable();
        final FixableError fixableError = (FixableError) in.readSerializable();

        return new FixableMessage(message, messageType, fixableError);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@Nonnull Parcel out, int flags) {
        out.writeString(message);
        out.writeSerializable(messageType);
        out.writeSerializable(fixableError);
    }

    @Nonnull
    public String getMessage() {
        return message;
    }

    @Nonnull
    public MessageType getMessageType() {
        return messageType;
    }

    @Nullable
    public FixableError getFixableError() {
        return fixableError;
    }
}
