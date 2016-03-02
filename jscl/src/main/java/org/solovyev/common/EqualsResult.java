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

package org.solovyev.common;

import org.solovyev.common.equals.Equalizer;

import javax.annotation.Nullable;

public class EqualsResult<T> {

    public static final Integer BOTH_NULLS_CONST = 0;

    @Nullable
    private final Integer result;

    @Nullable
    private final T o1;

    @Nullable
    private final T o2;

    @Nullable
    private final Equalizer<? super T> equalizer;

    EqualsResult(@Nullable T o1, @Nullable T o2, @Nullable Equalizer<? super T> equalizer) {
        this.equalizer = equalizer;
        if (o1 == null && o2 == null) {
            result = EqualsResult.BOTH_NULLS_CONST;
        } else if (o1 == null) {
            result = -1;
        } else if (o2 == null) {
            result = 1;
        } else {
            //both not nulls
            result = null;
        }
        this.o1 = o1;
        this.o2 = o2;
    }

    @Nullable
    public Integer getResult() {
        return result;
    }

    public boolean areBothNotNulls() {
        return result == null;
    }

    public boolean areBothNulls() {
        return result != null && result.equals(BOTH_NULLS_CONST);
    }

    public boolean areEqual() {
        //noinspection ConstantConditions
        boolean areSame = o1 == o2;
        return areBothNulls() || areSame || (areBothNotNulls() && (equalizer == null ? o1.equals(o2) : equalizer.areEqual(o1, o2)));
    }
}
