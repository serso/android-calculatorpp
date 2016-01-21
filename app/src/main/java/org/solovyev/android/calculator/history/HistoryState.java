package org.solovyev.android.calculator.history;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.DisplayState;
import org.solovyev.android.calculator.EditorState;
import org.solovyev.android.calculator.json.Json;
import org.solovyev.android.calculator.json.Jsonable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static android.text.TextUtils.isEmpty;

public class HistoryState implements Parcelable, Jsonable {

    public static final Creator<HistoryState> CREATOR = new Creator<HistoryState>() {
        @Override
        public HistoryState createFromParcel(Parcel in) {
            return new HistoryState(in);
        }

        @Override
        public HistoryState[] newArray(int size) {
            return new HistoryState[size];
        }
    };
    public static final Json.Creator<HistoryState> JSON_CREATOR = new Json.Creator<HistoryState>() {
        @NonNull
        @Override
        public HistoryState create(@NonNull JSONObject json) throws JSONException {
            return new HistoryState(json);
        }
    };
    private static final String JSON_EDITOR = "e";
    private static final String JSON_DISPLAY = "d";
    private static final String JSON_TIME = "t";
    private static final String JSON_COMMENT = "c";
    public final int id;
    @Nonnull
    public final EditorState editor;
    @Nonnull
    public final DisplayState display;
    protected long time = now();
    @Nonnull
    protected String comment = "";

    private HistoryState(@Nonnull EditorState editor, @Nonnull DisplayState display) {
        this.id = System.identityHashCode(this);
        this.editor = editor;
        this.display = display;
    }

    private HistoryState(@Nonnull HistoryState state, boolean newState) {
        this.id = newState ? System.identityHashCode(this) : state.id;
        this.editor = state.editor;
        this.display = state.display;
        this.time = state.time;
        this.comment = state.comment;
    }

    private HistoryState(@Nonnull JSONObject json) throws JSONException {
        this(EditorState.create(json.getJSONObject(JSON_EDITOR)), DisplayState.create(json.getJSONObject(JSON_DISPLAY)));
        this.time = json.optLong(JSON_TIME, 0L);
        this.comment = json.optString(JSON_COMMENT, "");
    }

    private HistoryState(Parcel in) {
        id = in.readInt();
        editor = in.readParcelable(EditorState.class.getClassLoader());
        display = in.readParcelable(DisplayState.class.getClassLoader());
        time = in.readLong();
        comment = in.readString();
    }

    @Nonnull
    public static Builder builder(@Nonnull EditorState editor, @Nonnull DisplayState display) {
        return new Builder(editor, display);
    }

    @Nonnull
    public static Builder builder(@Nonnull HistoryState state, boolean newState) {
        return new Builder(state, newState);
    }

    @Nonnull
    public static HistoryState create(@Nonnull JSONObject json) throws JSONException {
        return new HistoryState(json);
    }

    private static long now() {
        return System.currentTimeMillis();
    }

    @Nonnull
    public JSONObject toJson() throws JSONException {
        final JSONObject json = new JSONObject();
        json.put(JSON_EDITOR, editor.toJson());
        json.put(JSON_DISPLAY, display.toJson());
        json.put(JSON_TIME, time);
        if (!isEmpty(comment)) {
            json.put(JSON_COMMENT, comment);
        }
        return json;
    }

    @Nonnull
    public EditorState getEditor() {
        return editor;
    }

    @Nonnull
    public DisplayState getDisplay() {
        return display;
    }

    public long getTime() {
        return time;
    }

    @Nonnull
    public String getComment() {
        return comment;
    }

    public boolean same(@Nonnull HistoryState that) {
        return this.editor.same(that.editor) && this.display.same(that.display);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof HistoryState)) return false;

        final HistoryState that = (HistoryState) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "HistoryState{" +
                "id=" + id +
                ", editor=" + editor +
                ", display=" + display +
                ", time=" + time +
                ", comment='" + comment + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(editor, flags);
        dest.writeParcelable(display, flags);
        dest.writeLong(time);
        dest.writeString(comment);
    }

    public static final class Builder {

        @NonNull
        private final HistoryState state;
        private boolean built;

        private Builder(@Nonnull EditorState editor, @Nonnull DisplayState display) {
            this.state = new HistoryState(editor, display);
        }

        private Builder(@Nonnull HistoryState state, boolean newState) {
            this.state = new HistoryState(state, newState);
            if (newState) {
                withTime(now());
            }
        }

        @Nonnull
        public Builder withTime(long time) {
            Check.isTrue(!built);
            state.time = time;
            return this;
        }

        @Nonnull
        public Builder withComment(@Nullable String comment) {
            Check.isTrue(!built);
            state.comment = comment == null ? "" : comment;
            return this;
        }

        @Nonnull
        public HistoryState build() {
            built = true;
            return state;
        }
    }
}
