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

package org.solovyev.common.search;

import org.solovyev.common.JPredicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StartsWithFinder implements JPredicate<String> {

	private int i;

	@Nonnull
	private final String targetString;

	private StartsWithFinder(@Nonnull String targetString, int i) {
		this.targetString = targetString;
		this.i = i;
	}

	@Nonnull
	public static StartsWithFinder newFrom(@Nonnull String targetString, int i) {
		return new StartsWithFinder(targetString, i);
	}

	@Nonnull
	public static StartsWithFinder newInstance(@Nonnull String targetString) {
		return newFrom(targetString, 0);
	}

	@Override
	public boolean apply(@Nullable String s) {
		return s != null && targetString.startsWith(s, i);
	}

	public void setI(int i) {
		this.i = i;
	}
}
