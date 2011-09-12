package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HistoryHelper<T> {

	boolean isUndoAvailable();
	
	@Nullable
	T undo (@Nullable T currentState);
	
	boolean isRedoAvailable();
	
	@Nullable
	T redo (@Nullable T currentState);
	
	boolean isActionAvailable(@NotNull HistoryAction historyAction);
	
	@Nullable
	T doAction(@NotNull HistoryAction historyAction, @Nullable T currentState);
	
	void addState(@Nullable T currentState);
}
