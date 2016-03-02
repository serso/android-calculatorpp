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
import java.util.List;

public class ListEqualizer<T> implements Equalizer<List<T>> {

	@Nonnull
	private static final Equalizer<List<Object>> instanceWithOrder = new ListEqualizer<>(true, null);

	@Nonnull
	private static final Equalizer<List<Object>> instanceWithoutOrder = new ListEqualizer<>(false, null);

	private final boolean checkOrder;

	@Nullable
	protected final Equalizer<T> nestedEqualizer;

	private ListEqualizer(boolean checkOrder, @Nullable Equalizer<T> nestedEqualizer) {
		this.checkOrder = checkOrder;
		this.nestedEqualizer = nestedEqualizer;
	}

	@Nonnull
	public static <T> ListEqualizer<T> newWithNestedEqualizer(boolean checkOrder, @Nullable Equalizer<T> nestedEqualizer) {
		return new ListEqualizer<>(checkOrder, nestedEqualizer);
	}

	@Nonnull
	public static <T> ListEqualizer<T> newWithNaturalEquals(boolean checkOrder) {
		if (checkOrder) {
			return (ListEqualizer<T>) instanceWithOrder;
		} else {
			return (ListEqualizer<T>) instanceWithoutOrder;
		}
	}

	@Override
	public boolean areEqual(@Nonnull List<T> first, @Nonnull List<T> second) {
		boolean result = false;

		if (first.size() == second.size()) {
			if (checkOrder) {
				result = true;
				for (int i = 0; i < first.size(); i++) {
					final T el1 = first.get(i);
					final T el2 = second.get(i);

					if (!Objects.areEqual(el1, el2, nestedEqualizer)) {
						result = false;
						break;
					}

				}
			} else {
				result = Objects.areEqual(first, second, new CollectionEqualizer<>(nestedEqualizer));
			}
		}

		return result;
	}
}
