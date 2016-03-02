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

package org.solovyev.common.equals;

import org.solovyev.common.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public class CollectionEqualizer<T> implements Equalizer<Collection<T>> {

    @Nullable
    protected final Equalizer<T> nestedEqualizer;

    public CollectionEqualizer(@Nullable Equalizer<T> nestedEqualizer) {
        this.nestedEqualizer = nestedEqualizer;
    }

    @Override
    public boolean areEqual(@Nonnull Collection<T> first, @Nonnull Collection<T> second) {
        boolean result = false;

        if (first.size() == second.size()) {
            result = true;

            for (T el1 : first) {
                boolean found = false;

                for (T el2 : second) {
                    if (Objects.areEqual(el1, el2, nestedEqualizer)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

}
