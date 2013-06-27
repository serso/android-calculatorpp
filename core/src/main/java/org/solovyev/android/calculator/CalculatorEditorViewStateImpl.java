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
 * Time: 12:02
 */
public class CalculatorEditorViewStateImpl implements CalculatorEditorViewState {

	@Nonnull
	private CharSequence text = "";

	private int selection = 0;

	private CalculatorEditorViewStateImpl() {
	}

	public CalculatorEditorViewStateImpl(@Nonnull CalculatorEditorViewState viewState) {
		this.text = viewState.getText();
		this.selection = viewState.getSelection();
	}

	@Nonnull
	@Override
	public String getText() {
		return this.text.toString();
	}

	@Nonnull
	@Override
	public CharSequence getTextAsCharSequence() {
		return this.text;
	}

	@Override
	public int getSelection() {
		return this.selection;
	}

	@Nonnull
	public static CalculatorEditorViewState newDefaultInstance() {
		return new CalculatorEditorViewStateImpl();
	}

	@Nonnull
	public static CalculatorEditorViewState newSelection(@Nonnull CalculatorEditorViewState viewState, int newSelection) {
		final CalculatorEditorViewStateImpl result = new CalculatorEditorViewStateImpl(viewState);

		result.selection = newSelection;

		return result;
	}

	@Nonnull
	public static CalculatorEditorViewState newInstance(@Nonnull CharSequence text, int selection) {
		final CalculatorEditorViewStateImpl result = new CalculatorEditorViewStateImpl();
		result.text = text;
		result.selection = selection;
		return result;
	}
}
