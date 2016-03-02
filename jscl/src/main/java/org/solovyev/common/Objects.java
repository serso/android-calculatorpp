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

/**
 * User: serso
 * Date: 5/18/11
 * Time: 11:18 AM
 */

import org.solovyev.common.equals.Equalizer;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

public class Objects {

	protected Objects() {
		throw new AssertionError();
	}

	/*
	**********************************************************************
	*
	*                           EQUALS
	*
	**********************************************************************
	*/

	public static EqualsResult getEqualsResult(@Nullable Object o1, @Nullable Object o2) {
		return new EqualsResult<Object>(o1, o2, null);
	}

	public static <T> boolean areEqual(@Nullable T o1, @Nullable T o2) {
		return new EqualsResult<T>(o1, o2, null).areEqual();
	}

	public static <T> boolean areEqual(@Nullable T o1, @Nullable T o2, @Nullable Equalizer<? super T> equalizer) {
		return new EqualsResult<T>(o1, o2, equalizer).areEqual();
	}

	/*
	**********************************************************************
	*
	*                           COMPARE
	*
	**********************************************************************
	*/

	public static int compare(Object value1, Object value2) {
		Integer result = compareOnNullness(value1, value2);

		if (result == null) {
			if (value1 instanceof Comparable && value2 instanceof Comparable) {
				//noinspection unchecked
				result = ((Comparable) value1).compareTo(value2);
			} else {
				result = 0;
			}
		}
		return result;
	}

	public static <T extends Comparable<T>> int compare(@Nullable T l,
														@Nullable T r) {
		Integer result = compareOnNullness(l, r);

		if (result == null) {
			assert l != null;
			result = l.compareTo(r);

		}
		return result;
	}

	public static int compare(List list1, List list2) {
		Integer result = compareOnNullness(list1, list2);

		if (result == null) {
			result = list1.size() - list2.size();
			if (result == 0) {
				for (int i = 0; i < list1.size(); i++) {
					result = compare(list1.get(i), list2.get(i));
					if (result != 0) {
						break;
					}
				}
			}
		}

		return result;
	}

	public static int compare(Number value1, Number value2) {
		Integer result = compareOnNullness(value1, value2);

		if (result == null) {
			if (value1 instanceof Comparable && value2 instanceof Comparable) {
				//noinspection unchecked
				result = ((Comparable) value1).compareTo(value2);
			} else {
				result = 0;
			}
		}

		return result;
	}

	public static int compare(Date value1, Date value2) {
		Integer result = compareOnNullness(value1, value2);
		if (result == null) {
			if (value1.before(value2)) {
				result = -1;
			} else if (value1.after(value2)) {
				result = 1;
			} else {
				result = 0;
			}
		}
		return result;
	}

	public static int compare(int value1, int value2) {
		if (value1 > value2) {
			return 1;
		} else if (value1 == value2) {
			return 0;
		} else {
			return -1;
		}
	}

	public static int compare(String value1, String value2, boolean ignoreCase) {
		Integer result = compareOnNullness(value1, value2);

		if (result == null) {
			if (ignoreCase) {
				result = value1.toLowerCase().compareTo(value2.toLowerCase());
			} else {
				result = value1.compareTo(value2);
			}
		}

		return result;
	}

	public static int compare(Boolean value1, Boolean value2) {
		Integer result = compareOnNullness(value1, value2);

		if (result == null) {
			result = value1.compareTo(value2);
		}

		return result;
	}

	/**
	 * Method compares objects according their nullness property
	 *
	 * @param o1 first compared object
	 * @param o2 second compared object
	 * @return if both objects are nulls then 0 (they are equal), if first is null then -1, if second is null then 1, otherwise - null
	 */
	@Nullable
	public static Integer compareOnNullness(Object o1, Object o2) {
		Integer result;

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

		return result;
	}

	/**
	 * Method compares objects according their nullness property
	 *
	 * @param o1 first compared object
	 * @param o2 second compared object
	 * @return if both objects are nulls then 0 (they are equal), if first is null then -1, if second is null then 1, otherwise - null
	 */
	public static EqualsResult compareOnNullnessWithResult(Object o1, Object o2) {
		return getEqualsResult(o1, o2);
	}
}
