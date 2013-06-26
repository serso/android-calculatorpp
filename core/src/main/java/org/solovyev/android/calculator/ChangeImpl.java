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

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 10/1/12
 * Time: 11:18 PM
 */
public class ChangeImpl<T> implements Change<T> {

	@Nonnull
	private T oldValue;

	@Nonnull
	private T newValue;

	private ChangeImpl() {
	}

	@Nonnull
	public static <T> Change<T> newInstance(@Nonnull T oldValue, @Nonnull T newValue) {
		final ChangeImpl<T> result = new ChangeImpl<T>();

		result.oldValue = oldValue;
		result.newValue = newValue;

		return result;
	}

	@Nonnull
	@Override
	public T getOldValue() {
		return this.oldValue;
	}

	@Nonnull
	@Override
	public T getNewValue() {
		return this.newValue;
	}
}
