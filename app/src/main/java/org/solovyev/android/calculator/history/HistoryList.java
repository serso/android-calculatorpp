package org.solovyev.android.calculator.history;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.Check;

import java.util.ArrayList;
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

    public void addAll(@NonNull List<HistoryState> states) {
        for (HistoryState state : states) {
            add(state);
        }
    }

    public void remove(@NonNull HistoryState state) {
        Check.isMainThread();
        for (int i = 0; i < list.size(); i++) {
            final HistoryState candidate = list.get(i);
            if (candidate.same(state)) {
                list.remove(i);
                if (current >= i) {
                    current--;
                }
                break;
            }
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

    @NonNull
    public static JSONArray toJson(@NonNull List<HistoryState> states) {
        final JSONArray array = new JSONArray();
        for (int i = 0; i < states.size(); i++) {
            final HistoryState state = states.get(i);
            try {
                array.put(i, state.toJson());
            } catch (JSONException e) {
                Log.e(History.TAG, e.getMessage(), e);
            }
        }
        return array;
    }

    @NonNull
    public static List<HistoryState> fromJson(@NonNull JSONArray array) {
        final List<HistoryState> states = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            final JSONObject json = array.optJSONObject(i);
            if (json == null) {
                continue;
            }
            try {
                states.add(HistoryState.create(json));
            } catch (JSONException e) {
                Log.e(History.TAG, e.getMessage(), e);
            }
        }
        return states;
    }
}
