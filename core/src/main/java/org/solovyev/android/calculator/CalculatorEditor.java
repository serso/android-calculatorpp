package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.common.gui.CursorControl;

/**
 * User: Solovyev_S
 * Date: 21.09.12
 * Time: 11:47
 */
public interface CalculatorEditor extends CalculatorEventListener {

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
