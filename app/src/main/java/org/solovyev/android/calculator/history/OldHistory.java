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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Root(name = "History")
class OldHistory {

    @Nonnull
    @ElementList(type = OldHistoryState.class, name = "historyItems")
    private List<OldHistoryState> items = new ArrayList<OldHistoryState>();

    public OldHistory() {
    }

    @Nullable
    public static OldHistory fromXml(@Nullable String xml) throws Exception {
        if (xml == null) {
            return null;
        }
        final Serializer serializer = new Persister();
        return serializer.read(OldHistory.class, xml);
    }

    @Nonnull
    public List<OldHistoryState> getItems() {
        return items;
    }
}
