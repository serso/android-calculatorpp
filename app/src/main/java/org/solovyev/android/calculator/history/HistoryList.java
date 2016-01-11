package org.solovyev.android.calculator.history;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.solovyev.android.Check;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class HistoryList {

    private static final int MAX_HISTORY = 20;

    @NonNull
    private final List<HistoryState> list = new LinkedList<>();
    private int current = -1;

    public void add(@NonNull HistoryState state) {
        Check.isMainThread();
        if (isCurrent(state)) {
            return;
        }
        while (current != list.size() - 1) {
            list.remove(list.size() - 1);
        }
        list.add(state);
        current++;
        if (list.size() > MAX_HISTORY) {
            current--;
            list.remove(0);
        }
    }

    private boolean isCurrent(@NonNull HistoryState state) {
        final HistoryState current = getCurrent();
        if (current == null) {
            return false;
        }
        return current.same(state);
    }

    @Nullable
    public HistoryState getCurrent() {
        Check.isMainThread();
        if (current == -1) {
            return null;
        }
        return list.get(current);
    }

    @Nullable
    public HistoryState redo() {
        Check.isMainThread();
        if (current < list.size() - 1) {
            current++;
        }
        return getCurrent();
    }

    @Nullable
    public HistoryState undo() {
        Check.isMainThread();
        if (current == -1) {
            return null;
        }
        if (current > 0) {
            current--;
            return getCurrent();
        }
        return null;
    }


    public void clear() {
        Check.isMainThread();
        list.clear();
        current = -1;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    @NonNull
    public List<HistoryState> asList() {
        return Collections.unmodifiableList(list);
    }
}
