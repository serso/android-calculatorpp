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

import org.solovyev.common.gui.CursorControl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: Solovyev_S
 * Date: 21.09.12
 * Time: 11:47
 */
public interface CalculatorEditor extends CalculatorEventListener {

	@Nonnull
	String TAG = CalculatorEditor.class.getSimpleName();

	void setView(@Nullable CalculatorEditorView view);

	@Nonnull
	CalculatorEditorViewState getViewState();

	// updates state of view (view.setState())
	void updateViewState();

	void setViewState(@Nonnull CalculatorEditorViewState viewState);

	/*
	**********************************************************************
	*
	*                           CURSOR CONTROL
	*
	**********************************************************************
	*/

	/**
	 * Method sets the cursor to the beginning
	 */
	@Nonnull
	public CalculatorEditorViewState setCursorOnStart();

	/**
	 * Method sets the cursor to the end
	 */
	@Nonnull
	public CalculatorEditorViewState setCursorOnEnd();

	/**
	 * Method moves cursor to the left of current position
	 */
	@Nonnull
	public CalculatorEditorViewState moveCursorLeft();

	/**
	 * Method moves cursor to the right of current position
	 */
	@Nonnull
	public CalculatorEditorViewState moveCursorRight();

	@Nonnull
	CursorControl asCursorControl();


	/*
	**********************************************************************
	*
	*                           EDITOR OPERATIONS
	*
	**********************************************************************
	*/
	@Nonnull
	CalculatorEditorViewState erase();

	@Nonnull
	CalculatorEditorViewState clear();

	@Nonnull
	CalculatorEditorViewState setText(@Nonnull String text);

	@Nonnull
	CalculatorEditorViewState setText(@Nonnull String text, int selection);

	@Nonnull
	CalculatorEditorViewState insert(@Nonnull String text);

	@Nonnull
	CalculatorEditorViewState insert(@Nonnull String text, int selectionOffset);

	@Nonnull
	CalculatorEditorViewState moveSelection(int offset);

	@Nonnull
	CalculatorEditorViewState setSelection(int selection);
}
