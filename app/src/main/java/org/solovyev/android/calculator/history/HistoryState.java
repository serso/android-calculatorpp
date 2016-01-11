package org.solovyev.android.calculator.history;

import android.support.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.DisplayState;
import org.solovyev.android.calculator.EditorState;

import javax.annotation.Nonnull;

public class HistoryState {

    @Nonnull
    private static final String JSON_EDITOR = "e";
    @Nonnull
    private static final String JSON_DISPLAY = "d";
    @Nonnull
    public final EditorState editor;
    @Nonnull
    public final DisplayState display;
    protected long time;
    @Nullable
    protected String comment;

    private HistoryState(@Nonnull EditorState editor, @Nonnull DisplayState display) {
        this.editor = editor;
        this.display = display;
    }

    private HistoryState(@Nonnull JSONObject json) throws JSONException {
        this(EditorState.create(json.getJSONObject(JSON_EDITOR)), DisplayState.create(json.getJSONObject(JSON_DISPLAY)));
    }

    @Nonnull
    public static Builder newBuilder(@Nonnull EditorState editor, @Nonnull DisplayState display) {
        return new Builder(editor, display);
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

    @Nullable
    public String getComment() {
        return comment;
    }

    public boolean same(@Nonnull HistoryState that) {
        return this.editor.same(that.editor) && this.display.same(that.display);
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

        public void setComment(@Nullable String comment) {
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
