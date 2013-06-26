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
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 10/7/12
 * Time: 7:17 PM
 */
public enum AndroidFunctionCategory {

	trigonometric(R.string.c_fun_category_trig),
	hyperbolic_trigonometric(R.string.c_fun_category_hyper_trig),
	comparison(R.string.c_fun_category_comparison),
	my(R.string.c_fun_category_my),
	common(R.string.c_fun_category_common);

	private final int captionId;

	AndroidFunctionCategory(int captionId) {
		this.captionId = captionId;
	}

	public int getCaptionId() {
		return captionId;
	}

	@Nullable
	public static AndroidFunctionCategory valueOf(@Nonnull FunctionCategory functionCategory) {
		for (AndroidFunctionCategory androidFunctionCategory : values()) {
			if (androidFunctionCategory.name().equals(functionCategory.name())) {
				return androidFunctionCategory;
			}
		}

		return null;
	}
}
