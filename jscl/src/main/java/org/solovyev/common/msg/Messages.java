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

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Messages {

    private Messages() {
        throw new AssertionError();
    }

    @Nonnull
    public static MessageRegistry synchronizedMessageRegistry(@Nonnull MessageRegistry messageRegistry) {
        return SynchronizedMessageRegistry.wrap(messageRegistry);
    }

    /**
     * @param locale     locale for which default formatting will be applied
     * @param pattern    message pattern which will be used for {@link MessageFormat}
     * @param parameters message parameters which will be used for {@link MessageFormat}
     * @return formatted message string according to default locale formatting, nested messages are
     * processed properly
     * (for each message from parameter method {@link Message#getLocalizedMessage(Locale)} is
     * called)
     */
    @Nonnull
    public static String prepareMessage(@Nonnull Locale locale, @Nonnull String pattern, @Nonnull List<?> parameters) {
        String result;

        if (parameters.isEmpty()) {
            result = pattern;
        } else {
            final MessageFormat format = new MessageFormat(pattern, locale);
            result = format.format(prepareParameters(parameters, locale));
        }

        return result;
    }

    @Nonnull
    private static Object[] prepareParameters(@Nonnull List<?> parameters, @Nonnull Locale locale) {
        final Object[] result = new Object[parameters.size()];

        for (int i = 0; i < parameters.size(); i++) {
            result[i] = substituteParameter(parameters.get(i), locale);
        }

        return result;
    }

    @Nullable
    private static Object substituteParameter(@Nullable Object object, @Nonnull Locale locale) {
        if (object instanceof Message) {
            return ((Message) object).getLocalizedMessage(locale);
        } else {
            return object;
        }
    }
}
