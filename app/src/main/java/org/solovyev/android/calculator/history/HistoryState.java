package org.solovyev.android.calculator.history;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.DisplayState;
import org.solovyev.android.calculator.EditorState;

import javax.annotation.Nonnull;

public class HistoryState {

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
        if (!TextUtils.isEmpty(comment)) {
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

    public static final class Builder extends HistoryState {

        private boolean built;

        private Builder(@Nonnull EditorState editor, @Nonnull DisplayState display) {
            super(editor, display);
            setTime(System.currentTimeMillis());
        }

        public void setTime(long time) {
            Check.isTrue(!built);
            this.time = time;
        }

        public void setComment(@Nonnull String comment) {
            Check.isTrue(!built);
            this.comment = comment;
        }

        @Nonnull
        public HistoryState build() {
            built = true;
            return this;
        }
    }
}
