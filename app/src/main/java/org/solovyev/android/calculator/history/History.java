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

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Root
public class History {

    @Nonnull
    @ElementList(type = HistoryState.class, name = "historyItems")
    private List<HistoryState> items = new ArrayList<HistoryState>();

    public History() {
    }

    @Nullable
    public static History fromXml(@Nullable String xml) {
        if (xml == null) {
            return null;
        }
        final Serializer serializer = new Persister();
        try {
            return serializer.read(History.class, xml);
        } catch (Exception e) {
            return null;
        }
    }

    @Nonnull
    public String toXml() {
        final StringWriter xml = new StringWriter();
        final Serializer serializer = new Persister();
        try {
            serializer.write(this, xml);
        } catch (Exception e) {
            return "";
        }
        return xml.toString();
    }

    @Nonnull
    public List<HistoryState> getItems() {
        return items;
    }

    public void add(@Nonnull HistoryState state) {
        items.add(state);
    }

    public void clear() {
        items.clear();
    }

    public void remove(@Nonnull HistoryState state) {
        items.remove(state);
    }
}
