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
 * User: Solovyev_S
 * Date: 21.09.12
 * Time: 13:46
 */
public final class CalculatorEditorChangeEventData implements Change<CalculatorEditorViewState> {

	@Nonnull
	private CalculatorEditorViewState oldState;

	@Nonnull
	private CalculatorEditorViewState newState;

	private CalculatorEditorChangeEventData(@Nonnull CalculatorEditorViewState oldState,
											@Nonnull CalculatorEditorViewState newState) {
		this.oldState = oldState;
		this.newState = newState;
	}

	public static CalculatorEditorChangeEventData newChangeEventData(@Nonnull CalculatorEditorViewState oldState,
																	 @Nonnull CalculatorEditorViewState newState) {
		return new CalculatorEditorChangeEventData(oldState, newState);
	}

	@Nonnull
	@Override
	public CalculatorEditorViewState getOldValue() {
		return this.oldState;
	}

	@Nonnull
	@Override
	public CalculatorEditorViewState getNewValue() {
		return this.newState;
	}
}
