package org.solovyev.android.calculator;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleHistoryHelper<T> implements HistoryHelper<T> {

	private List<T> history = new ArrayList<T>();
	
	private int currentStateIndex = -1;
	
	@Override
	public T undo(@Nullable T currentState) {
		if ( !isUndoAvailable() ) { 
			throw new IndexOutOfBoundsException();
		}
		
		currentStateIndex--;
		
		return history.get(currentStateIndex);
	}

	@Override
	public T redo(@Nullable T currentState) {
		if (!isRedoAvailable()) {
			throw new IndexOutOfBoundsException();
		}
		currentStateIndex++;
		return history.get(currentStateIndex);
	}

	@Override
	public void addState(@Nullable T currentState) {
		if (currentStateIndex == history.size() - 1) {
			currentStateIndex++;
			history.add(currentState);
		} else {
			assert currentStateIndex < history.size() - 1 : "Invalid history state index!";
			currentStateIndex++;
			history.set(currentStateIndex, currentState);
			while( history.size() > currentStateIndex + 1 ) {
				history.remove(history.size() - 1);
			}
		}
	}

	@Override
	public boolean isUndoAvailable() {
		return currentStateIndex > 0;
	}

	@Override
	public boolean isRedoAvailable() {
		return currentStateIndex < history.size() - 1;
	}

	@Override
	public boolean isActionAvailable(@NotNull HistoryAction historyAction) {
		boolean result = false;
		
		switch (historyAction) {
			case undo:
				result = isUndoAvailable();
				break;
			case redo:
				result = isRedoAvailable();
				break;
		}
		
		return result;
	}

	@Override
	public T doAction(@NotNull HistoryAction historyAction, @Nullable T currentState) {
		T result = null;
		
		switch (historyAction) {
			case undo:
				result = undo(currentState);
				break;
			case redo:
				result = redo(currentState);
				break;
		}
		
		return result;
	}
}
