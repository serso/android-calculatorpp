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

package org.solovyev.android.calculator.history;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.StringWriter;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 12/17/11
 * Time: 9:59 PM
 */
class HistoryUtils {

    // not intended for instantiation
    private HistoryUtils() {
        throw new AssertionError();
    }

    public static void fromXml(@Nullable String xml, @Nonnull List<HistoryState> historyItems) {
        if (xml != null) {
            final Serializer serializer = new Persister();
            try {
                final History history = serializer.read(History.class, xml);
                for (HistoryState historyItem : history.getHistoryItems()) {
                    historyItems.add(historyItem);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nonnull
    public static String toXml(@Nonnull List<HistoryState> historyItems) {
        final History history = new History();
        for (HistoryState historyState : historyItems) {
            if (historyState.isSaved()) {
                history.getHistoryItems().add(historyState);
            }
        }

        final StringWriter xml = new StringWriter();
        final Serializer serializer = new Persister();
        try {
            serializer.write(history, xml);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return xml.toString();
    }
}
