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
 * ---------------------------------------------------------------------
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.common.msg;

import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public abstract class AbstractMessage implements Message {

    @Nonnull
    private final String messageCode;

    @Nonnull
    private final List<Object> parameters;

    @Nonnull
    private final MessageLevel messageLevel;

    protected AbstractMessage(@Nonnull String messageCode, @Nonnull MessageLevel messageType, @Nullable Object... parameters) {
        this(messageCode, messageType, parameters == null ? Collections.emptyList() : Arrays.asList(parameters));
    }

    protected AbstractMessage(@Nonnull String messageCode, @Nonnull MessageLevel messageType, @Nonnull List<?> parameters) {
        this.messageCode = messageCode;
        this.parameters = new ArrayList<>(parameters);
        this.messageLevel = messageType;
    }

    @Override
    @Nonnull
    public String getMessageCode() {
        return this.messageCode;
    }

    @Nonnull
    @Override
    public List<Object> getParameters() {
        return java.util.Collections.unmodifiableList(this.parameters);
    }

    @Nonnull
    @Override
    public MessageLevel getMessageLevel() {
        return this.messageLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final AbstractMessage that = (AbstractMessage) o;

        if (!areEqual(parameters, that.parameters)) {
            return false;
        }
        if (!messageCode.equals(that.messageCode)) {
            return false;
        }
        if (!messageLevel.equals(that.messageLevel)) {
            return false;
        }

        return true;
    }

    private boolean areEqual(@Nonnull List<Object> thisList, @Nonnull List<Object> thatList) {
        if (thisList.size() != thatList.size()) {
            return false;
        }
        for (int i = 0; i < thisList.size(); i++) {
            final Object thisItem = thisList.get(i);
            final Object thatItem = thatList.get(i);
            if (!thisItem.equals(thatItem)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(messageCode, messageLevel, parameters);
    }

    /**
     * Method converts message to string setting passed message parameters and translating some of them.
     *
     * @param locale language to which parameters should be translated (if possible)
     * @return message as string with properly translated and set parameters
     */
    @Nonnull
    public String getLocalizedMessage(@Nonnull Locale locale) {
        String result = null;

        final String messagePattern = getMessagePattern(locale);
        if (!Strings.isEmpty(messagePattern)) {
            result = Messages.prepareMessage(locale, messagePattern, parameters);
        }

        return Strings.getNotEmpty(result, messageLevel.getName() + ": message code = " + messageCode);
    }

    @Nonnull
    @Override
    public String getLocalizedMessage() {
        return this.getLocalizedMessage(Locale.getDefault());
    }

    @Nullable
    protected abstract String getMessagePattern(@Nonnull Locale locale);
}
