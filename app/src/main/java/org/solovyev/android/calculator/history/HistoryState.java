package org.solovyev.android.calculator.history;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.DisplayState;
import org.solovyev.android.calculator.EditorState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static android.text.TextUtils.isEmpty;

public class HistoryState implements Parcelable {

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
    private static final String JSON_EDITOR = "e";
    private static final String JSON_DISPLAY = "d";
    private static final String JSON_TIME = "t";
    private static final String JSON_COMMENT = "c";
    @Nonnull
    public final EditorState editor;
    @Nonnull
    public final DisplayState display;
    protected long time;
    @Nonnull
    protected String comment = "";

    private HistoryState(@Nonnull EditorState editor, @Nonnull DisplayState display) {
        this.editor = editor;
        this.display = display;
    }

    private HistoryState(@Nonnull JSONObject json) throws JSONException {
        this(EditorState.create(json.getJSONObject(JSON_EDITOR)), DisplayState.create(json.getJSONObject(JSON_DISPLAY)));
        this.time = json.optLong(JSON_TIME, 0L);
        this.comment = json.optString(JSON_COMMENT, "");
    }

    private HistoryState(Parcel in) {
        editor = in.readParcelable(EditorState.class.getClassLoader());
        display = in.readParcelable(DisplayState.class.getClassLoader());
        time = in.readLong();
        comment = in.readString();
    }

    @Nonnull
    public static Builder newBuilder(@Nonnull EditorState editor, @Nonnull DisplayState display) {
        return new Builder(editor, display);
    }

    @Nonnull
    public static HistoryState create(@Nonnull JSONObject json) throws JSONException {
        return new HistoryState(json);
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
    public String toString() {
        return "HistoryState{" +
                "editor=" + editor +
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
        dest.writeParcelable(editor, flags);
        dest.writeParcelable(display, flags);
        dest.writeLong(time);
        dest.writeString(comment);
    }

    @SuppressLint("ParcelCreator")
    public static final class Builder extends HistoryState {

        private boolean built;

        private Builder(@Nonnull EditorState editor, @Nonnull DisplayState display) {
            super(editor, display);
            withTime(System.currentTimeMillis());
        }

        @Nonnull
        public Builder withTime(long time) {
            Check.isTrue(!built);
            this.time = time;
            return this;
        }

        @Nonnull
        public Builder withComment(@Nullable String comment) {
            Check.isTrue(!built);
            this.comment = comment == null ? "" : comment;
            return this;
        }

        @Nonnull
        public HistoryState build() {
            built = true;
            return this;
        }
    }
}
