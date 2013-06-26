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

import javax.annotation.Nonnull;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.solovyev.android.calculator.CalculatorDisplay;
import org.solovyev.android.calculator.CalculatorDisplayViewState;
import org.solovyev.android.calculator.CalculatorEditor;
import org.solovyev.android.calculator.CalculatorEditorViewState;

/**
 * User: serso
 * Date: 9/11/11
 * Time: 12:16 AM
 */

@Root
public class CalculatorHistoryState extends AbstractHistoryState {

	@Element
	@Nonnull
	private EditorHistoryState editorState;

	@Element
	@Nonnull
	private CalculatorDisplayHistoryState displayState;

	private CalculatorHistoryState() {
		// for xml
	}

	private CalculatorHistoryState(@Nonnull EditorHistoryState editorState,
								   @Nonnull CalculatorDisplayHistoryState displayState) {
		this.editorState = editorState;
		this.displayState = displayState;
	}

	@Nonnull
	public static CalculatorHistoryState newInstance(@Nonnull CalculatorEditor editor,
													 @Nonnull CalculatorDisplay display) {
		final CalculatorEditorViewState editorViewState = editor.getViewState();
		final CalculatorDisplayViewState displayViewState = display.getViewState();

		return newInstance(editorViewState, displayViewState);
	}

	@Nonnull
	public static CalculatorHistoryState newInstance(@Nonnull CalculatorEditorViewState editorViewState,
													 @Nonnull CalculatorDisplayViewState displayViewState) {
		final EditorHistoryState editorHistoryState = EditorHistoryState.newInstance(editorViewState);

		final CalculatorDisplayHistoryState displayHistoryState = CalculatorDisplayHistoryState.newInstance(displayViewState);

		return new CalculatorHistoryState(editorHistoryState, displayHistoryState);
	}

	@Nonnull
	public EditorHistoryState getEditorState() {
		return editorState;
	}

	public void setEditorState(@Nonnull EditorHistoryState editorState) {
		this.editorState = editorState;
	}

	@Nonnull
	public CalculatorDisplayHistoryState getDisplayState() {
		return displayState;
	}

	public void setDisplayState(@Nonnull CalculatorDisplayHistoryState displayState) {
		this.displayState = displayState;
	}

	@Override
	public String toString() {
		return "CalculatorHistoryState{" +
				"editorState=" + editorState +
				", displayState=" + displayState +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CalculatorHistoryState that = (CalculatorHistoryState) o;

		if (this.isSaved() != that.isSaved()) return false;
		if (this.getId() != that.getId()) return false;
		if (!displayState.equals(that.displayState)) return false;
		if (!editorState.equals(that.editorState)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = Boolean.valueOf(isSaved()).hashCode();
		result = 31 * result + getId();
		result = 31 * result + editorState.hashCode();
		result = 31 * result + displayState.hashCode();
		return result;
	}

	public void setValuesFromHistory(@Nonnull CalculatorEditor editor, @Nonnull CalculatorDisplay display) {
		this.getEditorState().setValuesFromHistory(editor);
		this.getDisplayState().setValuesFromHistory(display);
	}

	@Override
	protected CalculatorHistoryState clone() {
		final CalculatorHistoryState clone = (CalculatorHistoryState) super.clone();

		clone.editorState = this.editorState.clone();
		clone.displayState = this.displayState.clone();

		return clone;
	}
}
